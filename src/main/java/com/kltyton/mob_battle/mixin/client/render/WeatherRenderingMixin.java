package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherEffectRenderer.class)
public class WeatherRenderingMixin {

    /**
     * 拦截 buildPrecipitationPieces 中对 getTopY 的调用
     * 修复由于 min_y 修改导致的高度图失效问题
     */
    @Redirect(
            method = "collectColumnInstances",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")
    )
    private int fixRainHeightmap(Level world, Heightmap.Types type, int x, int z) {
        int originalY = world.getHeight(type, x, z);
        if (originalY < world.getMinY() + 1) {
            return world.getHeightmapPos(type, new BlockPos(x, 0, z)).getY();
        }
        return originalY;
    }
}