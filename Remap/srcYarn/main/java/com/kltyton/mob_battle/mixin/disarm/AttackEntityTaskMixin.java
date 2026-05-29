package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.FrogEatEntityTask;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FrogEatEntityTask.class)
public class AttackEntityTaskMixin {
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FrogEntity;tryAttack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)Z"))
    public boolean disarm(FrogEntity instance, ServerWorld world, Entity entity) {
        if (instance instanceof LivingEntity living) {
            if (!ModSkillEntityType.canSkill(living)) return false;
        }
        return instance.tryAttack(world, entity);
    }
}
