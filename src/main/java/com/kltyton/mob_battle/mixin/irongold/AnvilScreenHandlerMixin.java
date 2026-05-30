package com.kltyton.mob_battle.mixin.irongold;

import com.kltyton.mob_battle.items.ModItems;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {
    @Shadow
    @Final
    private DataSlot cost;

    public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }


    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    public void updateResult(ItemStack instance, int damage) {
        if (instance.is(ModItems.IRON_GOLD_SWORD)) {
            instance.setDamageValue(0);
        } else {
            instance.setDamageValue(damage);
        }
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 4)
    )
    private void capDiamondSwordAnvilCost(DataSlot instance, int value) {
        if (this.inputSlots.getItem(0).is(ModItems.IRON_GOLD_SWORD)) {
            instance.set(Math.min(value, 60));
        } else {
            instance.set(value);
        }
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I", ordinal = 1)
    )
    private int allowCappedDiamondSwordAnvilCost(DataSlot instance) {
        if (this.inputSlots.getItem(0).is(ModItems.IRON_GOLD_SWORD) && this.cost.get() <= 60) {
            return 39;
        }

        return instance.get();
    }
}
