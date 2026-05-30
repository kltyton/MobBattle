package com.kltyton.mob_battle.explosion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

public class EffectExplosionBehavior extends ExplosionDamageCalculator {
    public Entity ownerEntity;
    public EffectExplosionBehavior(Entity entity) {
        super();
        this.ownerEntity = entity;
    }
    public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
        return !ownerEntity.isAlliedTo(entity) && super.shouldDamageEntity(explosion, entity);
    }

    public float getKnockbackMultiplier(Entity entity) {
        return ownerEntity.isAlliedTo(entity) ? 0.0f : super.getKnockbackMultiplier(entity);
    }
}
