package com.kltyton.mob_battle.entity.littleperson.zombie.infected;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.littleperson.zombie.ai.ZombieLittlePersonTargets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** 感染守卫保留原型攻击、格挡和护佑行为，仅把固有免伤改为 60%。 */
public final class ZombieLittlePersonGuardEntity extends LittlePersonGuardEntity implements ZombieLittlePerson {
    public ZombieLittlePersonGuardEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder attributes() {
        return createLittlePersonGuardAttributes().add(ModEntityAttributes.DAMAGE_REDUCTION, 0.60D);
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
