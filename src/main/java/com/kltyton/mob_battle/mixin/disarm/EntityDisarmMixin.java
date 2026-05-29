package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {LivingEntity.class, Mob.class}, priority = 900)
public abstract class EntityDisarmMixin {
    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void cancelMeleeAttack(ServerLevel world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!ModSkillEntityType.canSkill(entity)) {
            cir.setReturnValue(false);
        }
    }
}
