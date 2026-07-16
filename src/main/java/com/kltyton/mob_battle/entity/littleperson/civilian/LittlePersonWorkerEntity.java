package com.kltyton.mob_battle.entity.littleperson.civilian;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;

public class LittlePersonWorkerEntity extends LittlePersonCivilianEntity {
    public LittlePersonWorkerEntity(EntityType<? extends Villager> entityType, Level world) {
        super(entityType, world);
    }
}
