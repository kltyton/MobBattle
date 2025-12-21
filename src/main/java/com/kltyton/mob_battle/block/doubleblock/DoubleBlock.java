package com.kltyton.mob_battle.block.doubleblock;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class DoubleBlock extends BlockWithEntity {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public DoubleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER));
        runSelectionAndCollisionShapes();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        // 必须保证上方还有一格空气
        if (pos.getY() >= world.getTopYInclusive()
                || !world.getBlockState(pos.up()).canReplace(ctx)) {
            return null;
        }
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing()) // 玩家朝向
                .with(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, WorldView world, ScheduledTickView tickView,
            BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, Random random) {

        DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            boolean isLower = (half == DoubleBlockHalf.LOWER);
            if (isLower != (direction == Direction.UP)) {
                return (isLower && direction == Direction.DOWN && !state.canPlaceAt(world, pos))
                        ? Blocks.AIR.getDefaultState()
                        : super.getStateForNeighborUpdate(state, world, tickView, pos,
                        direction, neighborPos, neighborState, random);
            }
            if (neighborState.getBlock() instanceof DoubleBlock
                    && neighborState.get(HALF) != half) {
                return neighborState.with(HALF, half);
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos,
                direction, neighborPos, neighborState, random);
    }
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient
                && (player.shouldSkipBlockDrops() || !player.canHarvest(state))) {
            onBreakInCreative(world, pos, state, player);
        }
        return super.onBreak(world, pos, state, player);
    }
    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }
    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        return state.get(HALF) == DoubleBlockHalf.LOWER
                ? belowState.isSideSolidFullSquare(world, below, Direction.UP)
                : belowState.isOf(this);
    }
    public final Map<Direction, VoxelShape> LOWER_SHAPES = Maps.newEnumMap(Direction.class);
    public final Map<Direction, VoxelShape> UPPER_SHAPES = Maps.newEnumMap(Direction.class);
    public abstract VoxelShape getLowerShape();
    public abstract VoxelShape getUpperShape();
    public void runSelectionAndCollisionShapes() {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            LOWER_SHAPES.put(direction, rotateShape(direction, getLowerShape()));
            UPPER_SHAPES.put(direction, rotateShape(direction, getUpperShape()));
        }
    }

    public VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = {
                shape, VoxelShapes.empty()
        };
        int times = (to.getHorizontalQuarterTurns() - Direction.EAST.getHorizontalQuarterTurns() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[1] = VoxelShapes.empty();
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1],
                    VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
        }
        return buffer[0];
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        return state.get(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPES.get(facing) : UPPER_SHAPES.get(facing);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }
}
