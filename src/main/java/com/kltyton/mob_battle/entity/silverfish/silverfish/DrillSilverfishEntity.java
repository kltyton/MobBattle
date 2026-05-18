package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DrillSilverfishEntity extends CoalSilverfishEntity {
    public DrillSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
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
        if (this.getWorld().isClient) return;
        ServerWorld serverWorld = (ServerWorld) this.getWorld();

        Vec3d lookDir = this.getRotationVector();
        lookDir = new Vec3d(lookDir.x, 0, lookDir.z);
        if (lookDir.lengthSquared() < 1.0E-6) return;
        lookDir = lookDir.normalize();

        Vec3d wantedDash = lookDir.multiply(10.0);

        Vec3d oldPos = this.getPos();
        Box oldBox = this.getBoundingBox();
        Vec3d startCenter = oldBox.getCenter();

        this.move(MovementType.SELF, wantedDash);

        Vec3d actualDash = this.getPos().subtract(oldPos);
        if (actualDash.lengthSquared() < 0.0001) return;

        Vec3d endCenter = startCenter.add(actualDash);
        Box hitBox = oldBox.stretch(actualDash).expand(0.3);

        List<Entity> targets = serverWorld.getOtherEntities(this, hitBox,
                target -> target instanceof LivingEntity
                        && target.isAlive()
                        && !target.isTeammate(this));

        float damageAmount = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);

        for (Entity target : targets) {
            if (target.getBoundingBox().expand(0.1).raycast(startCenter, endCenter).isPresent()) {
                target.damage(serverWorld, this.getDamageSources().mobAttack(this), damageAmount);
            }
        }

        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        return false;
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return SilverfishEntity.createSilverfishAttributes()
                .add(EntityAttributes.MAX_HEALTH, 200.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 100.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.4);
    }
}
