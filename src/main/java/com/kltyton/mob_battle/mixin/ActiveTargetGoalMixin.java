package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.highbird.HighbirdAndEggEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdSBEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoal {
    public ActiveTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }
    @Shadow
    protected LivingEntity targetEntity;

    @Shadow
    protected abstract Box getSearchBox(double distance);

    @Inject(method = "findClosestTarget", at = @At("TAIL"))
    private void injectHighbirdBabyTarget(CallbackInfo ci) {
        // 如果已经找到目标，则跳过
        if (targetEntity != null) return;

        ServerWorld world = (ServerWorld) mob.getWorld();
        Box searchBox = getSearchBox(mob.getAttributeValue(EntityAttributes.FOLLOW_RANGE));
        if (!(mob instanceof HighbirdAndEggEntity)) {
            List<HighbirdAndEggEntity> babies = world.getEntitiesByClass(
                    HighbirdAndEggEntity.class,
                    searchBox,
                    baby -> {
                        if (mob instanceof TameableEntity tameable) {
                            return baby.isAlive() && baby instanceof HighbirdSBEntity && !tameable.isTamed();
                        } else {
                            return baby != mob && baby.isAlive() && baby instanceof HighbirdSBEntity;
                        }
                    });

            if (!babies.isEmpty()) {
                // 找到最近的 HighbirdBabyEntity
                babies.sort((a, b) -> {
                    double distA = mob.squaredDistanceTo(a);
                    double distB = mob.squaredDistanceTo(b);
                    return Double.compare(distA, distB);
                });
                this.targetEntity = babies.getFirst();
            }
        }
    }
}