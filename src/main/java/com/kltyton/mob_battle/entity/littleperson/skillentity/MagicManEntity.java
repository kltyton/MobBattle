package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class MagicManEntity extends BaseSkillLittlePersonEntity {
    public MagicManEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 20 * 20;
        COOL_DOWN_TIME_2 = 25 * 20;
        COOL_DOWN_TIME_3 = 65 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1300.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0)
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
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5 * 20, 14), entity);
                }
        );
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(
                livingEntity -> {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 4), entity);
                }
        );
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(
                    livingEntity -> {
                        livingEntity.damage(serverWorld, this.getDamageSources().indirectMagic(entity, entity), 150);
                    }
            );
            EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(
                    livingEntity -> {
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 10 * 20, 14), entity);
                    }
            );
            this.damage(serverWorld, this.getDamageSources().magic(), 100);
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
