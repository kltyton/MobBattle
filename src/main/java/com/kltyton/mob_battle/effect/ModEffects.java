package com.kltyton.mob_battle.effect;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.beneficial.*;
import com.kltyton.mob_battle.effect.harmful.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static InsectBiteEffect INSECT_BITE;
    public static ProInsectBiteEffect PRO_INSECT_BITE;
    public static StunEffect STUN;
    public static SelfDestructEffect SELF_DESTRUCT;
    public static SuperSelfDestructEffect SUPER_SELF_DESTRUCT;
    public static SugarStatusEffect SUGAR;
    public static ArmorPiercingEffect ARMOR_PIERCING;
    public static VoidArmorPiercingEffect VOID_ARMOR_PIERCING;
    public static TrueInvisibleEffect TRUE_INVISIBLE;
    public static ProteinEffect PROTEIN;
    public static BlockStatusEffect BLOCK;
    public static HeartEaterStatusEffect HEART_EATER;
    public static InfestationEffect INFESTATION;
    public static DisarmEffect DISARM;

    public static RegistryEntry<StatusEffect> STUN_ENTRY;
    public static RegistryEntry<StatusEffect> INSECT_BITE_ENTRY;
    public static RegistryEntry<StatusEffect> PRO_INSECT_BITE_ENTRY;
    public static RegistryEntry<StatusEffect> SELF_DESTRUCT_ENTRY;
    public static RegistryEntry<StatusEffect> SUPER_SELF_DESTRUCT_ENTRY;
    public static RegistryEntry<StatusEffect> SUGAR_ENTRY;
    public static RegistryEntry<StatusEffect> ARMOR_PIERCING_ENTRY;
    public static RegistryEntry<StatusEffect> VOID_ARMOR_PIERCING_ENTRY;
    public static RegistryEntry<StatusEffect> TRUE_INVISIBLE_ENTRY;
    public static RegistryEntry<StatusEffect> PROTEIN_ENTRY;
    public static RegistryEntry<StatusEffect> BLOCK_ENTRY;
    public static RegistryEntry<StatusEffect> HEART_EATER_ENTRY;
    public static RegistryEntry<StatusEffect> INFESTATION_ENTRY;
    public static RegistryEntry<StatusEffect> DISARM_ENTRY;

    public static void init() {
        INSECT_BITE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "insect_bite"),
                new InsectBiteEffect());
        PRO_INSECT_BITE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "pro_insect_bite"),
                new ProInsectBiteEffect());
        STUN = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "stun"),
                new StunEffect());
        SELF_DESTRUCT = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "self_destruct"),
                new SelfDestructEffect());
        SUPER_SELF_DESTRUCT = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "super_self_destruct"),
                new SuperSelfDestructEffect());
        SUGAR = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "sugar"),
                new SugarStatusEffect());
        ARMOR_PIERCING = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "armor_piercing"),
                new ArmorPiercingEffect());
        VOID_ARMOR_PIERCING = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "void_armor_piercing"),
                new VoidArmorPiercingEffect());
        TRUE_INVISIBLE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "true_invisible"),
                new TrueInvisibleEffect());
        PROTEIN = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "protein"),
                new ProteinEffect());
        BLOCK = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "block"),
                new BlockStatusEffect());
        HEART_EATER = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "heart_eater"),
                new HeartEaterStatusEffect());
        INFESTATION = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "infestation"),
                new InfestationEffect());
        DISARM = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "disarm"),
                new DisarmEffect());


        INSECT_BITE_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "insect_bite")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        PRO_INSECT_BITE_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "pro_insect_bite")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        STUN_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "stun")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        SELF_DESTRUCT_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "self_destruct")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        SUPER_SELF_DESTRUCT_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "super_self_destruct")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        SUGAR_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "sugar")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        ARMOR_PIERCING_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "armor_piercing")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        VOID_ARMOR_PIERCING_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "void_armor_piercing")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        TRUE_INVISIBLE_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "true_invisible")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        PROTEIN_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "protein")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        BLOCK_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "block")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        HEART_EATER_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "heart_eater")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        INFESTATION_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "infestation")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        DISARM_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "disarm")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
    }
}

