package com.kltyton.mob_battle.mixin.initgoals;

import com.google.common.collect.ImmutableList;
import com.kltyton.mob_battle.accessor.IPiglinEntity;
import com.kltyton.mob_battle.accessor.IPiglinBruteSpearMode;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.Set;

@Mixin(PiglinBruteAi.class)
public abstract class PiglinBruteBrainMixin {

    @Shadow
    private static boolean isNearestValidAttackTarget(ServerLevel world, AbstractPiglin piglin, LivingEntity target) {
        return false;
    }

    /**
     * @author Mob Battle
     * @reason PiglinBrute's vanilla 26.1.2 AI reads ANGRY_AT, but the brute brain in this project does not register that memory.
     */
    @Overwrite
    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel world, AbstractPiglin piglin) {
        IPiglinEntity piglinAccess = (IPiglinEntity) piglin;
        LivingEntity rememberedTarget = piglinAccess.getTargetEntity();
        if (isUsableAttackTarget(piglin, rememberedTarget)) {
            piglin.setTarget(rememberedTarget);
            return Optional.of(rememberedTarget);
        }
        if (rememberedTarget != null) {
            piglinAccess.setTargetEntity(null);
            piglin.setTarget(null);
        }

        Brain<?> brain = piglin.getBrain();
        Optional<? extends LivingEntity> playerTarget = getMemorySafely(brain, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        if (playerTarget.isPresent() && isUsableAttackTarget(piglin, playerTarget.get())) {
            piglinAccess.setTargetEntity(playerTarget.get());
            piglin.setTarget(playerTarget.get());
            return playerTarget;
        }

        Optional<? extends LivingEntity> nemesisTarget = getMemorySafely(brain, MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
        if (nemesisTarget.isPresent() && isUsableAttackTarget(piglin, nemesisTarget.get())) {
            piglinAccess.setTargetEntity(nemesisTarget.get());
            piglin.setTarget(nemesisTarget.get());
            return nemesisTarget;
        }

        Iterable<LivingEntity> visible = getMemorySafely(brain, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .orElse(NearestVisibleLivingEntities.empty())
                .findAll(entity -> true);
        for (LivingEntity target : visible) {
            if (isUsableAttackTarget(piglin, target)) {
                piglinAccess.setTargetEntity(target);
                piglin.setTarget(target);
                return Optional.of(target);
            }
        }

        piglin.setTarget(null);
        return Optional.empty();
    }

    /**
     * @author Mob Battle
     * @reason Avoid vanilla Piglin anger broadcast, which can fetch unregistered ANGRY_AT memory from PiglinBrute brains.
     */
    @Overwrite
    protected static void wasHurtBy(ServerLevel world, PiglinBrute piglin, LivingEntity attacker) {
        if (!isUsableAttackTarget(piglin, attacker)) {
            return;
        }

        ((IPiglinEntity) piglin).setTargetEntity(attacker);
        piglin.setTarget(attacker);
        Brain<?> brain = piglin.getBrain();
        eraseMemorySafely(brain, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        setMemorySafely(brain, MemoryModuleType.ATTACK_TARGET, attacker);
        piglin.setAggressive(true);
    }

    /**
     * @author Mob Battle
     * @reason Keep PiglinBrute anger target on ATTACK_TARGET instead of unregistered ANGRY_AT.
     */
    @Overwrite
    protected static void setAngerTarget(PiglinBrute piglin, LivingEntity target) {
        if (!isUsableAttackTarget(piglin, target)) {
            return;
        }

        ((IPiglinEntity) piglin).setTargetEntity(target);
        piglin.setTarget(target);
        Brain<?> brain = piglin.getBrain();
        eraseMemorySafely(brain, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        setMemorySafely(brain, MemoryModuleType.ATTACK_TARGET, target);
    }

    @Unique
    private static boolean isUsableAttackTarget(AbstractPiglin piglin, LivingEntity target) {
        return target != null
                && target.isAlive()
                && target != piglin
                && !(target instanceof AbstractPiglin)
                && !areInSameTeam(piglin, target)
                && !isWearingPiglinLovedArmor(target)
                && piglin.canAttack(target);
    }

    @Unique
    private static <T> Optional<T> getMemorySafely(Brain<?> brain, MemoryModuleType<T> memoryType) {
        try {
            return brain.getMemory(memoryType);
        } catch (IllegalStateException ignored) {
            return Optional.empty();
        }
    }

    @Unique
    private static <T> void setMemorySafely(Brain<?> brain, MemoryModuleType<T> memoryType, T value) {
        try {
            brain.setMemory(memoryType, value);
        } catch (IllegalStateException ignored) {
            // The custom brute brain only needs best-effort memory writes.
        }
    }

    @Unique
    private static void eraseMemorySafely(Brain<?> brain, MemoryModuleType<?> memoryType) {
        try {
            brain.eraseMemory(memoryType);
        } catch (IllegalStateException ignored) {
            // Some vanilla Piglin memories are intentionally absent from the custom brute brain.
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ActivityData<PiglinBrute> initFightActivity(PiglinBrute piglinBrute) {
        ImmutableList<BehaviorControl<? super PiglinBrute>> behaviors = ImmutableList.of(
                StopAttackingIfTargetInvalid.<PiglinBrute>create((world, target) -> !isNearestValidAttackTarget(world, piglinBrute, target)),
                BehaviorBuilder.<PiglinBrute>triggerIf(PiglinBruteBrainMixin::isHoldingCrossbow, BackUpIfTooClose.create(5, 0.75F)),
                skipWhenUsingSpear(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F)),
                MeleeAttack.create(20),
                (BehaviorControl<? super PiglinBrute>) (BehaviorControl) new CrossbowAttack()
        );
        return ActivityData.create(
                Activity.FIGHT,
                10,
                behaviors,
                MemoryModuleType.ATTACK_TARGET
        );
    }

    @Unique
    private static boolean isHoldingCrossbow(PiglinBrute piglinBrute) {
        return piglinBrute.isHolding(Items.CROSSBOW)
                && piglinBrute.canUseNonMeleeWeapon(piglinBrute.getWeaponItem());
    }

    @Unique
    private static boolean isNotUsingSpear(PiglinBrute piglinBrute) {
        return !piglinBrute.getMainHandItem().has(DataComponents.KINETIC_WEAPON)
                || !((IPiglinBruteSpearMode) piglinBrute).mobBattle$usesSpearAsItem();
    }

    @Unique
    private static BehaviorControl<PiglinBrute> skipWhenUsingSpear(BehaviorControl<Mob> delegate) {
        return new BehaviorControl<>() {
            @Override
            public Behavior.Status getStatus() {
                return delegate.getStatus();
            }

            @Override
            public Set<MemoryModuleType<?>> getRequiredMemories() {
                return delegate.getRequiredMemories();
            }

            @Override
            public boolean tryStart(ServerLevel level, PiglinBrute body, long timestamp) {
                return isNotUsingSpear(body) && delegate.tryStart(level, body, timestamp);
            }

            @Override
            public void tickOrStop(ServerLevel level, PiglinBrute body, long timestamp) {
                if (isNotUsingSpear(body)) {
                    delegate.tickOrStop(level, body, timestamp);
                } else {
                    delegate.doStop(level, body, timestamp);
                }
            }

            @Override
            public void doStop(ServerLevel level, PiglinBrute body, long timestamp) {
                delegate.doStop(level, body, timestamp);
            }

            @Override
            public String debugString() {
                return delegate.debugString();
            }
        };
    }
}
