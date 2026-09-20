package com.kltyton.mob_battle.datagen.client.model;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * 双格水平朝向方块的 blockstate/item descriptor 生成。
 *
 * <p>旧手写 blockstate 中 scarecrow 模型正面为 NORTH、target 模型正面为 EAST:
 * y 旋转以正面方向为 0 度,按朝向的 2D 方位差递增 90 度。本类用同一映射规则生成
 * 与手写 JSON 逐项等价的 blockstate,并注册指向方块模型本体的 item descriptor。
 * 方块模型本体是复杂 Blockbench 资源,不在此生成,仍由手写维护。</p>
 */
public final class FacingBlockModels {

    private FacingBlockModels() {
    }

    /** 生成水平朝向方块的 blockstate 与 item descriptor(模型本体保留手写)。 */
    public static void register(BlockModelGenerators collector, Block block, Direction front) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(block);
        PropertyDispatch.C1<VariantMutator, Direction> rotationDispatch =
                PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            rotationDispatch = rotationDispatch.select(facing, yRotationFor(front, facing));
        }
        collector.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(modelLocation))
                        .with(rotationDispatch)
        );
        collector.registerSimpleItemModel(block, modelLocation);
    }

    /** 将 facing 相对 front 的 2D 方位差转换为 90 度整数倍的 y 旋转。 */
    private static VariantMutator yRotationFor(Direction front, Direction facing) {
        return switch ((facing.get2DDataValue() - front.get2DDataValue() + 4) % 4) {
            case 0 -> BlockModelGenerators.NOP;
            case 1 -> BlockModelGenerators.Y_ROT_90;
            case 2 -> BlockModelGenerators.Y_ROT_180;
            default -> BlockModelGenerators.Y_ROT_270;
        };
    }
}
