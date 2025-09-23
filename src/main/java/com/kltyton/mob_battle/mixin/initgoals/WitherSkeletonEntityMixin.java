package com.kltyton.mob_battle.mixin.initgoals;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkeletonEntity.class)
public abstract class WitherSkeletonEntityMixin extends AbstractSkeletonEntity {
    protected WitherSkeletonEntityMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
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
        return !(target instanceof WitherEntity || target instanceof WitherSkeletonEntity);
    }
}
