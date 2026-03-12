package com.kltyton.mob_battle.mixin.baby;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {
    @Redirect(method = "spawnBaby", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/PassiveEntity;createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PassiveEntity;"))
    public PassiveEntity spawnBaby(PassiveEntity instance, ServerWorld world, PassiveEntity passiveEntity, @Local(argsOnly = true) EntityType<? extends PassiveEntity> entityType) {
        return entityType.create(world, SpawnReason.SPAWN_ITEM_USE);
    }
    @Redirect(method = "spawnBaby", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;setBaby(Z)V"))
    public void setBaby(MobEntity instance, boolean baby) {
        instance.setBaby(false);
    }

}
