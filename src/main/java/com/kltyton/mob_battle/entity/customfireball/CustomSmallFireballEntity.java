package com.kltyton.mob_battle.entity.customfireball;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class CustomSmallFireballEntity extends SmallFireball {
    private final float damage;
    private final boolean createsFire;

    public CustomSmallFireballEntity(Level world, LivingEntity owner, float damage) {
        this(world, owner, damage, true);
    }

    public CustomSmallFireballEntity(Level world, LivingEntity owner, float damage, boolean createsFire) {
        super(world, owner, Vec3.ZERO);
        this.damage = damage;
        this.createsFire = createsFire;
        this.setNoGravity(true);
    }

    /**
     * 返回该小火球是否允许原版实体着火和方块起火逻辑。
     */
    public boolean createsFire() {
        return this.createsFire;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (this.createsFire) {
            super.onHitBlock(hitResult);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverWorld) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            if (entity instanceof LivingEntity living && !EntityQueries.isValidSummonCombatTarget(this, entity2, living)) {
                return;
            }
            int i = entity.getRemainingFireTicks();
            if (this.createsFire) {
                entity.igniteForSeconds(5.0F);
            }
            DamageSource damageSource = this.damageSources().fireball(this, entity2);
            if (!entity.hurtServer(serverWorld, damageSource, damage)) {
                if (this.createsFire) {
                    entity.setRemainingFireTicks(i);
                }
            } else {
                EnchantmentHelper.doPostAttackEffects(serverWorld, entity, damageSource);
            }
        }
    }
}
