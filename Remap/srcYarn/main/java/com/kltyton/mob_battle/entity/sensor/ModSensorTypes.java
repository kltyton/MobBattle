package com.kltyton.mob_battle.entity.sensor;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonHostilesSensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSensorTypes {
    public static SensorType<LittlePersonHostilesSensor> LITTLE_PERSON_HOSTILES;
    public static void init() {
        LITTLE_PERSON_HOSTILES = Registry.register(
                Registries.SENSOR_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "little_person_hostiles"),
                new SensorType<>(LittlePersonHostilesSensor::new)
        );
    }
}
