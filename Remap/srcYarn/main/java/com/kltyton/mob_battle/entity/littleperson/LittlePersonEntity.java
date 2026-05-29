package com.kltyton.mob_battle.entity.littleperson;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import software.bernie.geckolib.animatable.GeoEntity;

public interface LittlePersonEntity extends GeoEntity, ModSkillEntityType {
    default void heal() {
    }
    default boolean blockAttack(DamageSource source, float amount) {
        return false;
    }
    default int blockProbability() {
        return 0;
    }
    default float maxBlockDamage() {
        return 0;
    }
    default void attackAdditional(LivingEntity target) {
    }
    static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.STEP_HEIGHT, 3);
    }
}
