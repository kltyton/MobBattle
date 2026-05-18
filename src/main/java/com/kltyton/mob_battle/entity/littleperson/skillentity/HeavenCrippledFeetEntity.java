package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class HeavenCrippledFeetEntity extends BaseSkillLittlePersonEntity {
    public HeavenCrippledFeetEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 70 * 20;
        attackVariants = new String[]{"attack_1", "attack_2"};
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2100.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 30.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            this.getTarget().damage(serverWorld, this.getDamageSources().mobAttack(entity), 50);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 2, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (target != null && this.getWorld() instanceof ServerWorld serverWorld) {
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 40);
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 2, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (target != null && this.getWorld() instanceof ServerWorld serverWorld) {
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 150);
        }
    }
}
