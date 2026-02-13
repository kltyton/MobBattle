package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class FrenchSphereFlowEntity extends BaseSkillLittlePersonEntity {
    public FrenchSphereFlowEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 15 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        COOL_DOWN_TIME_4 = 25 * 20;
        COOL_DOWN_TIME_5 = 80 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 3700.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void attackAdditional(LivingEntity target) {
        target.damage((ServerWorld) this.getWorld(), this.getDamageSources().indirectMagic(this, this), 25);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (this.canSkill("attack2")) performSkill("attack2");
            if (this.canSkill("attack3")) performSkill("attack3");
            if (this.canSkill("attack4")) performSkill("attack4");
        }
    }

    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ALL)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5 * 20, 19));
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
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 9));
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 10 * 20, 1));
        }
    }
    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 10 * 20, 2));
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 20, 0));
        }
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ONLY_TEAM)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 1));
            livingEntity.heal(100f);
        }
        TaskSchedulerUtil.runLater(40, () -> {
            if (entity.getTarget() != null) {
                entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getTarget().getDamageSources().explosion(entity, entity), 200);
                entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getTarget().getDamageSources().indirectMagic(entity, entity), 70);
            }

        });
    }
}
