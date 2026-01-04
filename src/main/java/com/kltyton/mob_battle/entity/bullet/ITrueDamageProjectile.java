package com.kltyton.mob_battle.entity.bullet;

import org.jetbrains.annotations.Nullable;

public interface ITrueDamageProjectile {
     void setTrueDamage(boolean fixed_damage, @Nullable Boolean isMage);
     boolean isTrueDamage();
     boolean isMage();
}
