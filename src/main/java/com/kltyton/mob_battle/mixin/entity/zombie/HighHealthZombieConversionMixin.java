package com.kltyton.mob_battle.mixin.entity.zombie;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 阻止生命上限超过 20 的普通僵尸进入任何原版僵尸转换出口，同时保留特殊僵尸类型的原版行为。
 */
@Mixin(Zombie.class)
public abstract class HighHealthZombieConversionMixin {
    @Inject(method = "convertsInWater", at = @At("HEAD"), cancellable = true)
    private void mob_battle$disableWaterConversion(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob_battle$isProtectedOrdinaryZombie()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "convertToZombieType", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockZombieTypeConversion(
            ServerLevel level,
            EntityType<? extends Zombie> zombieType,
            CallbackInfo ci
    ) {
        if (this.mob_battle$isProtectedOrdinaryZombie()) {
            ci.cancel();
        }
    }

    @Inject(method = "convertVillagerToZombieVillager", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockVillagerConversion(
            ServerLevel level,
            Villager villager,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.mob_battle$isProtectedOrdinaryZombie()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean mob_battle$isProtectedOrdinaryZombie() {
        Zombie zombie = (Zombie) (Object) this;
        return zombie.getType() == EntityType.ZOMBIE && zombie.getMaxHealth() > 20.0F;
    }
}
