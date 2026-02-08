package com.kltyton.mob_battle.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class EffectExplosionBehavior extends ExplosionBehavior {
    public Entity ownerEntity;
    public EffectExplosionBehavior(Entity entity) {
        super();
        this.ownerEntity = entity;
    }
    public boolean shouldDamage(Explosion explosion, Entity entity) {
        return !ownerEntity.isTeammate(entity) && super.shouldDamage(explosion, entity);
    }

    public float getKnockbackModifier(Entity entity) {
        return ownerEntity.isTeammate(entity) ? 0.0f : super.getKnockbackModifier(entity);
    }
}
