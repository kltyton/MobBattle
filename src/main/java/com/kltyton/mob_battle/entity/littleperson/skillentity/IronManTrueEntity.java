package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IronManTrueEntity extends BaseSkillLittlePersonEntity {
    public IronManTrueEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 15 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        COOL_DOWN_TIME_4 = 20 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 2500.0)
                .add(Attributes.ATTACK_DAMAGE, 65.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.30);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
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
        if (!(entity.level() instanceof ServerLevel world)) return;

        Vec3 pos = entity.position();
        Vec3 forward = entity.getViewVector(1.0F);
        Vec3 side = new Vec3(-forward.z, 0.0, forward.x).normalize();
        double range = 6.0;
        List<Entity> targets = world.getEntities(entity, entity.getBoundingBox().inflate(range));
        for (Entity target : targets) {
            if (target instanceof LivingEntity livingTarget && EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), livingTarget)) {
                Vec3 relativePos = target.position().subtract(pos);
                double distanceForward = relativePos.dot(forward);
                double distanceSide = Math.abs(relativePos.dot(side));
                // 5. 范围判定：
                // 长度：在前方 0 到 5 格之间
                // 宽度：中心线左右各 1.5 格（总宽3格）
                // 高度：高度差在 2 格以内（防止打到正上方太高的东西）
                if (distanceForward > 0 && distanceForward <= 5.0 &&
                        distanceSide <= 0.5 &&
                        Math.abs(relativePos.y) <= 2.0) {

                    livingTarget.hurtServer(world, entity.damageSources().mobAttack(entity), 70.0f);
                }
            }
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            List<LivingEntity> targets = EntityUtil.getNearbyEntity(entity,LivingEntity.class, Object.class,4, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);
            for (LivingEntity livingEntity : targets) {
                if (!EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), livingEntity)) {
                    continue;
                }
                livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 90);
            }
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (!(entity.level() instanceof ServerLevel world)) return;

        Vec3 pos = entity.position();
        Vec3 forward = entity.getViewVector(1.0F);
        Vec3 side = new Vec3(-forward.z, 0.0, forward.x).normalize();
        double range = 16.0;
        List<Entity> targets = world.getEntities(entity, entity.getBoundingBox().inflate(range));
        for (Entity target : targets) {
            if (target instanceof LivingEntity livingTarget && EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), livingTarget)) {
                Vec3 relativePos = target.position().subtract(pos);
                double distanceForward = relativePos.dot(forward);
                double distanceSide = Math.abs(relativePos.dot(side));
                // 5. 范围判定：
                // 长度：在前方 0 到 5 格之间
                // 宽度：中心线左右各 1.5 格（总宽3格）
                // 高度：高度差在 2 格以内（防止打到正上方太高的东西）
                if (distanceForward > 0 && distanceForward <= 10.0 &&
                        distanceSide <= 1.5 &&
                        Math.abs(relativePos.y) <= 2.0) {

                    livingTarget.hurtServer(world, entity.damageSources().mobAttack(entity), 95.0f);
                }
            }
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        Level world = entity.level();
        int skullCount = 3;
        RandomSource random = world.getRandom();
        for (int i = 0; i < skullCount; i++) {
            double xOffset = (random.nextDouble() - 0.5) * 8.0;
            double yOffset = (random.nextDouble() - 0.5) * 8.0; // 围绕眼睛上下浮动
            double zOffset = (random.nextDouble() - 0.5) * 8.0;
            Vec3 lookDir = entity.getViewVector(1.0F);
            Vec3 velocity = lookDir.add(
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
            bullet.setPos(entity.getX() + xOffset, entity.getEyeY() + yOffset, entity.getZ() + zOffset);
            world.addFreshEntity(bullet);
        }
    }

}
