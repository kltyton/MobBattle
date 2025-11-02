package com.kltyton.mob_battle.buff;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.InsectBiteEffect;
import com.kltyton.mob_battle.effect.StunEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModBuffs {
    public static InsectBiteEffect INSECT_BITE;
    public static StunEffect STUN;
    public static RegistryEntry<StatusEffect> STUN_ENTRY;
    public static RegistryEntry<StatusEffect> INSECT_BITE_ENTRY;
    public static void init() {
        INSECT_BITE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "insect_bite"),
                new InsectBiteEffect());
        STUN = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "stun"),
                new StunEffect());

        INSECT_BITE_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "insect_bite")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
        STUN_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of(Mob_battle.MOD_ID, "stun")).orElseThrow(() ->
                new IllegalStateException("未能获得效果的注册表项"));
    }
}

