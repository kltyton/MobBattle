package com.kltyton.mob_battle.items.control;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LittlePersonScepterItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "little_person_scepter";
    private static final int COOLDOWN_TICKS = 20 * 20;
    private static final int SUMMON_LIFE_TICKS = 20 * 20;
    private static final int SUMMON_COUNT = 2;
    private static final int SUMMON_DURABILITY_COST = 4;
    private static final int LIGHTNING_DURABILITY_COST = 2;
    private static final float LIGHTNING_DAMAGE = 100.0F;
    private static final double LIGHTNING_RANGE = 40.0D;
    private static final double LIGHTNING_HIT_RADIUS = 4.0D;
    private static final double LIGHTNING_AIM_RADIUS = 2.5D;

    public LittlePersonScepterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return InteractionResult.FAIL;
        }
        if (!(level instanceof ServerLevel serverWorld)) {
            StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
            return InteractionResult.SUCCESS;
        }

        LivingEntity target = findLookTarget(serverWorld, player);
        summonGuards(serverWorld, player, target);
        damageAndCooldown(player, stack, hand, SUMMON_DURABILITY_COST);
        serverWorld.playSound(null, player.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return;
        }
        if (!isServer || !(player.level() instanceof ServerLevel serverWorld)) {
            StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
            return;
        }

        strikeLightning(serverWorld, player);
        damageAndCooldown(player, stack, InteractionHand.MAIN_HAND, LIGHTNING_DURABILITY_COST);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {
        StackBoundCooldowns.ensureGroup(stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    private static void summonGuards(ServerLevel world, Player player, LivingEntity target) {
        Vec3 playerPos = player.position();
        for (int i = 0; i < SUMMON_COUNT; i++) {
            LittlePersonGuardEntity guard = ModEntities.LITTLE_PERSON_GUARD.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (guard == null) {
                continue;
            }

            double angle = world.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = 1.5D + world.getRandom().nextDouble() * 2.0D;
            Vec3 desiredPos = playerPos.add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
            Vec3 spawnPos = EntityQueries.findSafeSpawnPosition(world, guard, desiredPos).orElse(desiredPos);
            BlockPos blockPos = BlockPos.containing(spawnPos);

            guard.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, world.getRandom().nextFloat() * 360.0F, 0.0F);
            guard.finalizeSpawn(world, world.getCurrentDifficultyAt(blockPos), EntitySpawnReason.MOB_SUMMONED, null);
            guard.setLife(SUMMON_LIFE_TICKS);
            guard.setSummonOwner(player);
            EntityQueries.joinSameTeam(guard, player);
            if (target != null && EntityQueries.isValidSummonCombatTarget(guard, player, target)) {
                guard.setTarget(target);
            }
            world.addFreshEntity(guard);
        }

        world.sendParticles(ParticleTypes.POOF, playerPos.x, playerPos.y + 1.0D, playerPos.z, 30, 1.0D, 0.7D, 1.0D, 0.1D);
    }

    private static void strikeLightning(ServerLevel world, Player player) {
        LivingEntity directTarget = findLookTarget(world, player);
        Vec3 lightningPos = directTarget == null
                ? player.getEyePosition().add(player.getLookAngle().normalize().scale(6.0D))
                : directTarget.position();

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.EVENT);
        if (lightning != null) {
            lightning.setVisualOnly(true);
            lightning.setPos(lightningPos.x, lightningPos.y, lightningPos.z);
            world.addFreshEntity(lightning);
        }

        AABB hitBox = new AABB(lightningPos, lightningPos).inflate(LIGHTNING_HIT_RADIUS);
        double radiusSq = LIGHTNING_HIT_RADIUS * LIGHTNING_HIT_RADIUS;
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, hitBox,
                target -> EntityQueries.isValidSummonCombatTarget(player, player, target)
                        && target.distanceToSqr(lightningPos) <= radiusSq)) {
            target.invulnerableTime = 0;
            target.hurtServer(world, target.damageSources().lightningBolt(), LIGHTNING_DAMAGE);
            target.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 3 * 20, 1), player);
        }

        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, lightningPos.x, lightningPos.y + 0.5D, lightningPos.z,
                60, 0.6D, 0.8D, 0.6D, 0.2D);
        world.playSound(null, BlockPos.containing(lightningPos), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static LivingEntity findLookTarget(ServerLevel world, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(LIGHTNING_RANGE)).inflate(LIGHTNING_HIT_RADIUS);
        LivingEntity closest = null;
        double closestPerpendicularSq = Double.MAX_VALUE;
        double closestForward = Double.MAX_VALUE;
        double maxPerpendicularSq = LIGHTNING_AIM_RADIUS * LIGHTNING_AIM_RADIUS;

        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, searchBox,
                target -> EntityQueries.isValidSummonCombatTarget(player, player, target))) {
            Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 toTarget = targetCenter.subtract(eye);
            double forward = toTarget.dot(look);
            if (forward <= 0.0D || forward > LIGHTNING_RANGE) {
                continue;
            }
            double perpendicularSq = Math.max(0.0D, toTarget.lengthSqr() - forward * forward);
            if (perpendicularSq > maxPerpendicularSq) {
                continue;
            }
            if (perpendicularSq < closestPerpendicularSq
                    || perpendicularSq == closestPerpendicularSq && forward < closestForward) {
                closest = target;
                closestPerpendicularSq = perpendicularSq;
                closestForward = forward;
            }
        }

        return closest;
    }

    private static void damageAndCooldown(Player player, ItemStack stack, InteractionHand hand, int durabilityCost) {
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(durabilityCost, player, hand);
        }
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }
}
