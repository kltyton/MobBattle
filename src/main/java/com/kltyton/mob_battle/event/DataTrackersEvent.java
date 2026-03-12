package com.kltyton.mob_battle.event;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DataTrackersEvent {
    public static final TrackedData<Boolean> FORCED_ATTACK_FLAG =
            DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_GECKO_LIB_USING =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> CAN_MOVE =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SKILL =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
}
