package com.kltyton.mob_battle.entity;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface OwnedSummon {
    @Nullable
    Entity getSummonOwner();
}
