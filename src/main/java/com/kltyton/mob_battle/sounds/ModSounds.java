package com.kltyton.mob_battle.sounds;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "dog_jiao");
    public static final Identifier ENGINEERS_SANCTUM = Identifier.of(Mob_battle.MOD_ID, "engineers_sanctum");
    public static final Identifier SHORELINE = Identifier.of(Mob_battle.MOD_ID, "shoreline");
    public static final Identifier GUN_SHOT = Identifier.of(Mob_battle.MOD_ID, "gun_shot");
    public static final Identifier GUN_RELOAD = Identifier.of(Mob_battle.MOD_ID, "gun_reload");
    public static SoundEvent DOG_JIAO_SOUND_EVENT = SoundEvent.of(DOG_JIAO_ID);
    public static SoundEvent ENGINEERS_SANCTUM_SOUND_EVENT = SoundEvent.of(ENGINEERS_SANCTUM);
    public static SoundEvent SHORELINE_SOUND_EVENT = SoundEvent.of(SHORELINE);
    public static SoundEvent GUN_SHOT_SOUND_EVENT = SoundEvent.of(GUN_SHOT);
    public static SoundEvent GUN_RELOAD_SOUND_EVENT = SoundEvent.of(GUN_RELOAD);
    public static RegistryEntry.Reference<SoundEvent> GUN_RELOAD_SOUND_EVENT_REFERENCE = Registry.registerReference(Registries.SOUND_EVENT, GUN_RELOAD, GUN_RELOAD_SOUND_EVENT);;
    public static void init() {
        Registry.register(Registries.SOUND_EVENT, DOG_JIAO_ID, DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, ENGINEERS_SANCTUM, ENGINEERS_SANCTUM_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, SHORELINE, SHORELINE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GUN_SHOT, GUN_SHOT_SOUND_EVENT);
    }
}

