package com.kltyton.mob_battle.entity.villager.trading;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class WitherSkeletonVillagerEntity extends CommandTradeVillagerEntity {
    public WitherSkeletonVillagerEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.WITHER_SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean validTrade) {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    public void playCelebrateSound() {
        this.makeSound(SoundEvents.WITHER_SKELETON_AMBIENT);
    }

    @Override
    public void playWorkSound() {
        this.makeSound(SoundEvents.WITHER_SKELETON_AMBIENT);
    }
}
