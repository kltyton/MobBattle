package com.kltyton.mob_battle.effect;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WardenEntity;

public class ProInsectBiteEffect extends InsectBiteEffect {
    protected void setupWardenAttributes(WardenEntity warden) {
        // 设置最大生命值
        EntityAttributeInstance maxHealth = warden.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(500.0f);
            warden.setHealth(500.0f);
        }

        // 设置攻击伤害
        EntityAttributeInstance attackDamage = warden.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(60.0);
        }
    }
}
