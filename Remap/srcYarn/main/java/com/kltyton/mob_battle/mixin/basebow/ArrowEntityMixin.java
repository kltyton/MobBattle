package com.kltyton.mob_battle.mixin.basebow;

import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ArrowEntity.class)
@Implements(@Interface(iface = ITrueDamageProjectile.class, prefix = "kltyton$"))
public abstract class ArrowEntityMixin extends PersistentProjectileEntity {
    @Unique
    private boolean TrueDamage = false;
    @Unique
    private boolean isMage = false;
    protected ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            this.age();
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
