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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    // PiglinBrainMixin.java
    @Inject(method = "getPreferredTarget", at = @At("RETURN"), cancellable = true)
    private static void getPreferredTarget(ServerWorld world,
                                           PiglinEntity piglin,
                                           CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        Optional<? extends LivingEntity> opt = cir.getReturnValue();
        if (opt.isPresent() && areInSameTeam(piglin, opt.get())) {
            ((IPiglinEntity) piglin).setTargetEntity(null);
            cir.setReturnValue(Optional.empty());
            return;
        }

        if (!cir.getReturnValue().isPresent()) {
            Brain<PiglinEntity> brain = piglin.getBrain();
            LivingEntity livingEntity = ((IPiglinEntity) piglin).getTargetEntity();
            if (livingEntity == null) {
                Iterable<LivingEntity> visible =
                        brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS)
                                .orElse(LivingTargetCache.empty())
                                .iterate(e -> true);

                for (LivingEntity target : visible) {
                    /* 新增：同队直接跳过 */
                    if (areInSameTeam(piglin, target)) continue;
                    if (!(target instanceof AbstractPiglinEntity) && piglin.canTarget(target)) {
                        cir.setReturnValue(Optional.of(target));
                        ((IPiglinEntity) piglin).setTargetEntity(target);
                        break;
                    }
                }
            } else {
                if (!piglin.canTarget(livingEntity) || areInSameTeam(piglin, livingEntity)) {
                    ((IPiglinEntity) piglin).setTargetEntity(null);
                } else {
                    cir.setReturnValue(Optional.of(livingEntity));
                }
            }
        }
    }
    @Unique
    private static boolean areInSameTeam(LivingEntity a, LivingEntity b) {
        return a.isTeammate(b);
    }
    @Inject(method = "getNearestZombifiedPiglin", at = @At("HEAD"), cancellable = true)
    private static void getNearestZombifiedPiglin(PiglinEntity piglin, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
