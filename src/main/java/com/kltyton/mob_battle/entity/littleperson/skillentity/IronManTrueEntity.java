package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class IronManTrueEntity extends BaseSkillLittlePersonEntity {
    public IronManTrueEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 15 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        COOL_DOWN_TIME_4 = 20 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2000.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 65.0);
    }
    @Override
    public int blockProbability() {
        return 30;
    }
    @Override
    public float maxBlockDamage() {
        return 220f;
    }
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;

        Vec3d pos = entity.getPos();
        Vec3d forward = entity.getRotationVec(1.0F);
        Vec3d side = new Vec3d(-forward.z, 0.0, forward.x).normalize();
        double range = 6.0;
        List<Entity> targets = world.getOtherEntities(entity, entity.getBoundingBox().expand(range));
        for (Entity target : targets) {
            if (target instanceof LivingEntity livingTarget && target.isAlive()) {
                Vec3d relativePos = target.getPos().subtract(pos);
                double distanceForward = relativePos.dotProduct(forward);
                double distanceSide = Math.abs(relativePos.dotProduct(side));
                // 5. 范围判定：
                // 长度：在前方 0 到 5 格之间
                // 宽度：中心线左右各 1.5 格（总宽3格）
                // 高度：高度差在 2 格以内（防止打到正上方太高的东西）
                if (distanceForward > 0 && distanceForward <= 5.0 &&
                        distanceSide <= 0.5 &&
                        Math.abs(relativePos.y) <= 2.0) {

                    livingTarget.damage(world, entity.getDamageSources().mobAttack(entity), 70.0f);
                }
            }
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 90);
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;

        Vec3d pos = entity.getPos();
        Vec3d forward = entity.getRotationVec(1.0F);
        Vec3d side = new Vec3d(-forward.z, 0.0, forward.x).normalize();
        double range = 16.0;
        List<Entity> targets = world.getOtherEntities(entity, entity.getBoundingBox().expand(range));
        for (Entity target : targets) {
            if (target instanceof LivingEntity livingTarget && target.isAlive()) {
                Vec3d relativePos = target.getPos().subtract(pos);
                double distanceForward = relativePos.dotProduct(forward);
                double distanceSide = Math.abs(relativePos.dotProduct(side));
                // 5. 范围判定：
                // 长度：在前方 0 到 5 格之间
                // 宽度：中心线左右各 1.5 格（总宽3格）
                // 高度：高度差在 2 格以内（防止打到正上方太高的东西）
                if (distanceForward > 0 && distanceForward <= 10.0 &&
                        distanceSide <= 1.5 &&
                        Math.abs(relativePos.y) <= 2.0) {

                    livingTarget.damage(world, entity.getDamageSources().mobAttack(entity), 95.0f);
                }
            }
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        World world = entity.getWorld();
        int skullCount = 3;
        Random random = world.getRandom();
        for (int i = 0; i < skullCount; i++) {
            double xOffset = (random.nextDouble() - 0.5) * 8.0;
            double yOffset = (random.nextDouble() - 0.5) * 8.0; // 围绕眼睛上下浮动
            double zOffset = (random.nextDouble() - 0.5) * 8.0;
            Vec3d lookDir = entity.getRotationVec(1.0F);
            Vec3d velocity = lookDir.add(
                    (random.nextDouble() - 0.5) * 8.0, // X轴扰动
                    (random.nextDouble() - 0.5) * 8.0, // Y轴扰动
                    (random.nextDouble() - 0.5) * 8.0  // Z轴扰动
            ).normalize(); // 重新归一化，确保速度一致

            Direction.Axis mainAxis = Math.abs(velocity.x) > Math.abs(velocity.z) ?
                    Direction.Axis.X : Direction.Axis.Z;
            IronManBulletEntity bullet = new IronManBulletEntity(
                    world,
                    entity,
                    entity.getTarget(),
                    mainAxis);
            // 把子弹加到世界
            bullet.setPosition(entity.getX() + xOffset, entity.getEyeY() + yOffset, entity.getZ() + zOffset);
            world.spawnEntity(bullet);
        }
    }

}
