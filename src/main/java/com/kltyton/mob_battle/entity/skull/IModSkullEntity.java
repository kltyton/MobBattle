package com.kltyton.mob_battle.entity.skull;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.Nullable;

public interface IModSkullEntity extends OwnableEntity, ModSkillEntityType, OwnedSummon {
    void setOwner(LivingEntity entity);
    boolean isOwner(LivingEntity entity);
    void setOwner(@Nullable EntityReference<LivingEntity> owner);
    @Nullable
    @Override
    default LivingEntity getSummonOwner() {
        return this.getOwner();
    }
    default void killSlave() {
        int count = EntityUtil.getNearbyEntityCount((LivingEntity) this, LivingEntity.class, IModSkullEntity.class, 100);
        if (count > 60) {
            ((LivingEntity) this).discard();
        }
    }
}
