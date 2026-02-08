package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.LivingEntity;

public interface ModSkillEntityType {
    boolean canSkill();

    static boolean canSkill(LivingEntity livingEntity) {
        return !livingEntity.hasStatusEffect(ModEffects.DISARM_ENTRY);
    }
}
