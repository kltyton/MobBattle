package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class HumanHammerEntity extends BaseSkillLittlePersonEntity {
    public HumanHammerEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 7 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        init();
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 1900.0)
                .add(Attributes.ATTACK_DAMAGE, 35.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public int blockProbability() {
        return 20;
    }
    @Override
    public float maxBlockDamage() {
        return 100f;
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        HumanShieldEntity shieldEntity = EntityUtil.getClosestNearbyEntity(this, HumanShieldEntity.class, 5, EntityUtil.TeamFilter.ONLY_TEAM);
        if (shieldEntity != null && shieldEntity.isAlive()) {
            this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 5, 14));
            shieldEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 5, 2));
            boolean result = shieldEntity.hurtServer(world, this.damageSources().mobAttack(this), amount);
            return result;
        } else return super.hurtServer(world, source, amount);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            LivingEntity target = this.getTarget();
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 50);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            LivingEntity target = this.getTarget();
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 45);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class,3,false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(target -> {
            target.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 55);
        });
    }
}
