package com.kltyton.mob_battle.mixin.crossbow;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("RETURN"), cancellable = true)
    private static void getArmPose(Player player, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (stack.is(ModItems.VS_SNIPE)) {
            ItemUseAnimation useAction = stack.getUseAnimation();
            if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
                if (useAction == ItemUseAnimation.CROSSBOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                }
            } else {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
            }
        }
    }
}
