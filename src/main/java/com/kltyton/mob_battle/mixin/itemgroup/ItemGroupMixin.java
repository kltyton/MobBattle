package com.kltyton.mob_battle.mixin.itemgroup;

import com.kltyton.mob_battle.items.itemgroup.ClientTagManager;
import com.kltyton.mob_battle.network.packet.ItemGroupPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
public abstract class ItemGroupMixin {
    @Shadow
    public abstract Text getDisplayName();

    @Inject(method = "shouldDisplay", at = @At("RETURN"), cancellable = true)
    public void shouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (this.getDisplayName().equals(Text.translatable("itemGroup.mob_battle.main"))) {
            ClientPlayNetworking.send(new ItemGroupPayload(ClientTagManager.isShen));
            cir.setReturnValue(ClientTagManager.isShen);
        }
    }
}
