package com.kltyton.mob_battle.event;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;

public class DataTrackersEvent {
    public static final EntityDataAccessor<Boolean> FORCED_ATTACK_FLAG =
            SynchedEntityData.defineId(Warden.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_GECKO_LIB_USING =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> CAN_MOVE =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_SKILL =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
}
