package com.kltyton.mob_battle.block.doubleblock.scarecrow;

import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.block.doubleblock.DoubleBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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

public class ScarecrowBlock extends DoubleBlock {
    public ScarecrowBlock(Properties settings) {
        super(settings);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new ScarecrowBlockEntity(pos, state);
        }
        return null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.SCARECROW_ENTITY, ScarecrowBlockEntity::tick);
    }
    @Override
    protected MapCodec<? extends ScarecrowBlock> codec() {
        return simpleCodec(ScarecrowBlock::new);
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
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide || state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ScarecrowBlockEntity scarecrowBE) {
            scarecrowBE.applyGlowingToTracked((ServerLevel) world, player);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        // 只在下半部分被替换/移除时处理，避免重复执行
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ScarecrowBlockEntity scarecrowBlockEntity) {
                scarecrowBlockEntity.killTrackedGolems(world);
            }
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(6, 1, 6, 10, 13, 10),
            Block.box(1, 0, 1, 15, 1, 15),
            Block.box(6, 13, 4, 10, 16, 12),
            Block.box(6, 11, 4, 7, 13, 5),
            Block.box(6, 11, 11, 7, 13, 12),
            Block.box(9, 11, 11, 10, 13, 12),
            Block.box(9, 11, 4, 10, 13, 5),
            Block.box(6, 12, 5, 7, 13, 6),
            Block.box(6, 12, 10, 7, 13, 11),
            Block.box(9, 12, 10, 10, 13, 11),
            Block.box(9, 12, 5, 10, 13, 6)
    );
    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            Block.box(6, 3, -2, 10, 7, 18),  // Y 相对偏移：原 19-23 → 相对上半格 3-7 (19-16=3)
            Block.box(4, 7, 3.5, 12, 15, 12.5),  // 原 23-31 → 相对 7-15
            Block.box(6, 0, 4, 10, 3, 12),
            Block.box(6, 0, 5, 10, 1, 6),  // 小突起上半
            Block.box(9, 0, 5, 10, 1, 6)
    );
}
