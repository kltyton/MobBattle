package com.kltyton.mob_battle.entity.littleperson.summon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Xbot002 的 9×9×3 召唤结构匹配器。
 *
 * <p>每个图案单元均为 3×3×3：中层三格与下层中央格为金块，上层中央格为
 * 雕刻南瓜，其余四格必须为空气。匹配器支持沿 X 或 Z 轴放置。</p>
 */
public final class Xbot002SummonPattern {
    private static final int CELL_SIZE = 3;

    private Xbot002SummonPattern() {
    }

    public static Optional<Match> find(Level level, BlockPos clickedPos) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            for (int localHorizontal = 0; localHorizontal < CELL_SIZE; localHorizontal++) {
                for (int localY = 0; localY < CELL_SIZE; localY++) {
                    for (int localDepth = 0; localDepth < CELL_SIZE; localDepth++) {
                        BlockPos centralMin = axis == Direction.Axis.X
                                ? clickedPos.offset(-localHorizontal, -localY, -localDepth)
                                : clickedPos.offset(-localDepth, -localY, -localHorizontal);
                        Optional<Match> match = matchAt(level, centralMin, axis);
                        if (match.isPresent()) {
                            return match;
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<Match> matchAt(Level level, BlockPos centralMin, Direction.Axis axis) {
        List<BlockPos> consumed = new ArrayList<>(5 * CELL_SIZE * CELL_SIZE * CELL_SIZE);
        for (int verticalCell = -1; verticalCell <= 1; verticalCell++) {
            for (int horizontalCell = -1; horizontalCell <= 1; horizontalCell++) {
                Cell cell = expectedCell(horizontalCell, verticalCell);
                BlockPos cellMin = cellMin(centralMin, axis, horizontalCell, verticalCell);
                for (int x = 0; x < CELL_SIZE; x++) {
                    for (int y = 0; y < CELL_SIZE; y++) {
                        for (int z = 0; z < CELL_SIZE; z++) {
                            BlockPos pos = cellMin.offset(x, y, z);
                            boolean valid = switch (cell) {
                                case GOLD -> level.getBlockState(pos).is(Blocks.GOLD_BLOCK);
                                case PUMPKIN -> level.getBlockState(pos).is(Blocks.CARVED_PUMPKIN);
                                case AIR -> level.getBlockState(pos).isAir();
                            };
                            if (!valid) {
                                return Optional.empty();
                            }
                            if (cell != Cell.AIR) {
                                consumed.add(pos.immutable());
                            }
                        }
                    }
                }
            }
        }
        return Optional.of(new Match(centralMin.immutable(), axis, List.copyOf(consumed)));
    }

    private static BlockPos cellMin(BlockPos centralMin, Direction.Axis axis, int horizontalCell, int verticalCell) {
        int horizontalOffset = horizontalCell * CELL_SIZE;
        int verticalOffset = verticalCell * CELL_SIZE;
        return axis == Direction.Axis.X
                ? centralMin.offset(horizontalOffset, verticalOffset, 0)
                : centralMin.offset(0, verticalOffset, horizontalOffset);
    }

    private static Cell expectedCell(int horizontalCell, int verticalCell) {
        if (verticalCell == 0 || verticalCell == -1 && horizontalCell == 0) {
            return Cell.GOLD;
        }
        if (verticalCell == 1 && horizontalCell == 0) {
            return Cell.PUMPKIN;
        }
        return Cell.AIR;
    }

    private enum Cell {
        AIR,
        GOLD,
        PUMPKIN
    }

    public record Match(BlockPos centralMin, Direction.Axis axis, List<BlockPos> consumedBlocks) {
        public double spawnX() {
            return centralMin.getX() + 1.5D;
        }

        public double spawnY() {
            return centralMin.getY() - CELL_SIZE;
        }

        public double spawnZ() {
            return centralMin.getZ() + 1.5D;
        }
    }
}
