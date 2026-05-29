package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

public class LoadSilverfishEntity extends CoalSilverfishEntity {
    public LoadSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
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
        if (this.tickCount % 20 == 0 && !this.level().isClientSide) {
            EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 5, true, EntityUtil.TeamFilter.ONLY_TEAM).forEach(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 2));
            });
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ARMOR, 23.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D);
    }
}
