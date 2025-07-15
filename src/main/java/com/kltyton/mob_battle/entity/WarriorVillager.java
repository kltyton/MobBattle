package com.kltyton.mob_battle.entity;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

// 近战村民
public class WarriorVillager extends IronGolemEntity {
    // 添加群体仇恨的检测范围（64格）
    private static final double ALERT_RANGE = 64.0;

    public WarriorVillager(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        this.attackTicksLeft = 10;
        world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.getDamageSources().mobAttack(this);
        boolean bl = target.damage(world, damageSource, g);
        if (bl) {
            // 获取目标的击退抗性
            double d = target instanceof LivingEntity livingEntity ?
                    livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) : 0.0;
            // 计算实际击退效果（击退抗性会减少击飞力度）
            double e = Math.max(0.0, 1.0 - d);
            // 设置目标的速度，实现击飞效果（y轴方向增加速度）
            target.setVelocity(target.getVelocity().add(0.1 * e, 0.0, 0.1 * e));
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
        }
        return bl;
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean bl = super.damage(world, source, amount);
        if (bl && !world.isClient) {
            Entity attacker = source.getAttacker();
            // 确保攻击者是有效生物且不是铁傀儡
            if (attacker instanceof LivingEntity) {
                this.alertOthers((LivingEntity)attacker);
            }
        }
        return bl;
    }
    private void alertOthers(LivingEntity attacker) {
        // 获取64格范围内所有铁傀儡
        List<IronGolemEntity> golems = this.getWorld().getEntitiesByClass(
                IronGolemEntity.class,
                this.getBoundingBox().expand(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (IronGolemEntity golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof GolemEntity) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            golem.setAngryAt(attacker.getUuid());
            golem.setAngerTime(ANGER_TIME_RANGE.get(this.random));

            // 立即更新目标选择
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }
    // 在类中添加以下方法
/*    @Override
    public void tickMovement() {
        super.tickMovement();

        // 游泳时自动上浮
        if (this.isTouchingWater() && !this.isOnGround()) {
            Vec3d velocity = this.getVelocity();
            if (velocity.y < 0.0) {
                this.setVelocity(velocity.multiply(1.0, 0.8, 1.0)); // 减缓下沉速度
            }
            // 头部在水中时持续上浮
            if (this.isSubmergedInWater()) {
                this.setVelocity(this.getVelocity().add(0.0, 0.008, 0.0));
            }
        }
    }*/

/*    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            // 水中移动逻辑
            this.updateVelocity(this.getMovementSpeed() * 0.6F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));

            // 保持游泳动画
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005D, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }*/
    // 添加游泳动画支持
/*    @Override
    protected void updateLimbs(float posDelta) {
        float f;
        if (this.isTouchingWater()) {
            f = (float)this.getVelocity().horizontalLengthSquared();
            if (f > 1.0F) {
                f = 1.0F;
            }
            this.limbAnimator.updateLimbs(f, 0.3F,0.4F);
        } else {
            super.updateLimbs(posDelta);
        }
    }*/

    public static boolean checkWarriorSpawnRules(EntityType<WarriorVillager> warriorVillagerEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getLocalDifficulty(pos).getGlobalDifficulty() != Difficulty.PEACEFUL;
    }
}
