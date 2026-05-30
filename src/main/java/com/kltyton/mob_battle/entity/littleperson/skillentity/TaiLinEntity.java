package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TaiLinEntity extends BaseSkillLittlePersonEntity {
    public TaiLinEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        COOL_DOWN_TIME_4 = 10 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 6000.0)
                .add(Attributes.ATTACK_DAMAGE, 70.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.25);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 90);
            entity.getTarget().knockback(5.0, entity.getX() - entity.getTarget().getX(), entity.getZ() - entity.getTarget().getZ());
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 180);
            if (entity.getTarget() instanceof Player player) player.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 40, 0));
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!this.hasSkill() && this.tickCount % 20 == 0 && this.random.nextInt(100) < 20) {
                this.triggerAnim("attack_controller", "idle_sometimes");
            }
            if (this.endDamage) {
                for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                    entity.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 70);
                }
            }
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        endDamage = true;
        entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 4, false, false));
        this.setNoAi(false);
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 80);
        }
    }
}
