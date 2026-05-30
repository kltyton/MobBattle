package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FrenchSphereFlowEntity extends BaseSkillLittlePersonEntity {
    public FrenchSphereFlowEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 15 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        COOL_DOWN_TIME_4 = 25 * 20;
        COOL_DOWN_TIME_5 = 80 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3700.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.25);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void attackAdditional(LivingEntity target) {
        target.hurtServer((ServerLevel) this.level(), this.damageSources().indirectMagic(this, this), 25);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            runSkill();
        }
    }

    public void runSkill() {
        String[] skills = {"attack6", "attack5", "attack4", "attack3", "attack2"};
        for (String skill : skills) {
            if (this.canSkill(skill)) {
                performSkill(skill);
                return;
            }
        }
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ALL)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, 19));
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ALL)) {
            livingEntity.heal(200f);
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 9));
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 10 * 20, 1));
        }
    }
    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 10 * 20, 2));
            livingEntity.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 20, 0));
        }
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ONLY_TEAM)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 20, 1));
            livingEntity.heal(100f);
        }
        TaskSchedulerUtil.runLater(40, () -> {
            if (entity.getTarget() != null) {
                entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.getTarget().damageSources().explosion(entity, entity), 200);
                entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.getTarget().damageSources().indirectMagic(entity, entity), 70);
            }

        });
    }
}
