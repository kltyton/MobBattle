package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.animation.PalMorePlayerAnimationServerHandler;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class PoisonKnifeItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "poison_knife";
    public static final int COOLDOWN_TICKS = 10 * 20;
    private static final int FALLBACK_RELEASE_TICKS = 10;
    private static final Map<MinecraftServer, Map<UUID, PendingRelease>> PENDING_RELEASES = new IdentityHashMap<>();
    private static long nextPendingToken;

    public PoisonKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 20, 2), attacker);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel) {
            if (player instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.level().getServer();
                if (server == null) {
                    return InteractionResult.FAIL;
                }
                long token = ++nextPendingToken;
                pendingFor(server).put(serverPlayer.getUUID(), new PendingRelease(hand, token));
                PalMorePlayerAnimationServerHandler.play(serverPlayer, ModPlayerAnimationIds.POISON_KNIFE);
                ServerTickScheduler.schedule(server, FALLBACK_RELEASE_TICKS,
                        () -> releasePendingSkill(serverPlayer, token));
            }
        }
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
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
        Map<UUID, PendingRelease> pending = PENDING_RELEASES.get(server);
        PendingRelease pendingRelease = pending == null ? null : pending.get(player.getUUID());
        if (pendingRelease == null
                || expectedToken != null && expectedToken != pendingRelease.token()) {
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
        InteractionHand hand = pendingRelease.hand();
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof PoisonKnifeItem)) {
            return false;
        }
        Vec3 look = player.getLookAngle().normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(4.0D)).inflate(2.0D, 1.0D, 2.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                target -> EntityQueries.isValidCombatTarget(player, target))) {
            target.invulnerableTime = 0;
            target.hurtServer(world, player.damageSources().playerAttack(player), 80.0F);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * 20, 4), player);
            target.invulnerableTime = 0;
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.8F);
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(20, player, hand);
        }
        return true;
    }

    /** 清除指定服务器上指定玩家的 pending，供断线生命周期调用。 */
    public static void clearPending(MinecraftServer server, UUID playerId) {
        Map<UUID, PendingRelease> pending = PENDING_RELEASES.get(server);
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

    private static Map<UUID, PendingRelease> pendingFor(MinecraftServer server) {
        return PENDING_RELEASES.computeIfAbsent(server, ignored -> new HashMap<>());
    }

    private record PendingRelease(InteractionHand hand, long token) {
    }
}
