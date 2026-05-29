package com.kltyton.mob_battle.items.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MasterScepterItem extends Item {

    public MasterScepterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
/*        if (world.isClient) {
            // 只在客户端打开屏幕
            MinecraftClient.getInstance().execute(() ->
                    MinecraftClient.getInstance().setScreen(new MasterScepterScreen())
            );
            // 可选：播放使用动画
            user.swingHand(hand);
        }*/
        // success 表示消耗一次使用（防止默认行为）
        return ActionResult.SUCCESS;
    }
}
