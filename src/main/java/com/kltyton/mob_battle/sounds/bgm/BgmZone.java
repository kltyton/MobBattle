package com.kltyton.mob_battle.sounds.bgm;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BgmZone(String name, AABB area, ResourceLocation musicId, float volume) {
    public boolean contains(Vec3 pos) {
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
