package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RangedBowAttackGoal.class, RangedCrossbowAttackGoal.class, RangedAttackGoal.class})
public class BowAttackGoalMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/RangedAttackMob;performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    public void disarm(RangedAttackMob instance, LivingEntity livingEntity, float v) {
        if (instance instanceof LivingEntity living) {
            if (!ModSkillEntityType.canSkill(living)) return;
        }
        instance.performRangedAttack(livingEntity, v);
    }
}
