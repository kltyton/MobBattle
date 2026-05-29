package com.kltyton.mob_battle.entity.littleperson;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
    static AttributeSupplier.Builder createLittlePersonAttributes() {
        return Mob.createMobAttributes().add(Attributes.STEP_HEIGHT, 3);
    }
}
