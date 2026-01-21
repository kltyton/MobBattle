package com.kltyton.mob_battle.block.doubleblock.target;

import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.block.doubleblock.DoubleBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class TargetBlock extends DoubleBlock {
    public TargetBlock(Settings settings) {
        super(settings);
    }
    protected static final VoxelShape LOWER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(6, 0, 3, 8, 16, 5),
            Block.createCuboidShape(6, 0, 10, 8, 16, 12)
    );

    // 更精确的上半部分
    protected static final VoxelShape UPPER_SHAPE = VoxelShapes.cuboid(
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
    protected MapCodec<? extends TargetBlock> getCodec() {
        return createCodec(TargetBlock::new);
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient || state.get(HALF) != DoubleBlockHalf.LOWER) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TargetBlockEntity targetBlockEntity) {
            targetBlockEntity.applyGlowingToTracked((ServerWorld) world, player);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return new TargetBlockEntity(pos, state);
        }
        return null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, ModBlockEntities.TARGET_ENTITY, TargetBlockEntity::tick);
    }
}
