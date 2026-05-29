package com.kltyton.mob_battle.block.doubleblock.machine_worktable;

import com.kltyton.mob_battle.block.doubleblock.DoubleBlock;
import com.kltyton.mob_battle.client.screen.machine_worktable.MechanicalWorktableScreenHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MachineWorktableBlock extends DoubleBlock {
    private static final Text TITLE = Text.translatable("container.crafting");
    private static final VoxelShape LOWER_SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.125, 0.25, 0.5625, 0.25),
            VoxelShapes.cuboid(0.125, 0, 0.75, 0.25, 0.5625, 0.875),
            VoxelShapes.cuboid(0.75, 0, 0.75, 0.875, 0.5625, 0.875),
            VoxelShapes.cuboid(0.75, 0, 0.125, 0.875, 0.5625, 0.25),
            VoxelShapes.cuboid(0, 0.5625, 0.0625, 1, 0.6875, 0.1875),
            VoxelShapes.cuboid(0, 0.5625, 0.8125, 1, 0.6875, 0.9375),
            VoxelShapes.cuboid(0.0625, 0.5625, 0, 0.1875, 0.6875, 1),
            VoxelShapes.cuboid(0.8125, 0.5625, 0, 0.9375, 0.6875, 1),
            VoxelShapes.cuboid(0.25, 0.75, 0.25, 0.75, 0.875, 0.75),
            VoxelShapes.cuboid(0.1875, 0.875, 0.1875, 0.8125, 1, 0.8125),
            VoxelShapes.cuboid(0.4375, 0.625, 0.75, 0.5625, 0.875, 0.8125),
            VoxelShapes.cuboid(0.1875, 0.625, 0.4375, 0.25, 0.875, 0.5625),
            VoxelShapes.cuboid(0.75, 0.625, 0.4375, 0.8125, 0.875, 0.5625),
            VoxelShapes.cuboid(0.4375, 0.625, 0.1875, 0.5625, 0.875, 0.25)
    );
    private static final VoxelShape UPPER_SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.375, 0.8125),
            VoxelShapes.cuboid(0.46875, 0.375, 0.46875, 0.53125, 0.75, 0.53125),
            VoxelShapes.cuboid(0.4375, 0.75, 0.375, 0.5625, 0.8125, 0.4375),
            VoxelShapes.cuboid(0.375, 0.75, 0.4375, 0.625, 0.8125, 0.5625),
            VoxelShapes.cuboid(0.4375, 0.75, 0.5625, 0.5625, 0.8125, 0.625),
            VoxelShapes.cuboid(0.3125, 0.375, 0.5625, 0.4375, 0.4375, 0.6875),
            VoxelShapes.cuboid(0.5625, 0.375, 0.3125, 0.6875, 0.625, 0.4375)
    );
    public MachineWorktableBlock(Settings settings) {
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
    protected MapCodec<? extends MachineWorktableBlock> getCodec() {
        return createCodec(MachineWorktableBlock::new);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
            BlockState lowerState = world.getBlockState(lowerPos);
            if (lowerState.isOf(this)) {
                player.openHandledScreen(lowerState.createScreenHandlerFactory(world, lowerPos));
                player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && state.get(HALF) == DoubleBlockHalf.UPPER && !player.shouldSkipBlockDrops()) {
            BlockPos lowerPos = pos.down();
            BlockState lowerState = world.getBlockState(lowerPos);
            if (lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER) {
                world.breakBlock(lowerPos, true, player);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new MechanicalWorktableScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
                TITLE
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved && state.get(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = pos.up();
            if (world.getBlockState(upperPos).isOf(this)) {
                world.setBlockState(upperPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
            }
        }

        super.onStateReplaced(state, world, pos, moved);
    }
}
