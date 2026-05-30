package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.highbird.HighbirdAndEggEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdSBEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TargetGoal {
    public ActiveTargetGoalMixin(Mob mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }
    @Shadow
    protected LivingEntity target;

    @Shadow
    protected abstract AABB getTargetSearchArea(double distance);

    @Inject(method = "findTarget", at = @At("TAIL"))
    private void injectHighbirdBabyTarget(CallbackInfo ci) {
        // 如果已经找到目标，则跳过
        if (target != null) return;

        ServerLevel world = (ServerLevel) mob.level();
        AABB searchBox = getTargetSearchArea(mob.getAttributeValue(Attributes.FOLLOW_RANGE));
        if (!(mob instanceof HighbirdAndEggEntity)) {
            List<HighbirdAndEggEntity> babies = world.getEntitiesOfClass(
                    HighbirdAndEggEntity.class,
                    searchBox,
                    baby -> {
                        if (mob instanceof TamableAnimal tameable) {
                            return baby.isAlive() && baby instanceof HighbirdSBEntity && !tameable.isTame();
                        } else {
                            return baby != mob && baby.isAlive() && baby instanceof HighbirdSBEntity;
                        }
                    });

            if (!babies.isEmpty()) {
                // 找到最近的 HighbirdBabyEntity
                babies.sort((a, b) -> {
                    double distA = mob.distanceToSqr(a);
                    double distB = mob.distanceToSqr(b);
                    return Double.compare(distA, distB);
                });
                this.target = babies.getFirst();
            }
        }
    }
}