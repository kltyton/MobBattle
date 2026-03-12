package com.kltyton.mob_battle.entity.skull;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import org.jetbrains.annotations.Nullable;

public interface IModSkullEntity extends Tameable, ModSkillEntityType {
    void setOwner(LivingEntity entity);
    boolean isOwner(LivingEntity entity);
    void setOwner(@Nullable LazyEntityReference<LivingEntity> owner);
    default void killSlave() {
        int count = EntityUtil.getNearbyEntityCount((LivingEntity) this, LivingEntity.class, IModSkullEntity.class, 100);
        if (count > 60) {
            ((LivingEntity) this).discard();
        }
    }
}
