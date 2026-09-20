package com.kltyton.mob_battle.input;

import com.kltyton.mob_battle.items.ModFabricItem;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** 左键输入的客户端/服务端统一分发边界；只去除同一游戏 tick 的重复入口。 */
public final class LeftClickDispatcher {
    private static final Map<Player, Long> CLIENT_LAST_START_TICKS =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Player, Long> SERVER_LAST_START_TICKS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private LeftClickDispatcher() {
    }

    public static void leftClick(Player player, boolean isPressed, boolean isServer) {
        leftClick(player, InteractionHand.MAIN_HAND, isPressed, isServer);
    }

    /**
     * 分发指定手的左键边沿。攻击实体回调使用实际回调手，网络空挥仍使用主手。
     */
    public static void leftClick(Player player, InteractionHand hand, boolean isPressed, boolean isServer) {
        ItemStack stack = player.getItemInHand(hand);
        Map<Player, Long> lastStartTicks = isServer ? SERVER_LAST_START_TICKS : CLIENT_LAST_START_TICKS;
        if (!isPressed) {
            lastStartTicks.remove(player);
            dispatchStop(player, stack, isServer);
            return;
        }
        if (stack.getItem() instanceof ModFabricItem modFabricItem) {
            long gameTime = player.level().getGameTime();
            synchronized (lastStartTicks) {
                if (lastStartTicks.getOrDefault(player, Long.MIN_VALUE) == gameTime) {
                    return;
                }
                lastStartTicks.put(player, gameTime);
            }
            modFabricItem.onLeftClickStart(player, stack, isServer);
        }
    }

    private static void dispatchStop(Player player, ItemStack stack, boolean isServer) {
        if (stack.getItem() instanceof ModFabricItem modFabricItem) {
            modFabricItem.onLeftClickStop(player, stack, isServer);
        }
    }
}
