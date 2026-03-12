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
    public static final Identifier METEORITE = Identifier.of(Mob_battle.MOD_ID, "meteorite");
    public static final Identifier B_C_DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "b_c_dog_jiao");
    public static final Identifier B_C_DEBUFF_DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "b_c_debuff_dog_jiao");
    public static final Identifier B_C_BELLOW_DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "b_c_bellow_dog_jiao");
    public static final Identifier B_C_Z_DOG_JIAO_ID = Identifier.of(Mob_battle.MOD_ID, "b_c_z_dog_jiao");
    public static final Identifier ENDER_DRAGON_SOUND_SKILL4_ID = Identifier.of(Mob_battle.MOD_ID, "ender_dragon_skill4");
    public static final Identifier ENDER_DRAGON_SOUND_SKILL5_ID = Identifier.of(Mob_battle.MOD_ID, "ender_dragon_skill5");
    public static final Identifier DRAGON_BGM_HALF_ID = Identifier.of(Mob_battle.MOD_ID, "dragon_bgm_half");
    public static final Identifier DRAGON_BGM_NORMAL_ID = Identifier.of(Mob_battle.MOD_ID, "dragon_bgm_normal");
    public static final Identifier PLAYER_ATTACK_ID = Identifier.of(Mob_battle.MOD_ID, "player_attack");
    public static final Identifier PLAYER_ATTACK_ID_4 = Identifier.of(Mob_battle.MOD_ID, "player_attack_4");
    public static SoundEvent DOG_JIAO_SOUND_EVENT = SoundEvent.of(DOG_JIAO_ID);
    public static SoundEvent ENGINEERS_SANCTUM_SOUND_EVENT = SoundEvent.of(ENGINEERS_SANCTUM);
    public static SoundEvent SHORELINE_SOUND_EVENT = SoundEvent.of(SHORELINE);
    public static SoundEvent GUN_SHOT_SOUND_EVENT = SoundEvent.of(GUN_SHOT);
    public static SoundEvent GUN_RELOAD_SOUND_EVENT = SoundEvent.of(GUN_RELOAD);
    public static SoundEvent METEORITE_SOUND_EVENT = SoundEvent.of(METEORITE);
    public static SoundEvent B_C_DOG_JIAO_SOUND_EVENT = SoundEvent.of(B_C_DOG_JIAO_ID);
    public static SoundEvent B_C_DEBUFF_DOG_JIAO_SOUND_EVENT = SoundEvent.of(B_C_DEBUFF_DOG_JIAO_ID);
    public static SoundEvent B_C_BELLOW_DOG_JIAO_SOUND_EVENT = SoundEvent.of(B_C_BELLOW_DOG_JIAO_ID);
    public static SoundEvent B_C_Z_DOG_JIAO_SOUND_EVENT = SoundEvent.of(B_C_Z_DOG_JIAO_ID);
    public static SoundEvent ENDER_DRAGON_SOUND_SKILL4_SOUND_EVENT = SoundEvent.of(ENDER_DRAGON_SOUND_SKILL4_ID);
    public static SoundEvent ENDER_DRAGON_SOUND_SKILL5_SOUND_EVENT = SoundEvent.of(ENDER_DRAGON_SOUND_SKILL5_ID);
    public static SoundEvent DRAGON_BGM_HALF_SOUND_EVENT = SoundEvent.of(DRAGON_BGM_HALF_ID);
    public static SoundEvent DRAGON_BGM_NORMAL_SOUND_EVENT = SoundEvent.of(DRAGON_BGM_NORMAL_ID);
    public static SoundEvent PLAYER_ATTACK_SOUND_EVENT = SoundEvent.of(PLAYER_ATTACK_ID);
    public static SoundEvent PLAYER_ATTACK_4_SOUND_EVENT = SoundEvent.of(PLAYER_ATTACK_ID_4);


    public static RegistryEntry.Reference<SoundEvent> GUN_RELOAD_SOUND_EVENT_REFERENCE = Registry.registerReference(Registries.SOUND_EVENT, GUN_RELOAD, GUN_RELOAD_SOUND_EVENT);
     public static RegistryEntry.Reference<SoundEvent> METEORITE_SOUND_EVENT_REFERENCE = Registry.registerReference(Registries.SOUND_EVENT, METEORITE, METEORITE_SOUND_EVENT);
    public static void init() {
        Registry.register(Registries.SOUND_EVENT, DOG_JIAO_ID, DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, ENGINEERS_SANCTUM, ENGINEERS_SANCTUM_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, SHORELINE, SHORELINE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GUN_SHOT, GUN_SHOT_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, B_C_DOG_JIAO_ID, B_C_DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, B_C_DEBUFF_DOG_JIAO_ID, B_C_DEBUFF_DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, B_C_BELLOW_DOG_JIAO_ID, B_C_BELLOW_DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, B_C_Z_DOG_JIAO_ID, B_C_Z_DOG_JIAO_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, ENDER_DRAGON_SOUND_SKILL4_ID, ENDER_DRAGON_SOUND_SKILL4_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, ENDER_DRAGON_SOUND_SKILL5_ID, ENDER_DRAGON_SOUND_SKILL5_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, DRAGON_BGM_HALF_ID, DRAGON_BGM_HALF_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, DRAGON_BGM_NORMAL_ID, DRAGON_BGM_NORMAL_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, PLAYER_ATTACK_ID, PLAYER_ATTACK_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, PLAYER_ATTACK_ID_4, PLAYER_ATTACK_4_SOUND_EVENT);
    }
}

