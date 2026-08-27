package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.skill.api.SkillEntity;
import net.minecraft.world.entity.LivingEntity;

/**
 * @deprecated 兼容旧源码与附属内容的入口；新代码统一实现 {@link SkillEntity}。
 */
@Deprecated(forRemoval = false)
public interface ModSkillEntityType extends SkillEntity {
    @Override
    boolean canSkill();

    static boolean canSkill(LivingEntity livingEntity) {
        return !livingEntity.hasEffect(ModEffects.DISARM_ENTRY);
    }
}
