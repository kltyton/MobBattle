package com.kltyton.mob_battle.entity.customfireball;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomSmallFireballEntity extends SmallFireballEntity {
    private final float damage;

    public CustomSmallFireballEntity(World world, LivingEntity owner, float damage) {
        super(world, owner, Vec3d.ZERO);
        this.damage = damage;
        this.setNoGravity(true);
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            int i = entity.getFireTicks();
            entity.setOnFireFor(5.0F);
            DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
            if (!entity.damage(serverWorld, damageSource, damage)) {
                entity.setFireTicks(i);
            } else {
                EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
            }
        }
    }
}
