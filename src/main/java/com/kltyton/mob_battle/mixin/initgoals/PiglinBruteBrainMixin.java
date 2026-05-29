package com.kltyton.mob_battle.mixin.initgoals;

import com.google.common.collect.ImmutableList;
import com.kltyton.mob_battle.accessor.IPiglinEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteAi.class)
public abstract class PiglinBruteBrainMixin {

    @Shadow
    private static boolean isNearestValidAttackTarget(ServerLevel world, AbstractPiglin piglin, LivingEntity target) {
        return false;
    }

    // PiglinBruteBrainMixin.java
    @Inject(method = "findNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
    private static void getPreferredTarget(ServerLevel world,
                                           AbstractPiglin piglin,
                                           CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        Optional<? extends LivingEntity> opt = cir.getReturnValue();
        if (opt.isPresent() && (areInSameTeam(piglin, opt.get()) || isWearingPiglinLovedArmor(opt.get()))) {
            ((IPiglinEntity) piglin).setTargetEntity(null);
            cir.setReturnValue(Optional.empty());
            return;
        }

        if (cir.getReturnValue().isEmpty()) {
            Brain<?> brain = piglin.getBrain();
            LivingEntity livingEntity = ((IPiglinEntity) piglin).getTargetEntity();
            if (livingEntity == null) {
                Iterable<LivingEntity> visible =
                        brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                                .orElse(NearestVisibleLivingEntities.empty())
                                .findAll(e -> true);

                for (LivingEntity target : visible) {
                    if (areInSameTeam(piglin, target) || isWearingPiglinLovedArmor(target)) continue;   // 同队跳过
                    if (!(target instanceof AbstractPiglin) && piglin.canAttack(target)) {
                        cir.setReturnValue(Optional.of(target));
                        ((IPiglinEntity) piglin).setTargetEntity(target);
                        break;
                    }
                }
            } else {
                if (!livingEntity.isAlive() || areInSameTeam(piglin, livingEntity) || isWearingPiglinLovedArmor(livingEntity)) {
                    ((IPiglinEntity) piglin).setTargetEntity(null);
                } else {
                    cir.setReturnValue(Optional.of(livingEntity));
                }
            }
        }
    }
    @Unique
    private static boolean areInSameTeam(LivingEntity a, LivingEntity b) {
        return a.isAlliedTo(b);
    }
    @Unique
    private static boolean isWearingPiglinLovedArmor(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.is(ItemTags.PIGLIN_SAFE_ARMOR) || stack.is(ItemTags.PIGLIN_LOVED)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @author Use CROSSBOW
     * @reason kltyton
     */
    @Overwrite
    private static void initFightActivity(PiglinBrute piglinBrute, Brain<PiglinBrute> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(
                Activity.FIGHT,
                10,
                ImmutableList.<BehaviorControl<? super PiglinBrute>>of(
                        StopAttackingIfTargetInvalid.create((world, target) -> !isNearestValidAttackTarget(world, piglinBrute, target)),
                        BehaviorBuilder.triggerIf(PiglinBruteBrainMixin::isHoldingCrossbow, BackUpIfTooClose.create(5, 0.75F)),
                        SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
                        MeleeAttack.create(20),
                        new CrossbowAttack()
                ),
                MemoryModuleType.ATTACK_TARGET
        );
    }

    @Unique
    private static boolean isHoldingCrossbow(PiglinBrute piglinBrute) {
        return piglinBrute.isHolding(Items.CROSSBOW);
    }
}
