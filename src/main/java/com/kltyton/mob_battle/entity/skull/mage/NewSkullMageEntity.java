package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.RequestedLittlePersonEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NewSkullMageEntity extends RequestedLittlePersonEntity {
    public NewSkullMageEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 2);
        this.healPerSecond = 1.0F;
        this.autoSkillRange = 40.0D;
        setCooldownSeconds(50, 20);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createNewSkullMageAttributes() {
        return createRequestedAttributes(100.0D, 8.0D, 0.25D, 40.0D, 0.0D);
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        return !this.hasSkill() && this.distanceTo(target) <= 2.5D;
    }

    @Override
    protected double skillRange(String skillName) {
        return switch (skillName) {
            case "attack2" -> 16.0D;
            case "attack3" -> 40.0D;
            default -> super.skillRange(skillName);
        };
    }

    @Override
    protected void runAttack() {
        damageTarget(8.0F, 0.0F);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !isOwnSummon(target) && super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target != null && isOwnSummon(target) ? null : target);
    }

    private boolean isOwnSummon(LivingEntity target) {
        return EntityQueries.getKnownOwner(target) == this;
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> summonSkeletons(this, this.getTarget(), this.position(), 2, 2);
            case 3 -> {
                shootSkillProjectile(ModEntities.SKELETON_HEAD_PROJECTILE, 20.0F, 0.0F, 1.25D, 90, false, false, true, 3.0D);
                this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 0.9F);
            }
            default -> {
            }
        }
    }

    public static void summonSkeletons(LivingEntity owner, LivingEntity target, Vec3 center, int meleeCount, int archerCount) {
        if (!(owner.level() instanceof ServerLevel world)) {
            return;
        }
        for (int i = 0; i < meleeCount; i++) {
            spawnSummonedSkeleton(world, owner, target, center, true, i);
        }
        for (int i = 0; i < archerCount; i++) {
            spawnSummonedSkeleton(world, owner, target, center, false, i + meleeCount);
        }
    }

    private static void spawnSummonedSkeleton(ServerLevel world, LivingEntity owner, LivingEntity target, Vec3 center, boolean melee, int index) {
        SummonedSkeletonEntity skeleton = ModEntities.SUMMONED_SKELETON.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (skeleton == null) {
            return;
        }
        double angle = index * Math.PI * 0.5D;
        Vec3 fallback = center.add(Math.cos(angle) * 2.0D, 0.0D, Math.sin(angle) * 2.0D);
        Vec3 spawnPos = EntityQueries.findSafeSpawnPosition(world, skeleton, fallback).orElse(fallback);
        skeleton.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, owner.getYRot(), 0.0F);
        skeleton.setSummonOwner(owner);
        skeleton.setItemSlot(EquipmentSlot.MAINHAND, melee ? new ItemStack(Items.IRON_SWORD) : new ItemStack(Items.BOW));
        skeleton.reassessWeaponGoal();
        if (target != null && EntityQueries.isValidSummonCombatTarget(skeleton, owner, target)) {
            skeleton.setTarget(target);
        }
        world.addFreshEntity(skeleton);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel world, DamageSource source, boolean killedByPlayer) {
        super.dropCustomDeathLoot(world, source, killedByPlayer);
        this.spawnAtLocation(world, new ItemStack(ModItems.SKULL_MAGE_SCROLL, 1 + this.random.nextInt(2)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }
}
