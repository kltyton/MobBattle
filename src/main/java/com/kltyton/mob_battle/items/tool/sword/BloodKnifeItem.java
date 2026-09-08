package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.animation.PalMorePlayerAnimationServerHandler;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class BloodKnifeItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "blood_knife";
    public static final int COOLDOWN_TICKS = 30 * 20;
    private static final int FALLBACK_RELEASE_TICKS = 14;
    private static final Map<MinecraftServer, Map<UUID, Long>> PENDING_RELEASES = new IdentityHashMap<>();
    private static long nextPendingToken;

    public BloodKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return;
        }
        if (!isServer || !(player.level() instanceof ServerLevel)) {
            StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.level().getServer();
            if (server == null) {
                return;
            }
            long token = ++nextPendingToken;
            pendingFor(server).put(serverPlayer.getUUID(), token);
            PalMorePlayerAnimationServerHandler.play(serverPlayer, ModPlayerAnimationIds.BLOOD_KNIFE);
            ServerTickScheduler.schedule(server, FALLBACK_RELEASE_TICKS,
                    () -> releasePendingSkill(serverPlayer, token));
        }
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {
        StackBoundCooldowns.ensureGroup(stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    /** 仅消费当前玩家在当前服务器上的一条 pending，并返回是否实际释放。 */
    public static boolean releasePendingSkill(ServerPlayer player) {
        return releasePendingSkill(player, null);
    }

    private static boolean releasePendingSkill(ServerPlayer player, Long expectedToken) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return false;
        }
        Map<UUID, Long> pending = PENDING_RELEASES.get(server);
        Long token = pending == null ? null : pending.get(player.getUUID());
        if (token == null || expectedToken != null && !expectedToken.equals(token)) {
            return false;
        }
        pending.remove(player.getUUID());
        if (pending.isEmpty()) {
            PENDING_RELEASES.remove(server);
        }
        if (server.getPlayerList().getPlayer(player.getUUID()) != player
                || !(player.level() instanceof ServerLevel world)) {
            return false;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BloodKnifeItem)) {
            return false;
        }
        for (float yawOffset : new float[]{-10.0F, 0.0F, 10.0F}) {
            SkillProjectileEntity projectile = ModEntities.BLOOD_SWORD_ENERGY.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (projectile == null) {
                continue;
            }
            Vec3 direction = Vec3.directionFromRotation(player.getXRot(), player.getYRot() + yawOffset).normalize();
            Vec3 start = player.getEyePosition().add(direction.scale(0.8D));
            projectile.configure(player, start, direction.scale(0.85D), 100.0F, 0.0F, true, true, false, 45);
            projectile.setOwnerHealOnHit(0.2F);
            world.addFreshEntity(projectile);
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.7F);
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(4, player, InteractionHand.MAIN_HAND);
        }
        return true;
    }

    /** 清除指定服务器上指定玩家的 pending，供断线生命周期调用。 */
    public static void clearPending(MinecraftServer server, UUID playerId) {
        Map<UUID, Long> pending = PENDING_RELEASES.get(server);
        if (pending == null) {
            return;
        }
        pending.remove(playerId);
        if (pending.isEmpty()) {
            PENDING_RELEASES.remove(server);
        }
    }

    /** 清除指定服务器上指定玩家的未释放动画状态，供断线生命周期调用。 */
    public static void clearAllPending(MinecraftServer server) {
        PENDING_RELEASES.remove(server);
    }

    private static Map<UUID, Long> pendingFor(MinecraftServer server) {
        return PENDING_RELEASES.computeIfAbsent(server, ignored -> new HashMap<>());
    }
}
