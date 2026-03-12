package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.player.IPlayerStateAccessor;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
@Implements(@Interface(iface = IPlayerStateAccessor.class, prefix = "accessor$"))
public abstract class PlayerEntityRenderStateMixin {
    @Unique
    private boolean isUsingGeckoLib = false;
    public void accessor$setUseGeckoLib(boolean use) {
        this.isUsingGeckoLib = use;
    }
    public boolean accessor$isUsingGeckoLib() {
        return this.isUsingGeckoLib;
    }
}
