package com.kltyton.mob_battle.block.doubleblock;

import com.google.common.collect.Maps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class DoubleBlock extends BaseEntityBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction> FACING =  BlockStateProperties.HORIZONTAL_FACING;

    public DoubleBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
        runSelectionAndCollisionShapes();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        // 必须保证上方还有一格空气
        if (pos.getY() >= world.getMaxY()
                || !world.getBlockState(pos.above()).canBeReplaced(ctx)) {
            return null;
        }
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection()) // 玩家朝向
                .setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    protected BlockState updateShape(
            BlockState state, LevelReader world, ScheduledTickAccess tickView,
            BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, RandomSource random) {

        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            boolean isLower = (half == DoubleBlockHalf.LOWER);
            if (isLower != (direction == Direction.UP)) {
                return (isLower && direction == Direction.DOWN && !state.canSurvive(world, pos))
                        ? Blocks.AIR.defaultBlockState()
                        : super.updateShape(state, world, tickView, pos,
                        direction, neighborPos, neighborState, random);
            }
            if (neighborState.getBlock() instanceof DoubleBlock
                    && neighborState.getValue(HALF) != half) {
                return neighborState.setValue(HALF, half);
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, world, tickView, pos,
                direction, neighborPos, neighborState, random);
    }
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide
                && (player.preventsBlockDrops() || !player.hasCorrectToolForDrops(state))) {
            onBreakInCreative(world, pos, state, player);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }
    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }
    }
    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = world.getBlockState(below);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? belowState.isFaceSturdy(world, below, Direction.UP)
                : belowState.is(this);
    }
    public final Map<Direction, VoxelShape> LOWER_SHAPES = Maps.newEnumMap(Direction.class);
    public final Map<Direction, VoxelShape> UPPER_SHAPES = Maps.newEnumMap(Direction.class);
    public abstract VoxelShape getLowerShape();
    public abstract VoxelShape getUpperShape();
    public void runSelectionAndCollisionShapes() {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            LOWER_SHAPES.put(direction, rotateShape(direction, getLowerShape()));
            UPPER_SHAPES.put(direction, rotateShape(direction, getUpperShape()));
        }
    }

    public VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = {
                shape, Shapes.empty()
        };
        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[1] = Shapes.empty();
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
        }
        return buffer[0];
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPES.get(facing) : UPPER_SHAPES.get(facing);
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
