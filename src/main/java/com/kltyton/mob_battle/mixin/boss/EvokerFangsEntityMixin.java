package com.kltyton.mob_battle.mixin.boss;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EvokerFangsEntity.class)
public abstract class EvokerFangsEntityMixin extends Entity implements Ownable {
    public EvokerFangsEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @ModifyArg(method = "damage(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 2)
    private float modifyDamage(float damage) {
        if (this.getOwner() != null && this.getOwner() instanceof DeepCreatureEntity) {
            return 150;
        }
        return damage;
    }
    @ModifyArg(method = "damage(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;serverDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), index = 1)
    private float modifyDamage2(float damage) {
        if (this.getOwner() != null && this.getOwner() instanceof DeepCreatureEntity) {
            return 150;
        }
        return damage;
    }
}
