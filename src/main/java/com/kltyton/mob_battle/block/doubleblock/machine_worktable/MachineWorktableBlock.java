package com.kltyton.mob_battle.block.doubleblock.machine_worktable;

import com.kltyton.mob_battle.block.doubleblock.DoubleBlock;
import com.kltyton.mob_battle.client.screen.machine_worktable.MechanicalWorktableScreenHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MachineWorktableBlock extends DoubleBlock {
    private static final Component TITLE = Component.translatable("container.crafting");
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            Shapes.box(0.125, 0, 0.125, 0.25, 0.5625, 0.25),
            Shapes.box(0.125, 0, 0.75, 0.25, 0.5625, 0.875),
            Shapes.box(0.75, 0, 0.75, 0.875, 0.5625, 0.875),
            Shapes.box(0.75, 0, 0.125, 0.875, 0.5625, 0.25),
            Shapes.box(0, 0.5625, 0.0625, 1, 0.6875, 0.1875),
            Shapes.box(0, 0.5625, 0.8125, 1, 0.6875, 0.9375),
            Shapes.box(0.0625, 0.5625, 0, 0.1875, 0.6875, 1),
            Shapes.box(0.8125, 0.5625, 0, 0.9375, 0.6875, 1),
            Shapes.box(0.25, 0.75, 0.25, 0.75, 0.875, 0.75),
            Shapes.box(0.1875, 0.875, 0.1875, 0.8125, 1, 0.8125),
            Shapes.box(0.4375, 0.625, 0.75, 0.5625, 0.875, 0.8125),
            Shapes.box(0.1875, 0.625, 0.4375, 0.25, 0.875, 0.5625),
            Shapes.box(0.75, 0.625, 0.4375, 0.8125, 0.875, 0.5625),
            Shapes.box(0.4375, 0.625, 0.1875, 0.5625, 0.875, 0.25)
    );
    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.375, 0.8125),
            Shapes.box(0.46875, 0.375, 0.46875, 0.53125, 0.75, 0.53125),
            Shapes.box(0.4375, 0.75, 0.375, 0.5625, 0.8125, 0.4375),
            Shapes.box(0.375, 0.75, 0.4375, 0.625, 0.8125, 0.5625),
            Shapes.box(0.4375, 0.75, 0.5625, 0.5625, 0.8125, 0.625),
            Shapes.box(0.3125, 0.375, 0.5625, 0.4375, 0.4375, 0.6875),
            Shapes.box(0.5625, 0.375, 0.3125, 0.6875, 0.625, 0.4375)
    );
    public MachineWorktableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getLowerShape() {
        return LOWER_SHAPE;
    }

    @Override
    public VoxelShape getUpperShape() {
        return UPPER_SHAPE;
    }

    @Override
    protected MapCodec<? extends MachineWorktableBlock> codec() {
        return simpleCodec(MachineWorktableBlock::new);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockState lowerState = world.getBlockState(lowerPos);
            if (lowerState.is(this)) {
                player.openMenu(lowerState.getMenuProvider(world, lowerPos));
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && state.getValue(HALF) == DoubleBlockHalf.UPPER && !player.preventsBlockDrops()) {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = world.getBlockState(lowerPos);
            if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                world.destroyBlock(lowerPos, true, player);
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider(
                (syncId, inventory, player) -> new MechanicalWorktableScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos)),
                TITLE
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        if (!moved && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = pos.above();
            if (world.getBlockState(upperPos).is(this)) {
                world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }
}
