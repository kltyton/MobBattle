package com.kltyton.mob_battle.block.mushroom;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING =  BlockStateProperties.HORIZONTAL_FACING;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Direction.class);

    public static VoxelShape makeShape() {
        return Shapes.or(
                Shapes.box(0.41875, -0.06875, 0.29375, 0.58125, -0.00625, 0.45625),
                Shapes.box(0.13097375, 0.55791875, 0.2599174999999999, 0.88722375, 0.73948125, 1.0724174999999998),
                Shapes.box(0.443739375, 0.44764625, 0.592405, 0.556239375, 0.710414375, 0.7031075),
                Shapes.box(0.45037000000000005, 0.288306875, 0.551120625, 0.5738175, 0.466966875, 0.694880625),
                Shapes.box(0.41911875, -0.094054375, 0.373878125, 0.57537875, 0.397105625, 0.523888125)
        );
    }
    public MushroomBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        runShapeCache();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }


    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = { shape, Shapes.empty() };
        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[1] = Shapes.empty();
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1],
                            Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
        }
        return buffer[0];
    }
    private void runShapeCache() {
        VoxelShape raw = makeShape();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            SHAPES.put(dir, rotateShape(dir, raw));
        }
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()  {
        return simpleCodec(MushroomBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MushroomBlockEntity(pos, state);
    }
}
