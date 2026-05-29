package com.kltyton.mob_battle.entity.sensor;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonHostilesSensor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class ModSensorTypes {
    public static SensorType<LittlePersonHostilesSensor> LITTLE_PERSON_HOSTILES;
    public static void init() {
        LITTLE_PERSON_HOSTILES = Registry.register(
                BuiltInRegistries.SENSOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_hostiles"),
                new SensorType<>(LittlePersonHostilesSensor::new)
        );
    }
}
