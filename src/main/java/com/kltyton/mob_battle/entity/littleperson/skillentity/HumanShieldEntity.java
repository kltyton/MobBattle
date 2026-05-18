package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HumanShieldEntity extends BaseSkillLittlePersonEntity {
    public HumanShieldEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 5 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1900.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 25.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public int blockProbability() {
        return 30;
    }
    @Override
    public float maxBlockDamage() {
        return 150f;
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.getAttacker() instanceof HumanHammerEntity && source.getAttacker().isTeammate(this)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 5, 2));
        }
        return super.damage(world, source, amount);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity target = this.getTarget();
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 55);
            target.takeKnockback(1, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity target = this.getTarget();
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 45);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20, 0));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity target = this.getTarget();
            target.damage(serverWorld, this.getDamageSources().mobAttack(entity), 35);
        }
    }
}
