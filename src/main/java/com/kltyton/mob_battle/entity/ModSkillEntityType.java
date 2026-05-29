package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.world.entity.LivingEntity;

public interface ModSkillEntityType {
    boolean canSkill();

    static boolean canSkill(LivingEntity livingEntity) {
        return !livingEntity.hasEffect(ModEffects.DISARM_ENTRY);
    }
}
