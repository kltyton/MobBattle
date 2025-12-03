package com.kltyton.mob_battle.mixin.undead;

import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullEntityKing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
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
    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 3))
    private boolean cancelDamage(DamageSource instance, TagKey<DamageType> tag) {
        Entity sourcer = instance.getSource();
        Entity attacker = instance.getAttacker();
        if (sourcer instanceof WitherSkullEntityKing && attacker instanceof WitherSkullEntityKing) {
            return true;
        }
        return instance.isIn(tag);
    }
    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean isInvulnerableTo(LivingEntity instance, ServerWorld world, DamageSource source) {
        Entity sourcer = source.getSource();
        Entity attacker = source.getAttacker();
        if (sourcer instanceof WitherSkullEntityKing && attacker instanceof WitherSkullEntityKing) {
            instance.timeUntilRegen = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }

}
