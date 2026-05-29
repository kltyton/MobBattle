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
import net.minecraft.world.level.Level;

public class BloodyBladeEntity extends BaseSkillLittlePersonEntity {
    public BloodyBladeEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 2);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 15 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public void attackAdditional(LivingEntity target) {
        this.heal(10f);
    }
    @Override
    public int blockProbability() {
        return 10;
    }
    @Override
    public float maxBlockDamage() {
        return 70f;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            this.getTarget().hurtServer(serverWorld, this.damageSources().mobAttack(entity), 80);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, BloodyBladeEntity.class, 10, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(livingEntity -> {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 5, 0));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 5, 4));
        });
    }
}
