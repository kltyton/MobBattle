package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
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

public class HumanHammerEntity extends BaseSkillLittlePersonEntity {
    public HumanHammerEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 7 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        init();
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1900.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 35.0)
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
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        HumanShieldEntity shieldEntity = EntityUtil.getClosestNearbyEntity(this, HumanShieldEntity.class, 5, EntityUtil.TeamFilter.ONLY_TEAM);
        if (shieldEntity != null && shieldEntity.isAlive()) {
            boolean result = shieldEntity.damage(world, this.getDamageSources().mobAttack(this), amount);
            if (result) this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 5, 14));
            return result;
        } else return super.damage(world, source, amount);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity target = this.getTarget();
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 50);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity target = this.getTarget();
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 45);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class,3,false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(target -> target.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 55));
    }
}
