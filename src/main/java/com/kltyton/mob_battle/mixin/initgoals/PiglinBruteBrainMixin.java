package com.kltyton.mob_battle.mixin.initgoals;

import com.google.common.collect.ImmutableList;
import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteBrain.class)
public abstract class PiglinBruteBrainMixin {

    @Shadow
    private static boolean isTarget(ServerWorld world, AbstractPiglinEntity piglin, LivingEntity target) {
        return false;
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    private static void getPreferredTarget(ServerWorld world, AbstractPiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Brain<?> brain = piglin.getBrain();
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
            if (!livingEntity.isAlive()) {
                ((IPiglinEntity)piglin).setTargetEntity(null);
            } else {
                cir.setReturnValue(Optional.of(livingEntity));
            }
        }
    }
    /**
     * @author Use CROSSBOW
     * @reason kltyton
     */
    @Overwrite
    private static void addFightActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(
                Activity.FIGHT,
                10,
                ImmutableList.<Task<? super PiglinBruteEntity>>of(
                        ForgetAttackTargetTask.create((world, target) -> !isTarget(world, piglinBrute, target)),
                        TaskTriggerer.runIf(PiglinBruteBrainMixin::isHoldingCrossbow, AttackTask.create(5, 0.75F)),
                        RangedApproachTask.create(1.0F),
                        MeleeAttackTask.create(20),
                        new CrossbowAttackTask()
                ),
                MemoryModuleType.ATTACK_TARGET
        );
    }

    @Unique
    private static boolean isHoldingCrossbow(PiglinBruteEntity piglinBrute) {
        return piglinBrute.isHolding(Items.CROSSBOW);
    }
}
