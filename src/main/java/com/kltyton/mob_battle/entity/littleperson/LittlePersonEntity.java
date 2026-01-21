package com.kltyton.mob_battle.entity.littleperson;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import software.bernie.geckolib.animatable.GeoEntity;

public interface LittlePersonEntity extends GeoEntity {
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
}
