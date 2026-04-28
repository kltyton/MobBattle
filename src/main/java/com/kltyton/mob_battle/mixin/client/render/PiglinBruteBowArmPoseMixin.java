package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BipedEntityRenderer.class)
public abstract class PiglinBruteBowArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getPiglinBruteBowArmPose(MobEntity entity, Arm arm, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (!(entity instanceof PiglinBruteEntity piglinBrute)) return;
        if (!piglinBrute.isUsingItem() || !(piglinBrute.getActiveItem().getItem() instanceof BowItem)) return;

        Arm activeArm = piglinBrute.getActiveHand() == Hand.MAIN_HAND
                ? piglinBrute.getMainArm()
                : piglinBrute.getMainArm().getOpposite();
        if (arm == activeArm) {
            cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
