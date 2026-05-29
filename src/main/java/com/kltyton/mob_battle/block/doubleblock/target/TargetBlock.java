package com.kltyton.mob_battle.block.doubleblock.target;

import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.block.doubleblock.DoubleBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TargetBlock extends DoubleBlock {
    public TargetBlock(Properties settings) {
        super(settings);
    }
    protected static final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(6, 0, 3, 8, 16, 5),
            Block.box(6, 0, 10, 8, 16, 12)
    );

    // 更精确的上半部分
    protected static final VoxelShape UPPER_SHAPE = Shapes.box(
            0.323, -0.259, -0.107,
            0.548,  0.713,  1.046
    );

    @Override
    public VoxelShape getLowerShape() {
        return LOWER_SHAPE;
    }

    @Override
    public VoxelShape getUpperShape() {
        return UPPER_SHAPE;
    }

    @Override
    protected MapCodec<? extends TargetBlock> codec() {
        return simpleCodec(TargetBlock::new);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide || state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TargetBlockEntity targetBlockEntity) {
            targetBlockEntity.applyGlowingToTracked((ServerLevel) world, player);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new TargetBlockEntity(pos, state);
        }
        return null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.TARGET_ENTITY, TargetBlockEntity::tick);
    }
    @Override
    public VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = {
                shape, Shapes.empty()
        };
        int times = (to.get2DDataValue() - Direction.EAST.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[1] = Shapes.empty();
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
        }
        return buffer[0];
    }
}
