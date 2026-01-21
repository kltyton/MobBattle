package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TaiLinEntity extends BaseSkillLittlePersonEntity {
    public TaiLinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 4);
        COOL_DOWN_TIME_1 = 8 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 15 * 20;
        COOL_DOWN_TIME_4 = 10 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 5000.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 70.0);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 90);
            entity.getTarget().takeKnockback(5.0, entity.getX() - entity.getTarget().getX(), entity.getZ() - entity.getTarget().getZ());
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 180);
            if (entity.getTarget() instanceof PlayerEntity player) player.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 40, 0));
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!this.hasSkill() && this.age % 20 == 0 && this.random.nextInt(100) < 20) {
                this.triggerAnim("attack_controller", "idle_sometimes");
            }
            if (this.endDamage) {
                for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                    entity.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 70);
                }
            }
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        endDamage = true;
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 4, false, false));
        this.setAiDisabled(false);
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 80);
        }
    }
}
