package com.kltyton.mob_battle.entity.summon.presence;

import net.minecraft.world.level.block.state.BlockState;

/** 方块形实体向客户端提供原版方块材质，不复制方块模型或贴图。 */
public interface BlockAppearance {
    BlockState visualBlockState();
    float visualScale();
    default float visualRoll(float partialTick) { return 0; }
}
