package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.player.IPlayerStateAccessor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
@Implements(@Interface(iface = IPlayerStateAccessor.class, prefix = "accessor$"))
public abstract class PlayerEntityRenderStateMixin {
    @Unique
    private boolean isUsingGeckoLib = false;

    @Unique
    private boolean compressedCopperPower = false;

    public void accessor$setUseGeckoLib(boolean use) {
        this.isUsingGeckoLib = use;
    }

    public boolean accessor$isUsingGeckoLib() {
        return this.isUsingGeckoLib;
    }

    public void accessor$setCompressedCopperPower(boolean charged) {
        this.compressedCopperPower = charged;
    }

    public boolean accessor$hasCompressedCopperPower() {
        return this.compressedCopperPower;
    }
}
