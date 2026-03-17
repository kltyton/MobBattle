package com.kltyton.mob_battle.entity.bullet;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface ITrueDamageProjectile {
     void setTrueDamage(boolean fixed_damage, @Nullable Boolean isMage);
     boolean isTrueDamage();
     boolean isMage();
     default void additionalDamage(Entity entity) {

     }
}
