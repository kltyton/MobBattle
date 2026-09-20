package com.kltyton.mob_battle.block.berryjuice;

import com.kltyton.mob_battle.items.consumable.berryjuice.BerryJuiceItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 空炼药锅的甜浆果汁填充入口。
 *
 * <p>原版空炼药锅不会经过甜浆果汁方块自身的 {@code useItemOn}，所以必须在
 * Fabric 的方块使用事件边界上仅拦截空炼药锅。其它方块和其它物品返回 PASS，
 * 不改变共享事件处理器的行为；真实方块状态修改仍只在服务端执行。</p>
 */
public final class BerryJuiceCauldronInteractions {

    private BerryJuiceCauldronInteractions() {
    }

    public static void init() {
        UseBlockCallback.EVENT.register(BerryJuiceCauldronInteractions::fillEmptyCauldron);
    }

    private static InteractionResult fillEmptyCauldron(
            Player player,
            Level level,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        BlockPos pos = hitResult.getBlockPos();
        if (!level.getBlockState(pos).is(Blocks.CAULDRON)) {
            return InteractionResult.PASS;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        boolean isBerry = itemStack.is(Items.SWEET_BERRIES);
        boolean isJuice = BerryJuiceItems.BERRY_JUICE != null
                && itemStack.is(BerryJuiceItems.BERRY_JUICE);
        if (!isBerry && !isJuice) {
            return InteractionResult.PASS;
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
            level.setBlockAndUpdate(pos, BerryJuiceBlocks.BERRY_JUICE_CAULDRON.defaultBlockState());
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
