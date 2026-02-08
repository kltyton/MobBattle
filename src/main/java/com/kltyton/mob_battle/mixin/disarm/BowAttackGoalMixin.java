package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BowAttackGoal.class, CrossbowAttackGoal.class, ProjectileAttackGoal.class})
public class BowAttackGoalMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/RangedAttackMob;shootAt(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void disarm(RangedAttackMob instance, LivingEntity livingEntity, float v) {
        if (instance instanceof LivingEntity living) {
            if (!ModSkillEntityType.canSkill(living)) return;
        }
        instance.shootAt(livingEntity, v);
    }
}
