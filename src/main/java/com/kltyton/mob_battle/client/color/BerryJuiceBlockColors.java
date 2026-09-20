package com.kltyton.mob_battle.client.color;

import com.kltyton.mob_battle.block.berryjuice.BerryJuiceBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockTintSources;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;

/**
 * 甜浆果汁炼药锅的客户端液面颜色注册。
 *
 * <p>原版炼药锅液面模型使用 tint index 0；注册固定的 RGB 颜色即可复用原版
 * 三种液面高度模型，并让液体在客户端显示为玫瑰红。该入口只能从客户端初始化
 * 调用，避免服务端 classloading 客户端类。</p>
 */
@Environment(EnvType.CLIENT)
public final class BerryJuiceBlockColors {

    public static final int ROSE_RED = 0xFFD94F70;

    private BerryJuiceBlockColors() {
    }

    public static void init() {
        BlockColorRegistry.register(
                java.util.List.of(BlockTintSources.constant(ROSE_RED)),
                BerryJuiceBlocks.BERRY_JUICE_CAULDRON
        );
    }
}
