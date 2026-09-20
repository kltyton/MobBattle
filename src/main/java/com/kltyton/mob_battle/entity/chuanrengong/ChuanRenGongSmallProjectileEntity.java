package com.kltyton.mob_battle.entity.chuanrengong;

import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/** 传仁工小型弹体；仅 attack2 的实例携带额外击退。 */
public final class ChuanRenGongSmallProjectileEntity extends SkillProjectileEntity {
    private final Set<Integer> knockedEntities = new HashSet<>();
    private double knockbackStrength;

    public ChuanRenGongSmallProjectileEntity(EntityType<? extends ChuanRenGongSmallProjectileEntity> entityType,
                                              Level world) {
        super(entityType, world);
    }

    public void setKnockbackStrength(double strength) {
        this.knockbackStrength = Math.max(0.0D, strength);
    }

    @Override
    public void tick() {
        if (this.knockbackStrength > 0.0D && this.level() instanceof ServerLevel world) {
            Entity owner = this.getOwner();
            AABB sweep = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.45D);
            for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, sweep,
                    living -> EntityQueries.isValidSummonCombatTarget(this, owner, living))) {
                if (!this.knockedEntities.add(target.getId())) {
                    continue;
                }
                Vec3 direction = target.position().subtract(owner == null ? this.position() : owner.position());
                Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
                if (horizontal.lengthSqr() > 1.0E-4D) {
                    horizontal = horizontal.normalize();
                    target.push(horizontal.x * this.knockbackStrength, 0.3D,
                            horizontal.z * this.knockbackStrength);
                    target.hurtMarked = true;
                }
            }
        }
        super.tick();
    }
}
