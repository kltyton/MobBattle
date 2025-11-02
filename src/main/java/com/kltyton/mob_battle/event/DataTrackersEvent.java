package com.kltyton.mob_battle.event;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WardenEntity;

public class DataTrackersEvent {
    public static final TrackedData<Boolean> FORCED_ATTACK_FLAG =
            DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
}
