package com.kltyton.mob_battle.mixin.net.minecraft.server.network;


import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.kltyton.mob_battle.event.ClearItemEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCreativeInventoryAction", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockCreativeProtectedItems(CreativeInventoryActionC2SPacket packet, CallbackInfo ci) {
        if (MobBattlePermissions.canUseProtectedContent(player)) return;

        ItemStack stack = packet.stack();
        if (ClearItemEvent.isModItem(stack)) {
            player.sendMessage(Text.literal("你没有权限获取该物品。"), false);
            ci.cancel();
        }
    }
}

