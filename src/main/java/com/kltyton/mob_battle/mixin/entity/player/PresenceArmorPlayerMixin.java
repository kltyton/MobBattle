package com.kltyton.mob_battle.mixin.entity.player;

import com.kltyton.mob_battle.items.armor.presence.PresenceArmorUser;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 为玩家同步“有”套装的弹药、模式、施法状态与剩余冷却，不改变已有 Payload。 */
@Mixin(Player.class)
public abstract class PresenceArmorPlayerMixin extends LivingEntity implements PresenceArmorUser {
    @Unique
    private static final EntityDataAccessor<Integer> PRESENCE_STATE =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    protected PresenceArmorPlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void mobBattle$definePresenceState(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(PRESENCE_STATE, 0);
    }

    @Override
    public int mobBattle$getPresenceState() {
        return this.entityData.get(PRESENCE_STATE);
    }

    @Override
    public void mobBattle$setPresenceState(int state) {
        this.entityData.set(PRESENCE_STATE, state);
    }
}
