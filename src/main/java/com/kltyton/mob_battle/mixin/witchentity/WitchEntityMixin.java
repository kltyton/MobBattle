package com.kltyton.mob_battle.mixin.witchentity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.RaidGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity implements RangedAttackMob {
    @Shadow
    private RaidGoal<RaiderEntity> raidGoal;

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 5))
    public void initGoals(GoalSelector instance, int priority, Goal goal) {
        instance.add(priority, new RevengeGoal(this, RaiderEntity.class, CreeperEntity.class, SilverfishEntity.class));
    }
    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 6))
    public void initGoals2(GoalSelector instance, int priority, Goal goal) {
        this.raidGoal = new RaidGoal<>(this, RaiderEntity.class, true, (target, world) ->
                this.hasActiveRaid() &&
                        target.getType() != EntityType.WITCH &&
                        target.getType() != EntityType.CREEPER &&
                        target.getType() != EntityType.SILVERFISH
        );
        this.targetSelector.add(2, this.raidGoal);
    }
}
