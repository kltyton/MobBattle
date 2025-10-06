package com.kltyton.mob_battle.mixin.undead;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(method = "canHaveStatusEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 2))
    private boolean cancelCanHaveStatusEffect(EntityType<?> instance, TagKey<EntityType<?>> tag) {
        if (instance.isIn(EntityTypeTags.UNDEAD)) return false;
        return instance.isIn(tag);
    }
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
    private boolean cancelBaseTick(LivingEntity instance, ServerWorld world, DamageSource source, float amount) {
        if (instance.hasVehicle() || instance.hasPassengers()) {
            return false;
        } else {
            return instance.damage(world, source, amount);
        }
    }
}
