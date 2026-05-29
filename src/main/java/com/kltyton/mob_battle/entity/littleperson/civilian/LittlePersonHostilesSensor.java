package com.kltyton.mob_battle.entity.littleperson.civilian;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;

public class LittlePersonHostilesSensor extends NearestVisibleLivingEntitySensor {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder()
            .put(EntityType.DROWNED, 8.0F)
            .put(EntityType.EVOKER, 12.0F)
            .put(EntityType.HUSK, 8.0F)
            .put(EntityType.ILLUSIONER, 12.0F)
            .put(EntityType.PILLAGER, 15.0F)
            .put(EntityType.RAVAGER, 12.0F)
            .put(EntityType.VEX, 8.0F)
            .put(EntityType.VINDICATOR, 10.0F)
            .put(EntityType.ZOGLIN, 10.0F)
            .put(EntityType.ZOMBIE, 8.0F)
            .put(EntityType.PLAYER, 15.0F)
            .put(EntityType.ZOMBIE_VILLAGER, 8.0F)
            .build();

    @Override
    protected boolean isMatchingEntity(ServerLevel world, LivingEntity entity, LivingEntity target) {
        return this.isHostile(target) && this.isCloseEnoughForDanger(entity, target);
    }

    private boolean isCloseEnoughForDanger(LivingEntity villager, LivingEntity target) {
        float f = SQUARED_DISTANCES_FOR_DANGER.get(target.getType());
        return target.distanceToSqr(villager) <= f * f;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity entity) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
    }
}
