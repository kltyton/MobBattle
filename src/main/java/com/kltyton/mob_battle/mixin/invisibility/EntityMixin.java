package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public abstract class EntityMixin {

    @Unique
    public void custom$setTrueInvisible(boolean invisible) {

    }

    @Unique
    public boolean custom$isTrueInvisible() {
        return false;
    }

    @Unique
    public void custom$setIceAmplifier(int amplifier) {

    }

    @Unique
    public int custom$getIceAmplifier() {
        return -1;
    }

    @Unique
    public void custom$setCompressedArmorMarkerType(int markerType) {

    }

    @Unique
    public int custom$getCompressedArmorMarkerType() {
        return 0;
    }

    @Unique
    public void custom$setPigSpiritMarkAmplifier(int amplifier) {

    }

    @Unique
    public int custom$getPigSpiritMarkAmplifier() {
        return -1;
    }
}
