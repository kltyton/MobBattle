package com.kltyton.mob_battle.entity.littleperson.zombie.infected;

import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePersonSpawning;
import com.kltyton.mob_battle.entity.littleperson.zombie.ai.ZombieLittlePersonTargets;
import com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 感染皇族保留原型阶段与技能，免伤上限 80%，召唤对象统一为感染大护卫。 */
public final class ZombieLittlePersonKingEntity extends LittlePersonKingEntity implements ZombieLittlePerson {
    public ZombieLittlePersonKingEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    public void heal() { this.heal(1.0F); }

    @Override
    protected double damageReductionCap() { return 0.80D; }

    @Override
    public void summonGuards(int count) {
        if (this.level().isClientSide()) {
            return;
        }
        for (int index = 0; index < count; index++) {
            double angle = Math.PI * 2.0D * index / count;
            Vec3 position = this.position().add(Math.cos(angle) * 2.5D, 0.0D, Math.sin(angle) * 2.5D);
            ZombieEliteLittlePersonGuardEntity guard = ZombieLittlePersonSpawning.summon(
                    LittlePersonZombieEntityTypes.ELITE_GUARD, this, position);
            if (guard != null) {
                guard.setRemainingLifetime(1200);
            }
        }
    }

    @Override
    public void summonEliteGuard() {
        ZombieLittlePersonSpawning.summon(LittlePersonZombieEntityTypes.ELITE_GUARD,
                this, this.position().add(this.getViewVector(1.0F).scale(2.0D)));
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
