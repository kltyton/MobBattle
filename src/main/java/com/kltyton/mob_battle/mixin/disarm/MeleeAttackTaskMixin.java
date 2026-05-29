package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({MeleeAttackGoal.class})
public class MeleeAttackTaskMixin {
    @Redirect(method = "checkAndPerformAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;doHurtTarget(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean disarm(PathfinderMob instance, ServerLevel world, Entity entity) {
        if (instance instanceof LivingEntity living) {
            if (!ModSkillEntityType.canSkill(living)) return false;
        }
        return instance.doHurtTarget(world, entity);
    }
}
