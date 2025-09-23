package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(method = "getPreferredTarget", at = @At("RETURN"), cancellable = true)
    private static void getPreferredTarget(ServerWorld world, PiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Brain<PiglinEntity>  brain = piglin.getBrain();
        LivingEntity livingEntity = ((IPiglinEntity)piglin).getTargetEntity();
        if (livingEntity == null) {
            Iterable<LivingEntity> visible = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS)
                    .orElse(LivingTargetCache.empty())
                    .iterate(e -> true);

            // 主动攻击非 AbstractPiglinEntity 的实体
            for (LivingEntity target : visible) {
                if (!(target instanceof AbstractPiglinEntity) && piglin.canTarget(target)) {
                    cir.setReturnValue(Optional.of(target));
                    ((IPiglinEntity)piglin).setTargetEntity(target);
                    break;
                }
            }
        } else {
            if (!piglin.canTarget(livingEntity)) {
                ((IPiglinEntity)piglin).setTargetEntity(null);
            } else {
                cir.setReturnValue(Optional.of(livingEntity));
            }
        }
    }
    @Inject(method = "getNearestZombifiedPiglin", at = @At("HEAD"), cancellable = true)
    private static void getNearestZombifiedPiglin(PiglinEntity piglin, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
/*    @Inject(method = "sense", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void sense(ServerWorld world, LivingEntity entity, CallbackInfo ci, Brain brain, Optional<MobEntity> optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7, int i, List list, List list2) {
        LivingTargetCache livingTargetCache = (LivingTargetCache)brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
        for (LivingEntity livingEntity : livingTargetCache.iterate(livingEntityx -> true)) {
            if (!(livingEntity instanceof AbstractPiglinEntity)) {
                brain.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingEntity);
            }
        }
    }*/
}
