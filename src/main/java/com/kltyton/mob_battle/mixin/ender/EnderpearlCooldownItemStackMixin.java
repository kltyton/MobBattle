package com.kltyton.mob_battle.mixin.ender;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class EnderpearlCooldownItemStackMixin {
    private static final int MOB_BATTLE_ENDER_PEARL_COOLDOWN_TICKS = 50 * 20;

    @Inject(method = "applyAfterUseComponentSideEffects", at = @At("TAIL"))
    private void mobBattle$extendEnderPearlCooldown(LivingEntity user, ItemStack stackBeforeUsing, CallbackInfoReturnable<ItemStack> cir) {
        if (stackBeforeUsing.is(Items.ENDER_PEARL) && user instanceof Player player) {
            player.getCooldowns().addCooldown(stackBeforeUsing, MOB_BATTLE_ENDER_PEARL_COOLDOWN_TICKS);
        }
    }
}
