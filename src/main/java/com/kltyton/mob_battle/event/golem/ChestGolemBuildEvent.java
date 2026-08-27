package com.kltyton.mob_battle.event.golem;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.golem.ChestGolemEntity;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ChestGolemBuildEvent {
    private ChestGolemBuildEvent() {
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(world instanceof ServerLevel level) || !player.getItemInHand(hand).is(Items.CHEST)) {
                return InteractionResult.PASS;
            }

            BlockPos clickedPos = hitResult.getBlockPos();
            BlockPos placedPos = clickedPos.relative(hitResult.getDirection());
            ServerTickScheduler.schedule(level.getServer(), 1, () -> {
                if (!trySpawn(level, placedPos, player instanceof ServerPlayer serverPlayer ? serverPlayer : null)) {
                    trySpawn(level, clickedPos, player instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                }
            });
            return InteractionResult.PASS;
        });
    }

    private static boolean trySpawn(ServerLevel level, BlockPos topPos, ServerPlayer player) {
        if (!level.getBlockState(topPos).is(Blocks.CHEST)) {
            return false;
        }
        return trySpawnWithArms(level, topPos, Direction.EAST, player)
                || trySpawnWithArms(level, topPos, Direction.NORTH, player);
    }

    private static boolean trySpawnWithArms(ServerLevel level, BlockPos topPos, Direction armDirection, ServerPlayer player) {
        BlockPos bodyPos = topPos.below();
        BlockPos legPos = bodyPos.below();
        BlockPos chestPos = bodyPos.relative(armDirection.getClockWise());
        BlockPos chestPos2 = bodyPos.relative(armDirection.getCounterClockWise());
        BlockPos armA = bodyPos.relative(armDirection);
        BlockPos armB = bodyPos.relative(armDirection.getOpposite());
        if (!isPlanks(level, bodyPos) || !isPlanks(level, legPos) || !isPlanks(level, armA) || !isPlanks(level, armB) || !(isVines(level, chestPos) || isVines(level, chestPos2))) {
            return false;
        }

        ChestGolemEntity golem = ModEntities.CHEST_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
        if (golem == null) {
            return false;
        }

        clear(level, topPos);
        clear(level, bodyPos);
        clear(level, legPos);
        clear(level, armA);
        clear(level, armB);

        golem.setPlayerCreated(true);
        golem.snapTo(topPos.getX() + 0.5D, topPos.getY() + 0.05D, topPos.getZ() + 0.5D, 0.0F, 0.0F);
        golem.finalizeSpawn(level, level.getCurrentDifficultyAt(golem.blockPosition()), EntitySpawnReason.TRIGGERED, null);
        level.addFreshEntity(golem);
        if (player != null) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
        }
        updateNeighbors(level, topPos, bodyPos, legPos, armA, armB);
        return true;
    }

    private static boolean isPlanks(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.OAK_PLANKS);
    }
    private static boolean isVines(ServerLevel level, BlockPos pos) {
        Mob_battle.LOGGER.info(level.getBlockState(pos).toString());
        return level.getBlockState(pos).is(Blocks.VINE);
    }

    private static void clear(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        level.levelEvent(2001, pos, Block.getId(state));
    }

    private static void updateNeighbors(ServerLevel level, BlockPos... positions) {
        for (BlockPos pos : positions) {
            level.updateNeighborsAt(pos, Blocks.AIR);
        }
    }
}
