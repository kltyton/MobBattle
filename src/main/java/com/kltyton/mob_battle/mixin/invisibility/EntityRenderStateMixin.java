package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public class EntityRenderStateMixin {
    @Unique
    private boolean trueInvisible;
    @Unique
    private int iceAmplifier = -1;
    @Unique
    private int compressedArmorMarkerType = 0;

    public void custom$setIceAmplifier(int amplifier) {
        this.iceAmplifier = amplifier;
    }
    public int custom$getIceAmplifier() {
        return iceAmplifier;
    }
    public void custom$setCompressedArmorMarkerType(int markerType) {
        this.compressedArmorMarkerType = markerType;
    }
    public int custom$getCompressedArmorMarkerType() {
        return compressedArmorMarkerType;
    }
    public boolean custom$isTrueInvisible() {
        return trueInvisible;
    }
    public void custom$setTrueInvisible(boolean invisible) {
        this.trueInvisible = invisible;
    }
}
