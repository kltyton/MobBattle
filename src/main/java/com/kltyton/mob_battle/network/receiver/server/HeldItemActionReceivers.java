package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.piglin.PiglinCannonModes;
import com.kltyton.mob_battle.items.tool.sword.ChasingWindSwordItem;
import com.kltyton.mob_battle.network.packet.LeftClickPacket;
import com.kltyton.mob_battle.network.packet.PiglinCannonModePayload;
import com.kltyton.mob_battle.input.LeftClickDispatcher;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 手持物品驱动的输入/切换接收器（服务端）。
 *
 * <p>左键点击保持原样直调（Fabric 回调本就运行在服务端线程，旧实现无
 * {@code server.execute} 包装）；追风剑/猪灵炮形态切换同样直接在该线程处理，
 * 保留主手物品门并移除无意义的二次派发。
 *
 * <p>信任边界：两个接收器都以服务端持有的物品为准（左键分发仅在主手物品
 * 实现 {@code ModFabricItem} 时生效，形态切换仅作用于主手物品），不信任
 * 负载中的物品描述。本文件不新增权限或速率限制等推测性约束。
 */
public final class HeldItemActionReceivers {
    private HeldItemActionReceivers() {
    }

    /**
     * 左键点击接收器（注册顺序第 6 位，无 server.execute 包装，保持原样）。
     */
    public static void initLeftClick() {
        ServerPlayNetworking.registerGlobalReceiver(LeftClickPacket.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    LeftClickDispatcher.leftClick(player, payload.pressing(), true);
                }
        );
    }

    /**
     * 追风剑/猪灵炮形态切换接收器（注册顺序第 13 位）。
     */
    public static void initPiglinCannonMode() {
        ServerPlayNetworking.registerGlobalReceiver(PiglinCannonModePayload.ID, (payload, context) -> {
            //紫金套装效果
            ServerPlayer player = context.player();
            ServerLevel world = player.level();
            if (player.getMainHandItem().is(ModItems.CHASING_WIND_SWORD)) {
                ChasingWindSwordItem.Mode mode = ChasingWindSwordItem.toggleMode(player.getMainHandItem());
                if (!world.isClientSide()) {
                    player.sendOverlayMessage(ChasingWindSwordItem.modeMessage(mode));
                }
            } else if (player.getMainHandItem().is(ModItems.PIGLIN_CANNON)) {
                PiglinCannonModes.Mode mode = PiglinCannonModes.toggleMode(player.getMainHandItem());
                if (!world.isClientSide()) {
                    player.sendOverlayMessage(Component.literal(mode == PiglinCannonModes.Mode.FAST_FIRE ? "切换为速射形态" : "切换为重击模式"));
                }
            }
        });
    }
}
