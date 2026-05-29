package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class SbPfwull {
    public static void runCommand(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        // 播放使用音效
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClient) {
            FireWallEntity wall = new FireWallEntity(ModEntities.FIRE_WALL, world, player);
            wall.setDamage(wall.getDamage() + 40f);
            wall.setLength(10.0D);
            wall.setHeight(10.0D);
            wall.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            world.spawnEntity(wall);
        }
    }
}
