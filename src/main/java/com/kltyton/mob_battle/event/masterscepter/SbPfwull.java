package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class SbPfwull {
    public static void runCommand(ServerPlayer player) {
        ServerLevel world = player.level();
        // 播放使用音效
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClientSide) {
            FireWallEntity wall = new FireWallEntity(ModEntities.FIRE_WALL, world, player);
            wall.setDamage(wall.getDamage() + 40f);
            wall.setLength(10.0D);
            wall.setHeight(10.0D);
            wall.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            world.addFreshEntity(wall);
        }
    }
}
