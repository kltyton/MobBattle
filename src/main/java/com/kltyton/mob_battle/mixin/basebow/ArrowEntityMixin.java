package com.kltyton.mob_battle.mixin.basebow;

import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

@Mixin(Arrow.class)
@Implements(@Interface(iface = ITrueDamageProjectile.class, prefix = "kltyton$"))
public abstract class ArrowEntityMixin extends AbstractArrow {
    @Unique
    private boolean TrueDamage = false;
    @Unique
    private boolean isMage = false;
    protected ArrowEntityMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            this.tickDespawn();
        }
    }
    public void kltyton$setTrueDamage(boolean fixed_damage, Boolean isMage) {
        TrueDamage = fixed_damage;
        this.isMage = Objects.requireNonNullElse(isMage, false);
    }
    public boolean kltyton$isTrueDamage() {
        return TrueDamage;
    }
    public boolean kltyton$isMage() {
        return this.isMage;
    }
}
