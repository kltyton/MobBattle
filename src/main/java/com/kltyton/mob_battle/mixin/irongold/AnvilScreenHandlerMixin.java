package com.kltyton.mob_battle.mixin.irongold;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }


    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
    public void updateResult(ItemStack instance, int damage) {
        if (instance.isOf(ModItems.IRON_GOLD_SWORD)) {
            instance.setDamage(0);
        } else {
            instance.setDamage(damage);
        }
    }

    @Redirect(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 4)
    )
    private void capDiamondSwordAnvilCost(Property instance, int value) {
        if (this.input.getStack(0).isOf(ModItems.IRON_GOLD_SWORD)) {
            instance.set(Math.min(value, 60));
        } else {
            instance.set(value);
        }
    }

    @Redirect(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I", ordinal = 1)
    )
    private int allowCappedDiamondSwordAnvilCost(Property instance) {
        if (this.input.getStack(0).isOf(ModItems.IRON_GOLD_SWORD) && this.levelCost.get() <= 60) {
            return 39;
        }

        return instance.get();
    }
}
