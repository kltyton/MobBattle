package com.kltyton.mob_battle.sounds;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final ResourceLocation DOG_JIAO_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "dog_jiao");
    public static final ResourceLocation ENGINEERS_SANCTUM = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "engineers_sanctum");
    public static final ResourceLocation SHORELINE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "shoreline");
    public static final ResourceLocation GUN_SHOT = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "gun_shot");
    public static final ResourceLocation GUN_RELOAD = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "gun_reload");
    public static final ResourceLocation METEORITE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteorite");
    public static final ResourceLocation B_C_DOG_JIAO_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "b_c_dog_jiao");
    public static final ResourceLocation B_C_DEBUFF_DOG_JIAO_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "b_c_debuff_dog_jiao");
    public static final ResourceLocation B_C_BELLOW_DOG_JIAO_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "b_c_bellow_dog_jiao");
    public static final ResourceLocation B_C_Z_DOG_JIAO_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "b_c_z_dog_jiao");
    public static final ResourceLocation ENDER_DRAGON_SOUND_SKILL4_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ender_dragon_skill4");
    public static final ResourceLocation ENDER_DRAGON_SOUND_SKILL5_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ender_dragon_skill5");
    public static final ResourceLocation DRAGON_BGM_HALF_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "dragon_bgm_half");
    public static final ResourceLocation DRAGON_BGM_NORMAL_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "dragon_bgm_normal");
    public static final ResourceLocation PLAYER_ATTACK_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_attack");
    public static final ResourceLocation PLAYER_ATTACK_ID_4 = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_attack_4");
    public static SoundEvent DOG_JIAO_SOUND_EVENT = SoundEvent.createVariableRangeEvent(DOG_JIAO_ID);
    public static SoundEvent ENGINEERS_SANCTUM_SOUND_EVENT = SoundEvent.createVariableRangeEvent(ENGINEERS_SANCTUM);
    public static SoundEvent SHORELINE_SOUND_EVENT = SoundEvent.createVariableRangeEvent(SHORELINE);
    public static SoundEvent GUN_SHOT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(GUN_SHOT);
    public static SoundEvent GUN_RELOAD_SOUND_EVENT = SoundEvent.createVariableRangeEvent(GUN_RELOAD);
    public static SoundEvent METEORITE_SOUND_EVENT = SoundEvent.createVariableRangeEvent(METEORITE);
    public static SoundEvent B_C_DOG_JIAO_SOUND_EVENT = SoundEvent.createVariableRangeEvent(B_C_DOG_JIAO_ID);
    public static SoundEvent B_C_DEBUFF_DOG_JIAO_SOUND_EVENT = SoundEvent.createVariableRangeEvent(B_C_DEBUFF_DOG_JIAO_ID);
    public static SoundEvent B_C_BELLOW_DOG_JIAO_SOUND_EVENT = SoundEvent.createVariableRangeEvent(B_C_BELLOW_DOG_JIAO_ID);
    public static SoundEvent B_C_Z_DOG_JIAO_SOUND_EVENT = SoundEvent.createVariableRangeEvent(B_C_Z_DOG_JIAO_ID);
    public static SoundEvent ENDER_DRAGON_SOUND_SKILL4_SOUND_EVENT = SoundEvent.createVariableRangeEvent(ENDER_DRAGON_SOUND_SKILL4_ID);
    public static SoundEvent ENDER_DRAGON_SOUND_SKILL5_SOUND_EVENT = SoundEvent.createVariableRangeEvent(ENDER_DRAGON_SOUND_SKILL5_ID);
    public static SoundEvent DRAGON_BGM_HALF_SOUND_EVENT = SoundEvent.createVariableRangeEvent(DRAGON_BGM_HALF_ID);
    public static SoundEvent DRAGON_BGM_NORMAL_SOUND_EVENT = SoundEvent.createVariableRangeEvent(DRAGON_BGM_NORMAL_ID);
    public static SoundEvent PLAYER_ATTACK_SOUND_EVENT = SoundEvent.createVariableRangeEvent(PLAYER_ATTACK_ID);
    public static SoundEvent PLAYER_ATTACK_4_SOUND_EVENT = SoundEvent.createVariableRangeEvent(PLAYER_ATTACK_ID_4);


    public static Holder.Reference<SoundEvent> GUN_RELOAD_SOUND_EVENT_REFERENCE = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, GUN_RELOAD, GUN_RELOAD_SOUND_EVENT);
     public static Holder.Reference<SoundEvent> METEORITE_SOUND_EVENT_REFERENCE = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, METEORITE, METEORITE_SOUND_EVENT);
    public static void init() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, DOG_JIAO_ID, DOG_JIAO_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENGINEERS_SANCTUM, ENGINEERS_SANCTUM_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SHORELINE, SHORELINE_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, GUN_SHOT, GUN_SHOT_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, B_C_DOG_JIAO_ID, B_C_DOG_JIAO_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, B_C_DEBUFF_DOG_JIAO_ID, B_C_DEBUFF_DOG_JIAO_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, B_C_BELLOW_DOG_JIAO_ID, B_C_BELLOW_DOG_JIAO_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, B_C_Z_DOG_JIAO_ID, B_C_Z_DOG_JIAO_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENDER_DRAGON_SOUND_SKILL4_ID, ENDER_DRAGON_SOUND_SKILL4_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENDER_DRAGON_SOUND_SKILL5_ID, ENDER_DRAGON_SOUND_SKILL5_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, DRAGON_BGM_HALF_ID, DRAGON_BGM_HALF_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, DRAGON_BGM_NORMAL_ID, DRAGON_BGM_NORMAL_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_ATTACK_ID, PLAYER_ATTACK_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_ATTACK_ID_4, PLAYER_ATTACK_4_SOUND_EVENT);
    }
}

