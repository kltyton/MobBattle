package com.kltyton.mob_battle.entity.customfireball;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class CustomSmallFireballEntity extends SmallFireball {
    private final float damage;

    public CustomSmallFireballEntity(Level world, LivingEntity owner, float damage) {
        super(world, owner, Vec3.ZERO);
        this.damage = damage;
        this.setNoGravity(true);
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverWorld) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, entity2, living)) {
                return;
            }
            int i = entity.getRemainingFireTicks();
            entity.igniteForSeconds(5.0F);
            DamageSource damageSource = this.damageSources().fireball(this, entity2);
            if (!entity.hurtServer(serverWorld, damageSource, damage)) {
                entity.setRemainingFireTicks(i);
            } else {
                EnchantmentHelper.doPostAttackEffects(serverWorld, entity, damageSource);
            }
        }
    }
}
