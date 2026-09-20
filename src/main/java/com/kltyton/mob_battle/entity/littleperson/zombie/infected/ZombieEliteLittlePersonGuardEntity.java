package com.kltyton.mob_battle.entity.littleperson.zombie.infected;

import com.kltyton.mob_battle.entity.littleperson.skillentity.requested.EliteLittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.littleperson.zombie.ai.ZombieLittlePersonTargets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** 感染大护卫保持原型技能、出生流程与回血，最大生命值为 1000。 */
public final class ZombieEliteLittlePersonGuardEntity extends EliteLittlePersonGuardEntity implements ZombieLittlePerson {
    private int remainingLifetime = -1;

    public ZombieEliteLittlePersonGuardEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder attributes() {
        return createLittlePersonAttributes().add(Attributes.MAX_HEALTH, 1000.0D);
    }

    /** 皇族的批量护卫召唤保留 60 秒寿命，单体大护卫召唤不设置寿命。 */
    public void setRemainingLifetime(int ticks) {
        this.remainingLifetime = Math.max(-1, ticks);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.remainingLifetime >= 0 && --this.remainingLifetime <= 0) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ZombieGuardLifetime", this.remainingLifetime);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.remainingLifetime = Math.max(-1, input.getIntOr("ZombieGuardLifetime", -1));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ZombieLittlePersonTargets.install(this, this.targetSelector);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.ZOMBIE_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ZOMBIE_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ZOMBIE_DEATH; }
}
