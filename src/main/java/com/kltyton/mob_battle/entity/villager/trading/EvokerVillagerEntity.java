package com.kltyton.mob_battle.entity.villager.trading;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;

public final class EvokerVillagerEntity extends CommandTradeVillagerEntity {
    public EvokerVillagerEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean validTrade) {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void playCelebrateSound() {
        this.makeSound(SoundEvents.EVOKER_CELEBRATE);
    }

    @Override
    public void playWorkSound() {
        this.makeSound(SoundEvents.EVOKER_CAST_SPELL);
    }
}
