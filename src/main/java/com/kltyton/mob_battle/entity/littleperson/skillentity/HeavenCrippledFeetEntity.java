package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HeavenCrippledFeetEntity extends BaseSkillLittlePersonEntity {
    public HeavenCrippledFeetEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 70 * 20;
        attackVariants = new String[]{"attack_1", "attack_2"};
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 2100.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            this.getTarget().hurtServer(serverWorld, this.damageSources().mobAttack(entity), 50);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 2, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (target != null && this.level() instanceof ServerLevel serverWorld) {
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 40);
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 2, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (target != null && this.level() instanceof ServerLevel serverWorld) {
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 150);
        }
    }
}
