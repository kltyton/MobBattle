package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DrillSilverfishEntity extends CoalSilverfishEntity {
    public DrillSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public int getCooldownTime() {
        return 20;
    }
    @Override
    public boolean canBlock() {
        return false;
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
        if (this.level().isClientSide) return;
        ServerLevel serverWorld = (ServerLevel) this.level();

        Vec3 lookDir = this.getLookAngle();
        lookDir = new Vec3(lookDir.x, 0, lookDir.z);
        if (lookDir.lengthSqr() < 1.0E-6) return;
        lookDir = lookDir.normalize();

        Vec3 wantedDash = lookDir.scale(10.0);

        Vec3 oldPos = this.position();
        AABB oldBox = this.getBoundingBox();
        Vec3 startCenter = oldBox.getCenter();

        this.move(MoverType.SELF, wantedDash);

        Vec3 actualDash = this.position().subtract(oldPos);
        if (actualDash.lengthSqr() < 0.0001) return;

        Vec3 endCenter = startCenter.add(actualDash);
        AABB hitBox = oldBox.expandTowards(actualDash).inflate(0.3);

        List<Entity> targets = serverWorld.getEntities(this, hitBox,
                target -> target instanceof LivingEntity
                        && target.isAlive()
                        && !target.isAlliedTo(this));

        float damageAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (Entity target : targets) {
            if (target.getBoundingBox().inflate(0.1).clip(startCenter, endCenter).isPresent()) {
                target.hurtServer(serverWorld, this.damageSources().mobAttack(this), damageAmount);
            }
        }

        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        return false;
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 100.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.4);
    }
}
