package com.kltyton.mob_battle.mixin.net.minecraft.server.network;


import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.kltyton.mob_battle.event.ClearItemEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleSetCreativeModeSlot", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockCreativeProtectedItems(ServerboundSetCreativeModeSlotPacket packet, CallbackInfo ci) {
        if (MobBattlePermissions.canUseProtectedContent(player)) return;

        ItemStack stack = packet.itemStack();
        if (ClearItemEvent.isModItem(stack)) {
            player.displayClientMessage(Component.literal("你没有权限获取该物品。"), false);
            ci.cancel();
        }
    }
}

