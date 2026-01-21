package com.kltyton.mob_battle.effect;

import com.kltyton.mob_battle.Mob_battle;
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
    public static RegistryEntry<StatusEffect> STUN_ENTRY;
    public static RegistryEntry<StatusEffect> INSECT_BITE_ENTRY;
    public static RegistryEntry<StatusEffect> PRO_INSECT_BITE_ENTRY;
    public static RegistryEntry<StatusEffect> SELF_DESTRUCT_ENTRY;
    public static RegistryEntry<StatusEffect> SUPER_SELF_DESTRUCT_ENTRY;
    public static RegistryEntry<StatusEffect> SUGAR_ENTRY;
    public static RegistryEntry<StatusEffect> ARMOR_PIERCING_ENTRY;
    public static RegistryEntry<StatusEffect> VOID_ARMOR_PIERCING_ENTRY;

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
    }
}

