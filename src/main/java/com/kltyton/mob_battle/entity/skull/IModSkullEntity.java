package com.kltyton.mob_battle.entity.skull;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import org.jetbrains.annotations.Nullable;

public interface IModSkullEntity extends Tameable {
    void setOwner(LivingEntity entity);
    boolean isOwner(LivingEntity entity);
    void setOwner(@Nullable LazyEntityReference<LivingEntity> owner);
}
