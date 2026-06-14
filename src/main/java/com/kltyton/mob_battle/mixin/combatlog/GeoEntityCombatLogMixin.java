package com.kltyton.mob_battle.mixin.combatlog;

import com.geckolib.animatable.GeoEntity;
import com.kltyton.mob_battle.command.CombatLogSystem;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GeoEntity.class, remap = false)
public interface GeoEntityCombatLogMixin {
    @Inject(method = "triggerAnim", at = @At("HEAD"))
    private void mobBattle$logTriggeredAction(String controllerName, String animationName, CallbackInfo ci) {
        if ((Object) this instanceof Entity entity
                && !entity.level().isClientSide()
                && !"idle".equals(animationName)
                && !"walk".equals(animationName)) {
            CombatLogSystem.logAction(entity, "播放动作 " + controllerName + "/" + animationName);
        }
    }
}
