package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class HumanShieldEntity extends BaseSkillLittlePersonEntity {
    public HumanShieldEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 5 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 1900.0)
                .add(Attributes.ATTACK_DAMAGE, 25.0)
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
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.getEntity() instanceof HumanHammerEntity && source.getEntity().isAlliedTo(this)) {
            this.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 5, 2));
        }
        return super.hurtServer(world, source, amount);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            LivingEntity target = this.getTarget();
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 55);
            target.knockback(1, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            LivingEntity target = this.getTarget();
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 45);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.getTarget() != null && this.getTarget().isAlive() && this.level() instanceof ServerLevel serverWorld) {
            LivingEntity target = this.getTarget();
            target.hurtServer(serverWorld, this.damageSources().mobAttack(entity), 35);
        }
    }
}
