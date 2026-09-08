package com.kltyton.mob_battle.mixin.combatlog;

import com.geckolib.animatable.GeoEntity;
import com.kltyton.mob_battle.command.CombatLogSystem;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 只观察 GeckoLib 触发动作并记录战斗日志，不决定资源有效性或取消第三方动画。 */
@Mixin(value = GeoEntity.class, remap = false)
public interface GeoEntityCombatLogMixin {
    @Inject(method = "triggerAnim", at = @At("HEAD"))
    private void mobBattle$logTriggeredAction(String controllerName, String animationName, CallbackInfo ci) {
        if ((Object) this instanceof Entity entity) {
            if (!entity.level().isClientSide()
                    && !"idle".equals(animationName)
                    && !"walk".equals(animationName)) {
                CombatLogSystem.logAction(entity, "播放动作 " + controllerName + "/" + animationName);
            }
        }
    }
}
