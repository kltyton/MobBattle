package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FireWallScrollItem extends Item {
    public FireWallScrollItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClient) {
            FireWallEntity wall = new FireWallEntity(ModEntities.FIRE_WALL, world, user);
            wall.refreshPositionAndAngles(user.getX(), user.getY(), user.getZ(), user.getYaw(), user.getPitch());
            world.spawnEntity(wall);
        }
        // 增加玩家使用统计
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // 消耗物品
        if (!user.getAbilities().creativeMode) itemStack.decrement(1);

        return ActionResult.SUCCESS;
    }
}
