package com.kltyton.mob_battle.mixin.net.minecraft.item;

import com.kltyton.mob_battle.config.whitelist.ClientItemFilter;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTab$ItemDisplayBuilder")
public abstract class ItemGroupEntriesImplMixin {

    @Inject(method = "accept(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void mob_battle$filterCreativeEntries(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        if (ClientItemFilter.shouldHide(stack)) {
            ci.cancel();
        }
    }
}

