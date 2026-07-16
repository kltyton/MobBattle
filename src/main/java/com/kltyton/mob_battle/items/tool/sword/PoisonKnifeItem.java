package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.animation.PalMorePlayerAnimationServerHandler;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PoisonKnifeItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "poison_knife";
    public static final int COOLDOWN_TICKS = 10 * 20;
    private static final int FALLBACK_RELEASE_TICKS = 10;
    private static final Map<UUID, InteractionHand> PENDING_RELEASES = new ConcurrentHashMap<>();

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
                PENDING_RELEASES.put(serverPlayer.getUUID(), hand);
                PalMorePlayerAnimationServerHandler.play(serverPlayer, ModPlayerAnimationIds.POISON_KNIFE);
                TaskSchedulerUtil.runLater(FALLBACK_RELEASE_TICKS, () -> releasePendingSkill(serverPlayer));
            }
        }
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {
        StackBoundCooldowns.ensureGroup(stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    public static void releasePendingSkill(ServerPlayer player) {
        InteractionHand hand = PENDING_RELEASES.remove(player.getUUID());
        if (hand == null || !(player.level() instanceof ServerLevel world)) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof PoisonKnifeItem)) {
            return;
        }
        Vec3 look = player.getLookAngle().normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(4.0D)).inflate(2.0D, 1.0D, 2.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                target -> target.isAlive() && target != player && !player.isAlliedTo(target))) {
            target.invulnerableTime = 0;
            target.hurtServer(world, player.damageSources().playerAttack(player), 80.0F);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * 20, 4), player);
            target.invulnerableTime = 0;
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.8F);
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(20, player, hand);
        }
    }
}
