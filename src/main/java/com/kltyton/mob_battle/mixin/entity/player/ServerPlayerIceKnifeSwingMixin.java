package com.kltyton.mob_battle.mixin.entity.player;

import com.kltyton.mob_battle.input.LeftClickDispatcher;
import com.kltyton.mob_battle.items.weapon.iceknife.IceKnifeItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 在原版服务端挥手重置攻击冷却之前，让冰刀按真实蓄力值处理空挥。 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerIceKnifeSwingMixin {
    /**
     * 26.1.2 的挥手包先于 tick 末尾的自定义左键包到达；后者到达时蓄力已被清零。
     * 沿用服务端左键分发及去重边界，实体攻击和随后到达的左键包不会重复发射。
     */
    @Inject(method = "swing", at = @At("HEAD"))
    private void mobBattle$fireChargedIceKnife(InteractionHand hand, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (hand == InteractionHand.MAIN_HAND && player.isAlive() && !player.isSpectator()
                && player.getMainHandItem().getItem() instanceof IceKnifeItem) {
            LeftClickDispatcher.leftClick(player, hand, true, true);
        }
    }
}
