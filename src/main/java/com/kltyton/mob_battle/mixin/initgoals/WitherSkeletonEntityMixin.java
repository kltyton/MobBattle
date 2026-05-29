package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkeleton.class)
public abstract class WitherSkeletonEntityMixin extends AbstractSkeleton {
    protected WitherSkeletonEntityMixin(EntityType<? extends AbstractSkeleton> entityType, Level world) {
        super(entityType, world);
    }
    @Inject(method = "registerGoals", at =@At(value = "HEAD"))
    private void initGoals(CallbackInfo ci) {
        // 攻击所有活体实体，但排除自己和凋零
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                true,
                true,
                this::isValidTarget
        ));
    }

    @Unique
    private boolean isValidTarget(LivingEntity target, ServerLevel world) {
        if (this.isAlliedTo(target)) {
            return false;
        }
        if (this instanceof IModSkullEntity) {
            return !(target instanceof IModSkullEntity);
        }
        return !(target instanceof WitherBoss || target instanceof WitherSkeleton);
    }
}
