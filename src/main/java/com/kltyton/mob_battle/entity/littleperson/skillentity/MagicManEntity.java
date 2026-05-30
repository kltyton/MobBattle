package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
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
import org.jetbrains.annotations.NotNull;

public class MagicManEntity extends BaseSkillLittlePersonEntity {
    public MagicManEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 20 * 20;
        COOL_DOWN_TIME_2 = 25 * 20;
        COOL_DOWN_TIME_3 = 65 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 1300.0)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(
                livingEntity -> {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, 14), entity);
                }
        );
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(
                livingEntity -> {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 4), entity);
                }
        );
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.level() instanceof ServerLevel serverWorld) {
            EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(
                    livingEntity -> {
                        livingEntity.hurtServer(serverWorld, this.damageSources().indirectMagic(entity, entity), 150);
                    }
            );
            EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(
                    livingEntity -> {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 10 * 20, 14), entity);
                    }
            );
            this.hurtServer(serverWorld, this.damageSources().magic(), 100);
        }
    }
    @Override
    public boolean canSkill(String skill) {
        if (skill.equals("attack4")) {
            if (this.getHealth() / this.getMaxHealth() < 0.3) {
                return super.canSkill(skill);
            } else return false;
        } else return super.canSkill(skill);
    }
}
