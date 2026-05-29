package com.kltyton.mob_battle.accessor;

import net.minecraft.entity.LivingEntity;

public interface IPiglinEntity {
    LivingEntity getTargetEntity();
    void setTargetEntity(LivingEntity livingEntity);
}
