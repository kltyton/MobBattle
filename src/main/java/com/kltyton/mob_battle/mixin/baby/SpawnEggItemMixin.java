package com.kltyton.mob_battle.mixin.baby;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {
    @Redirect(method = "spawnOffspringFromSpawnEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/AgeableMob;"))
    public AgeableMob spawnBaby(AgeableMob instance, ServerLevel world, AgeableMob passiveEntity, @Local(argsOnly = true) EntityType<? extends AgeableMob> entityType) {
        return entityType.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
    }
    @Redirect(method = "spawnOffspringFromSpawnEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setBaby(Z)V"))
    public void setBaby(Mob instance, boolean baby) {
        instance.setBaby(false);
    }

}
