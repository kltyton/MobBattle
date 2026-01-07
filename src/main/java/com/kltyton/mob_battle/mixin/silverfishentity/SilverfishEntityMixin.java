package com.kltyton.mob_battle.mixin.silverfishentity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SilverfishEntity.class)
public abstract class SilverfishEntityMixin extends HostileEntity {
    protected SilverfishEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 5))
    public void initGoals(GoalSelector instance, int priority, Goal goal) {
        instance.add(priority,  new RevengeGoal(this, CreeperEntity.class, WitchEntity.class).setGroupRevenge(CreeperEntity.class, WitchEntity.class));
    }
    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 6))
    public void initGoals2(GoalSelector instance, int priority, Goal goal) {
        instance.add(priority, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, (entity, world) -> !(entity instanceof WitchEntity) && !(entity instanceof CreeperEntity) && !(entity instanceof SilverfishEntity)));
    }
}
