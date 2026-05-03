package com.kltyton.mob_battle.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ModDamageUtil {
    public static void resetDamageCooldown(Entity target) {
        if (target instanceof LivingEntity living) {
            living.timeUntilRegen = 0;
        }
    }
}
