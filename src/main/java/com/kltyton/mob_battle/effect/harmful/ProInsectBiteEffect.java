package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;

public class ProInsectBiteEffect extends InsectBiteEffect {
    protected void setupWardenAttributes(Warden warden) {
        // 设置最大生命值
        AttributeInstance maxHealth = warden.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(500.0f);
            warden.setHealth(500.0f);
        }

        // 设置攻击伤害
        AttributeInstance attackDamage = warden.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(60.0);
        }
    }
}
