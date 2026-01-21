package com.kltyton.mob_battle.entity.general;

import net.minecraft.entity.mob.MobEntity;

public interface GeneralEntityOnlyOneSkill<T extends MobEntity> {
    void runSkill(T entity);
    boolean hasSkill();
    void setHasSkill(boolean skill);
}
