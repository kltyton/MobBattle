package com.kltyton.mob_battle.mixin.net.minecraft.item;

import com.kltyton.mob_battle.config.whitelist.ClientItemFilter;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroup.EntriesImpl.class)
public abstract class ItemGroupEntriesImplMixin {

    @Inject(method = "add(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemGroup$StackVisibility;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void mob_battle$filterCreativeEntries(ItemStack stack, ItemGroup.StackVisibility visibility, CallbackInfo ci) {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        if (ClientItemFilter.shouldHide(stack)) {
            ci.cancel();
        }
    }
}

