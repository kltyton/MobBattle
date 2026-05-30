package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PoisonousSlashEntity extends BaseSkillLittlePersonEntity {
    public PoisonousSlashEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 25 * 20;
        COOL_DOWN_TIME_3 = 5 * 20;
        COOL_DOWN_TIME_4 = 15 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 4500.0)
                .add(Attributes.ATTACK_DAMAGE, 55.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.25);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public void attackAdditional(LivingEntity target) {
        target.addEffect(
                new MobEffectInstance(MobEffects.POISON, 30 * 60 * 20, 2)
        );
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.endDamage) {
            for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 3, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                entity.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 70);
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
        this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 10 * 20, 4));
        this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 10 * 20, 14));
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().setDeltaMovement(
                    (entity.getTarget().getX() - entity.getX()) * 0.5,  // 水平速度分量
                    1.2,                                    // 垂直速度分量（向上）
                    (entity.getTarget().getZ() - entity.getZ()) * 0.5   // 水平速度分量
            );
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 80);
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 3, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 80);
        }
    }
}
