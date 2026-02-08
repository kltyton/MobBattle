package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowAttackTask.class)
public abstract class MobEntityDisarmMixin {
    @Redirect(method = "tickState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/RangedAttackMob;shootAt(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void disarm(RangedAttackMob instance, LivingEntity livingEntity, float v) {
        if (instance instanceof LivingEntity living) {
            if (!ModSkillEntityType.canSkill(living)) return;
        }
        instance.shootAt(livingEntity, v);
    }
}
