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
        // 1. 方向向量与冲刺向量 (距离改为 1.0)
        Vec3d lookDir = this.getRotationVector();
        Vec3d dashVector = lookDir.multiply(10.0); // 1格距离
        Box dashArea = this.getBoundingBox().stretch(dashVector).expand(1.0);
        // 3. 伤害逻辑
        List<Entity> targets = serverWorld.getOtherEntities(this, dashArea,
                target -> target instanceof LivingEntity && target.isAlive() && !target.isTeammate(this));
        float damageAmount = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        for (Entity target : targets) {
            target.damage(serverWorld, this.getDamageSources().mobAttack(this), damageAmount);
        }
        this.move(MovementType.SELF, dashVector);

        // 给一点微弱的初速度，让动作看起来更丝滑
        this.setVelocity(lookDir.multiply(2));
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
