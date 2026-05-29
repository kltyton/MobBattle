package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.world.World;

public class LoadSilverfishEntity extends CoalSilverfishEntity {
    public LoadSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    public int getCooldownTime() {
        return 20 * 35;
    }
    @Override
    public boolean canBlock() {
        return false;
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
    }
    @Override
    public boolean canSkill() {
        return false;
    }
    public void performSkill() {
    }
    @Override
    public boolean hasSkill() {
        return true;
    }
    @Override
    public void tick() {
        super.tick();
        if (this.age % 20 == 0 && !this.getWorld().isClient) {
            EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(entity -> {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 2));
            });
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return SilverfishEntity.createSilverfishAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1000.0D)
                .add(EntityAttributes.ARMOR, 23.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D);
    }
}
