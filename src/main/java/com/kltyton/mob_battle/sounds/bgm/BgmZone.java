package com.kltyton.mob_battle.sounds.bgm;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record BgmZone(String name, Box area, Identifier musicId, float volume) {
    public boolean contains(Vec3d pos) {
        return area.contains(pos);
    }

    @Override
    public @NotNull String toString() {
        return String.format("§e分区名称:§r%s\n§b对应BGM:§r%s\n§a音量:§r%.2f\n§d区域:§r(%.1f,%.1f,%.1f)§r至§r(%.1f,%.1f,%.1f)",
                name, musicId, volume,
                area.minX, area.minY, area.minZ,
                area.maxX, area.maxY, area.maxZ);
    }

}
