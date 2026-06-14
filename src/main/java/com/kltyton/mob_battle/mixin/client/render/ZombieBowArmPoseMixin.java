package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidMobRenderer.class)
public abstract class ZombieBowArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getZombieBowArmPose(Mob mob, HumanoidArm arm, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (mob instanceof Zombie zombie
                && zombie.getMainArm() == arm
                && zombie.isAggressive()
                && zombie.getMainHandItem().is(Items.BOW)
                && zombie.canUseNonMeleeWeapon(zombie.getMainHandItem())) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
