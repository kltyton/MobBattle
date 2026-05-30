package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {
    // PiglinBrainMixin.java
    @Inject(method = "findNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
    private static void getPreferredTarget(ServerLevel world,
                                           Piglin piglin,
                                           CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        Optional<? extends LivingEntity> opt = cir.getReturnValue();
        if (opt.isPresent() && (areInSameTeam(piglin, opt.get()) || isWearingPiglinLovedArmor(opt.get()))) {
            ((IPiglinEntity) piglin).setTargetEntity(null);
            cir.setReturnValue(Optional.empty());
            return;
        }

        if (cir.getReturnValue().isEmpty()) {
            Brain<Piglin> brain = piglin.getBrain();
            LivingEntity livingEntity = ((IPiglinEntity) piglin).getTargetEntity();
            if (livingEntity == null) {
                Iterable<LivingEntity> visible =
                        brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                                .orElse(NearestVisibleLivingEntities.empty())
                                .findAll(e -> true);

                for (LivingEntity target : visible) {
                    /* 新增：同队直接跳过 */
                    if (areInSameTeam(piglin, target) || isWearingPiglinLovedArmor(target)) continue;
                    if (!(target instanceof AbstractPiglin) && piglin.canAttack(target)) {
                        cir.setReturnValue(Optional.of(target));
                        ((IPiglinEntity) piglin).setTargetEntity(target);
                        break;
                    }
                }
            } else {
                if (!piglin.canAttack(livingEntity) || areInSameTeam(piglin, livingEntity) || isWearingPiglinLovedArmor(livingEntity)) {
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
    @Inject(method = "isNearZombified", at = @At("HEAD"), cancellable = true)
    private static void getNearestZombifiedPiglin(Piglin piglin, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
