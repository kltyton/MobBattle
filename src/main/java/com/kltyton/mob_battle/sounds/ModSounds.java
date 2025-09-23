package com.kltyton.mob_battle.sounds;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "dog_jiao");
    public static SoundEvent DOG_JIAO_SOUND_EVENT = SoundEvent.of(DOG_JIAO_ID);
    public static void init() {
        Registry.register(Registries.SOUND_EVENT, DOG_JIAO_ID, DOG_JIAO_SOUND_EVENT);
    }
}
