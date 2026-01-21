package com.kltyton.mob_battle.block.doubleblock.scarecrow;

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

public class ScarecrowBlock extends DoubleBlock {
    public ScarecrowBlock(Settings settings) {
        super(settings);
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return new ScarecrowBlockEntity(pos, state);
        }
        return null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, ModBlockEntities.SCARECROW_ENTITY, ScarecrowBlockEntity::tick);
    }
    @Override
    protected MapCodec<? extends ScarecrowBlock> getCodec() {
        return createCodec(ScarecrowBlock::new);
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient || state.get(HALF) != DoubleBlockHalf.LOWER) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ScarecrowBlockEntity scarecrowBE) {
            scarecrowBE.applyGlowingToTracked((ServerWorld) world, player);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    private static final VoxelShape LOWER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(6, 1, 6, 10, 13, 10),
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(6, 13, 4, 10, 16, 12),
            Block.createCuboidShape(6, 11, 4, 7, 13, 5),
            Block.createCuboidShape(6, 11, 11, 7, 13, 12),
            Block.createCuboidShape(9, 11, 11, 10, 13, 12),
            Block.createCuboidShape(9, 11, 4, 10, 13, 5),
            Block.createCuboidShape(6, 12, 5, 7, 13, 6),
            Block.createCuboidShape(6, 12, 10, 7, 13, 11),
            Block.createCuboidShape(9, 12, 10, 10, 13, 11),
            Block.createCuboidShape(9, 12, 5, 10, 13, 6)
    );
    private static final VoxelShape UPPER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(6, 3, -2, 10, 7, 18),  // Y 相对偏移：原 19-23 → 相对上半格 3-7 (19-16=3)
            Block.createCuboidShape(4, 7, 3.5, 12, 15, 12.5),  // 原 23-31 → 相对 7-15
            Block.createCuboidShape(6, 0, 4, 10, 3, 12),
            Block.createCuboidShape(6, 0, 5, 10, 1, 6),  // 小突起上半
            Block.createCuboidShape(9, 0, 5, 10, 1, 6)
    );
}
