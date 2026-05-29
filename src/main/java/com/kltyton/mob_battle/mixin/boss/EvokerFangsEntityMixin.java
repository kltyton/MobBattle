package com.kltyton.mob_battle.mixin.boss;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.evoker.ModEvokerOwner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EvokerFangs.class)
public abstract class EvokerFangsEntityMixin extends Entity implements TraceableEntity {
    public EvokerFangsEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    @ModifyArg(method = "dealDamageTo(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 2)
    private float modifyDamage(float damage) {
        if (this.getOwner() != null) {
            if (this.getOwner() instanceof DeepCreatureEntity) {
                return 150;
            }
            if (this.getOwner() instanceof ModEvokerOwner modEvokerOwner) {
                return damage + modEvokerOwner.getEvokerDamage();
            }
        }
        return damage;
    }
    @ModifyArg(method = "dealDamageTo(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"), index = 1)
    private float modifyDamage2(float damage) {
        if (this.getOwner() != null) {
            if (this.getOwner() instanceof DeepCreatureEntity) {
                return 150;
            }
            if (this.getOwner() instanceof ModEvokerOwner modEvokerOwner) {
                return damage + modEvokerOwner.getEvokerDamage();
            }
        }
        return damage;
    }
}
