package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
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
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 3200.0)
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
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ALL)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5 * 20, 19));
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, LittlePersonEntity.class, 10, true, EntityUtil.TeamFilter.ALL)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 20, 6));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        for (LivingEntity livingEntity : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 10, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 9));
        }
    }
}
