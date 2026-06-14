package com.kltyton.mob_battle.entity.snowgolem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.level.Level;

public class NewSnowGolemEntity extends SnowGolem {
    public NewSnowGolemEntity(EntityType<? extends SnowGolem> entityType, Level level) {
        super(entityType, level);
        super.setPumpkin(false);
    }

    @Override
    public void setPumpkin(boolean pumpkin) {
        super.setPumpkin(false);
    }
}
