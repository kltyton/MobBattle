package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class PoisonousSlashEntity extends BaseSkillLittlePersonEntity {
    public PoisonousSlashEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 25 * 20;
        COOL_DOWN_TIME_3 = 5 * 20;
        COOL_DOWN_TIME_4 = 15 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 4000.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 55.0);
    }
    @Override
    public void attackAdditional(LivingEntity target) {
        target.addStatusEffect(
                new StatusEffectInstance(StatusEffects.POISON, 30 * 60 * 20, 2)
        );
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.endDamage) {
            for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 3, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                entity.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 70);
            }
        }
    }
    @Override
    public int blockProbability() {
        return 20;
    }
    @Override
    public float maxBlockDamage() {
        return 220f;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        entity.endDamage = true;
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10 * 20, 1));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 8 * 20, 1));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 10 * 20, 4));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10 * 20, 1));
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().setVelocity(
                    (entity.getTarget().getX() - entity.getX()) * 0.5,  // 水平速度分量
                    1.2,                                    // 垂直速度分量（向上）
                    (entity.getTarget().getZ() - entity.getZ()) * 0.5   // 水平速度分量
            );
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 80);
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 3, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 80);
        }
    }
}
