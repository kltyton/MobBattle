package com.kltyton.mob_battle.mixin.witchentity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Witch.class)
public abstract class WitchEntityMixin extends Raider implements RangedAttackMob {
    @Shadow
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;

    protected WitchEntityMixin(EntityType<? extends Raider> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 5))
    public void initGoals(GoalSelector instance, int priority, Goal goal) {
        instance.addGoal(priority, new HurtByTargetGoal(this, Raider.class, Creeper.class, Silverfish.class));
    }
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 6))
    public void initGoals2(GoalSelector instance, int priority, Goal goal) {
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (target, world) ->
                this.hasActiveRaid() &&
                        target.getType() != EntityType.WITCH &&
                        target.getType() != EntityType.CREEPER &&
                        target.getType() != EntityType.SILVERFISH
        );
        this.targetSelector.addGoal(2, this.healRaidersGoal);
    }
}
