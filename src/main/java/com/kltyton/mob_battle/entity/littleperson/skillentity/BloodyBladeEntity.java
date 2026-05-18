package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BloodyBladeEntity extends BaseSkillLittlePersonEntity {
    public BloodyBladeEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
        super(entityType, world, 2);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 15 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1000.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0)
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
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            this.getTarget().damage(serverWorld, this.getDamageSources().mobAttack(entity), 80);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, BloodyBladeEntity.class, 10, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(livingEntity -> {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 5, 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 5, 4));
        });
    }
}
