package com.kltyton.mob_battle.items.tool.moneygun;

import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.animation.ModPlayerAnimationServerHandler;
import com.kltyton.mob_battle.entity.projectile.MoneyGunProjectileEntity;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.sounds.ModSounds;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * 撒币枪：灾厄货币半自动、绿宝石全自动和绿宝石散弹三种模式。
 */
public final class MoneyGunItem extends Item implements ModFabricItem {
    private static final String LAST_SHOT_TICK_KEY = "MoneyGunLastShotTick";
    // Shoot2 的连续后坐关键帧约每 0.21-0.25 秒回到基准姿势，按 20 TPS 取 5 tick。
    private static final int SHOT_INTERVAL_TICKS = 5;
    private static final int SHOTGUN_COOLDOWN_TICKS = 20 * 20;
    private static final int SHOTGUN_PROJECTILES = 10;
    private static final Set<Player> AUTOMATIC_PLAYERS = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<Player> ANIMATING_PLAYERS = Collections.newSetFromMap(new WeakHashMap<>());

    public MoneyGunItem(Properties properties) {
        super(properties);
    }

    public static void initLifecycle() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            AUTOMATIC_PLAYERS.remove(handler.player);
            ANIMATING_PLAYERS.remove(handler.player);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            AUTOMATIC_PLAYERS.clear();
            ANIMATING_PLAYERS.clear();
        });
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            if (!level.isClientSide()) {
                float remaining = player.getCooldowns().getCooldownPercent(stack, 0.0F) * SHOTGUN_COOLDOWN_TICKS / 20.0F;
                player.sendOverlayMessage(Component.translatable(
                        "message.mob_battle.money_gun_cooldown",
                        String.format("%.1f", remaining)
                ));
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            stopAutomatic(player);
            MoneyGunModes.Mode mode = MoneyGunModes.next(stack);
            player.sendOverlayMessage(Component.translatable(mode.translationKey()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (!isServer || !(player.level() instanceof ServerLevel level) || player.getCooldowns().isOnCooldown(stack)) {
            return;
        }
        switch (MoneyGunModes.getMode(stack)) {
            case ILLAGER_SEMI -> fireSemiAutomatic(level, player, stack);
            case EMERALD_AUTO -> {
                AUTOMATIC_PLAYERS.add(player);
                ANIMATING_PLAYERS.add(player);
                ModPlayerAnimationServerHandler.play((ServerPlayer) player, ModPlayerAnimationIds.MONEY_GUN_SHOOT);
                fireAutomatic(level, player, stack);
            }
            case EMERALD_SHOTGUN -> fireShotgun(level, player, stack);
        }
    }

    @Override
    public void onLeftClickStop(Player player, ItemStack stack, boolean isServer) {
        if (isServer) {
            stopAutomatic(player);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, EquipmentSlot slot) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Player player)
                || !AUTOMATIC_PLAYERS.contains(player) && !ANIMATING_PLAYERS.contains(player)) {
            return;
        }
        if (!player.isAlive() || !player.getMainHandItem().is(this)) {
            stopAutomatic(player);
            return;
        }
        if (player.getMainHandItem() == stack && MoneyGunModes.getMode(stack) == MoneyGunModes.Mode.EMERALD_AUTO) {
            fireAutomatic(serverLevel, player, stack);
        }
    }

    private static void fireSemiAutomatic(ServerLevel level, Player player, ItemStack stack) {
        if (!canFireNow(level, stack)) {
            return;
        }
        if (!consume(player, ModItems.ILLAGER_CURRENCY, 1)) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.money_gun_no_ammo"));
            return;
        }
        spawnProjectile(level, player, stack, new ItemStack(ModItems.ILLAGER_CURRENCY), 70.0F, false, 0.0F, 0.0F, 0.0F);
        markFired(level, stack);
        playSemiAnimation((ServerPlayer) player);
    }

    private static void fireAutomatic(ServerLevel level, Player player, ItemStack stack) {
        if (!canFireNow(level, stack)) {
            return;
        }
        if (!consume(player, Items.EMERALD, 1)) {
            stopAutomatic(player);
            player.sendOverlayMessage(Component.translatable("message.mob_battle.money_gun_no_ammo"));
            return;
        }
        spawnProjectile(level, player, stack, new ItemStack(Items.EMERALD), 95.0F, false, 0.0F, 0.0F, 0.0F);
        markFired(level, stack);
    }

    private static void fireShotgun(ServerLevel level, Player player, ItemStack stack) {
        if (!consume(player, Items.EMERALD, 10)) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.money_gun_no_ammo"));
            return;
        }
        for (int i = 0; i < SHOTGUN_PROJECTILES; i++) {
            float yawOffset = (i - (SHOTGUN_PROJECTILES - 1) / 2.0F) * 4.0F;
            float pitchOffset = i % 2 == 0 ? -2.0F : 2.0F;
            spawnProjectile(level, player, stack, new ItemStack(Items.EMERALD), 15.0F, true, 0.35F, pitchOffset, yawOffset);
        }
        player.getCooldowns().addCooldown(stack, SHOTGUN_COOLDOWN_TICKS);
        markFired(level, stack);
        playSemiAnimation((ServerPlayer) player);
    }

    private static void spawnProjectile(
            ServerLevel level,
            Player player,
            ItemStack weapon,
            ItemStack visual,
            float damage,
            boolean bypassInvulnerability,
            float knockback,
            float pitchOffset,
            float yawOffset
    ) {
        MoneyGunProjectileEntity projectile = new MoneyGunProjectileEntity(
                level,
                player,
                visual,
                damage,
                bypassInvulnerability,
                knockback
        );
        projectile.setPos(player.getEyePosition().add(player.getViewVector(1.0F).scale(0.6D)));
        VsSnipe.shootLikeSniper(projectile, player, pitchOffset, yawOffset);
        level.addFreshEntity(projectile);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.GUN_SHOT_SOUND_EVENT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static boolean canFireNow(ServerLevel level, ItemStack stack) {
        return level.getGameTime() - getLastShotTick(stack) >= SHOT_INTERVAL_TICKS;
    }

    private static long getLastShotTick(ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return data.getLongOr(LAST_SHOT_TICK_KEY, Long.MIN_VALUE / 2L);
    }

    private static void markFired(ServerLevel level, ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        data.putLong(LAST_SHOT_TICK_KEY, level.getGameTime());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    private static boolean consume(Player player, Item item, int count) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        int available = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack candidate = player.getInventory().getItem(i);
            if (candidate.is(item)) {
                available += candidate.getCount();
            }
        }
        if (available < count) {
            return false;
        }
        int remaining = count;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack candidate = player.getInventory().getItem(i);
            if (!candidate.is(item)) continue;
            int consumed = Math.min(remaining, candidate.getCount());
            candidate.shrink(consumed);
            remaining -= consumed;
        }
        return true;
    }

    private static void playSemiAnimation(ServerPlayer player) {
        ANIMATING_PLAYERS.add(player);
        ModPlayerAnimationServerHandler.play(player, ModPlayerAnimationIds.MONEY_GUN_SHOOT);
        ServerTickScheduler.schedule(player.level().getServer(), SHOT_INTERVAL_TICKS, () -> {
            if (!AUTOMATIC_PLAYERS.contains(player) && ANIMATING_PLAYERS.remove(player)) {
                ModPlayerAnimationServerHandler.stop(player);
            }
        });
    }

    private static void stopAutomatic(Player player) {
        boolean wasActive = AUTOMATIC_PLAYERS.remove(player) | ANIMATING_PLAYERS.remove(player);
        if (wasActive && player instanceof ServerPlayer serverPlayer) {
            ModPlayerAnimationServerHandler.stop(serverPlayer);
        }
    }
}
