package com.kltyton.mob_battle.block.mushroom;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MushroomBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING =  Properties.HORIZONTAL_FACING;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Direction.class);

    public static VoxelShape makeShape() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.41875, -0.06875, 0.29375, 0.58125, -0.00625, 0.45625),
                VoxelShapes.cuboid(0.13097375, 0.55791875, 0.2599174999999999, 0.88722375, 0.73948125, 1.0724174999999998),
                VoxelShapes.cuboid(0.443739375, 0.44764625, 0.592405, 0.556239375, 0.710414375, 0.7031075),
                VoxelShapes.cuboid(0.45037000000000005, 0.288306875, 0.551120625, 0.5738175, 0.466966875, 0.694880625),
                VoxelShapes.cuboid(0.41911875, -0.094054375, 0.373878125, 0.57537875, 0.397105625, 0.523888125)
        );
    }
    public MushroomBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
        runShapeCache();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec()  {
        return createCodec(MushroomBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MushroomBlockEntity(pos, state);
    }
    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = { shape, VoxelShapes.empty() };
        int times = (to.getHorizontalQuarterTurns() - Direction.NORTH.getHorizontalQuarterTurns() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[1] = VoxelShapes.empty();
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = VoxelShapes.union(buffer[1],
                            VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
        }
        return buffer[0];
    }
    private void runShapeCache() {
        VoxelShape raw = makeShape();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            SHAPES.put(dir, rotateShape(dir, raw));
        }
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
