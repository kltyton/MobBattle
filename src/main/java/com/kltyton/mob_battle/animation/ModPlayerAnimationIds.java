package com.kltyton.mob_battle.animation;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;

public final class ModPlayerAnimationIds {
    public static final Identifier KNIFE_KEYFRAME_CONTROLLER =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "knife_keyframe_controller");
    public static final Identifier POISON_KNIFE =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "poison_knife_animation");
    public static final Identifier BLOOD_KNIFE =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "blood_knife_animation");

    private ModPlayerAnimationIds() {
    }
}
