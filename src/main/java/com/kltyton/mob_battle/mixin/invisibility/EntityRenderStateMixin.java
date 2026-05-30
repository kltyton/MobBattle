package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
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
    @Unique
    private int pigSpiritMarkAmplifier = -1;
    @Unique
    private boolean healthBarVisible = false;
    @Unique
    private float healthBarHealth = 0.0F;
    @Unique
    private float healthBarMaxHealth = 1.0F;

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
    public void custom$setPigSpiritMarkAmplifier(int amplifier) {
        this.pigSpiritMarkAmplifier = amplifier;
    }
    public int custom$getPigSpiritMarkAmplifier() {
        return pigSpiritMarkAmplifier;
    }
    public void custom$setHealthBarVisible(boolean visible) {
        this.healthBarVisible = visible;
    }
    public boolean custom$isHealthBarVisible() {
        return this.healthBarVisible;
    }
    public void custom$setHealthBarHealth(float health) {
        this.healthBarHealth = health;
    }
    public float custom$getHealthBarHealth() {
        return this.healthBarHealth;
    }
    public void custom$setHealthBarMaxHealth(float maxHealth) {
        this.healthBarMaxHealth = maxHealth;
    }
    public float custom$getHealthBarMaxHealth() {
        return this.healthBarMaxHealth;
    }
    public boolean custom$isTrueInvisible() {
        return trueInvisible;
    }
    public void custom$setTrueInvisible(boolean invisible) {
        this.trueInvisible = invisible;
    }
}
