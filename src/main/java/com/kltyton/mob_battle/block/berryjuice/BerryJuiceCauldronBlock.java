package com.kltyton.mob_battle.block.berryjuice;

import com.kltyton.mob_battle.items.consumable.berryjuice.BerryJuiceItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 甜浆果汁的 1–3 级炼药锅。
 *
     * <p>26.1.2 的 {@link CauldronInteractions} 写入表不是公开扩展
 * API，因此这里在官方 {@code LayeredCauldronBlock} 的交互入口中实现专属状态机：
 * 玻璃瓶提取一级，甜浆果或汁瓶填充/增加一级，满级拒绝继续填充。所有世界状态
 * 修改只在服务端执行；客户端仍返回成功以保持原版交互预测。</p>
 */
public final class BerryJuiceCauldronBlock extends LayeredCauldronBlock {

    public BerryJuiceCauldronBlock(BlockBehaviour.Properties properties) {
        super(net.minecraft.world.level.biome.Biome.Precipitation.NONE, CauldronInteractions.EMPTY, properties);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (itemStack.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide()) {
                Item usedItem = itemStack.getItem();
                player.setItemInHand(
                        hand,
                        ItemUtils.createFilledResult(itemStack, player, new ItemStack(BerryJuiceItems.BERRY_JUICE))
                );
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(usedItem));
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        boolean isBerry = itemStack.is(Items.SWEET_BERRIES);
        boolean isJuice = BerryJuiceItems.BERRY_JUICE != null
                && itemStack.is(BerryJuiceItems.BERRY_JUICE);
        if ((!isBerry && !isJuice) || isFull(state)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            Item usedItem = itemStack.getItem();
            if (isJuice) {
                player.setItemInHand(
                        hand,
                        ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE))
                );
            } else {
                itemStack.consume(1, player);
            }
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(usedItem));
            level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
