package com.kltyton.mob_battle.buff;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.InsectBiteEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBuffs {
    public static InsectBiteEffect INSECT_BITE;
    public static void init() {
        //注册BUFF
        INSECT_BITE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(Mob_battle.MOD_ID, "insect_bite"),
                new InsectBiteEffect());

    }
}
