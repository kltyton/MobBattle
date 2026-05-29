package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {LivingEntity.class, MobEntity.class}, priority = 900)
public abstract class EntityDisarmMixin {
    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    private void cancelMeleeAttack(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!ModSkillEntityType.canSkill(entity)) {
            cir.setReturnValue(false);
        }
    }
}
