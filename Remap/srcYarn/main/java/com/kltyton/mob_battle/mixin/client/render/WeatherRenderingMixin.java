package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.render.WeatherRendering;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherRendering.class)
public class WeatherRenderingMixin {

    /**
     * 拦截 buildPrecipitationPieces 中对 getTopY 的调用
     * 修复由于 min_y 修改导致的高度图失效问题
     */
    @Redirect(
            method = "buildPrecipitationPieces",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTopY(Lnet/minecraft/world/Heightmap$Type;II)I")
    )
    private int fixRainHeightmap(World world, Heightmap.Type type, int x, int z) {
        int originalY = world.getTopY(type, x, z);
        if (originalY < world.getBottomY() + 1) {
            return world.getTopPosition(type, new BlockPos(x, 0, z)).getY();
        }
        return originalY;
    }
}