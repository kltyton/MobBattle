package com.kltyton.mob_battle.mixin.ender;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderpearlItem.class)
public class EnderpearlItemMixin {
    private static final int MOB_BATTLE_ENDER_PEARL_COOLDOWN_TICKS = 50 * 20;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void blockEnderPearlWhileCoolingDown(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "use", at = @At("TAIL"))
    private void addEnderPearlCooldown(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().consumesAction()) {
            player.getCooldowns().addCooldown(Items.ENDER_PEARL.getDefaultInstance(), MOB_BATTLE_ENDER_PEARL_COOLDOWN_TICKS);
        }
    }
}
