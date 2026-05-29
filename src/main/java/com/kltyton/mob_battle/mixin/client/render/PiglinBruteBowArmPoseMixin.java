package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidMobRenderer.class)
public abstract class PiglinBruteBowArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getPiglinBruteBowArmPose(Mob entity, HumanoidArm arm, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!(entity instanceof PiglinBrute piglinBrute)) return;
        if (!piglinBrute.isUsingItem() || !(piglinBrute.getUseItem().getItem() instanceof BowItem)) return;

        HumanoidArm activeArm = piglinBrute.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? piglinBrute.getMainArm()
                : piglinBrute.getMainArm().getOpposite();
        if (arm == activeArm) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
