package com.kltyton.mob_battle.mixin.initgoals;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaiderEntity.class)
public abstract class RaiderEntityMixin extends PatrolEntity {
    protected RaiderEntityMixin(EntityType<? extends PatrolEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at =@At(value = "HEAD"))
    private void initGoals(CallbackInfo ci) {
        // 攻击所有活体实体，但排除自己和凋零
        this.targetSelector.add(3, new ActiveTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                true,
                true,
                this::isValidTarget
        ));
    }

    @Unique
    private boolean isValidTarget(LivingEntity target, ServerWorld world) {
        if ((Object) this instanceof WitchEntity witchEntity) {
            return !(target instanceof RaiderEntity) && !witchEntity.isTeammate(target) && !(target instanceof SilverfishEntity) && !(target instanceof CreeperEntity);
        }
        return !(target instanceof RaiderEntity) && !this.isTeammate(target);
    }
}
