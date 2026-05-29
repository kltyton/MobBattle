package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireWallScrollItem extends Item {
    public FireWallScrollItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClientSide) {
            FireWallEntity wall = new FireWallEntity(ModEntities.FIRE_WALL, world, user);
            wall.snapTo(user.getX(), user.getY(), user.getZ(), user.getYRot(), user.getXRot());
            world.addFreshEntity(wall);
        }
        // 增加玩家使用统计
        user.awardStat(Stats.ITEM_USED.get(this));

        // 消耗物品
        if (!user.getAbilities().instabuild) itemStack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}
