package com.kltyton.mob_battle.items.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class MasterScepterItem extends Item {

    public MasterScepterItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
/*        if (world.isClient) {
            // 只在客户端打开屏幕
            MinecraftClient.getInstance().execute(() ->
                    MinecraftClient.getInstance().setScreen(new MasterScepterScreen())
            );
            // 可选：播放使用动画
            user.swingHand(hand);
        }*/
        // success 表示消耗一次使用（防止默认行为）
        return InteractionResult.SUCCESS;
    }
}
