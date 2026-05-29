package com.kltyton.mob_battle.effect;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.beneficial.*;
import com.kltyton.mob_battle.effect.harmful.*;
import com.kltyton.mob_battle.effect.neutral.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

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
    public static IceEffect ICE;
    public static SuperRegenerationStatusEffect SUPER_REGENERATION;
    public static PigSpiritMarkEffect PIG_SPIRIT_MARK;
    public static ExcitementEffect EXCITEMENT;
    public static FatigueEffect FATIGUE;
    public static DiamondMarkEffect DIAMOND_MARK;
    public static NetheriteMarkEffect NETHERITE_MARK;
    public static BlindnessImmunityFactorEffect BLINDNESS_IMMUNITY_FACTOR;
    public static DarknessImmunityFactorEffect DARKNESS_IMMUNITY_FACTOR;
    public static StutterEffect STUTTER;
    public static DecayEffect DECAY;

    public static Holder<MobEffect> STUN_ENTRY;
    public static Holder<MobEffect> INSECT_BITE_ENTRY;
    public static Holder<MobEffect> PRO_INSECT_BITE_ENTRY;
    public static Holder<MobEffect> SELF_DESTRUCT_ENTRY;
    public static Holder<MobEffect> SUPER_SELF_DESTRUCT_ENTRY;
    public static Holder<MobEffect> SUGAR_ENTRY;
    public static Holder<MobEffect> ARMOR_PIERCING_ENTRY;
    public static Holder<MobEffect> VOID_ARMOR_PIERCING_ENTRY;
    public static Holder<MobEffect> TRUE_INVISIBLE_ENTRY;
    public static Holder<MobEffect> PROTEIN_ENTRY;
    public static Holder<MobEffect> BLOCK_ENTRY;
    public static Holder<MobEffect> HEART_EATER_ENTRY;
    public static Holder<MobEffect> INFESTATION_ENTRY;
    public static Holder<MobEffect> DISARM_ENTRY;
    public static Holder<MobEffect> SUPER_REGENERATION_ENTRY;
    public static Holder<MobEffect> PIG_SPIRIT_MARK_ENTRY;
    public static Holder<MobEffect> ICE_ENTRY;
    public static Holder<MobEffect> EXCITEMENT_ENTRY;
    public static Holder<MobEffect> FATIGUE_ENTRY;
    public static Holder<MobEffect> DIAMOND_MARK_ENTRY;
    public static Holder<MobEffect> NETHERITE_MARK_ENTRY;
    public static Holder<MobEffect> BLINDNESS_IMMUNITY_FACTOR_ENTRY;
    public static Holder<MobEffect> DARKNESS_IMMUNITY_FACTOR_ENTRY;
    public static Holder<MobEffect> STUTTER_ENTRY;
    public static Holder<MobEffect> DECAY_ENTRY;

    public static void init() {
        INSECT_BITE = register("insect_bite", new InsectBiteEffect());
        PRO_INSECT_BITE = register("pro_insect_bite", new ProInsectBiteEffect());
        STUN = register("stun", new StunEffect());
        SELF_DESTRUCT = register("self_destruct", new SelfDestructEffect());
        SUPER_SELF_DESTRUCT = register("super_self_destruct", new SuperSelfDestructEffect());
        SUGAR = register("sugar", new SugarStatusEffect());
        ARMOR_PIERCING = register("armor_piercing", new ArmorPiercingEffect());
        VOID_ARMOR_PIERCING = register("void_armor_piercing", new VoidArmorPiercingEffect());
        TRUE_INVISIBLE = register("true_invisible", new TrueInvisibleEffect());
        PROTEIN = register("protein", new ProteinEffect());
        BLOCK = register("block", new BlockStatusEffect());
        HEART_EATER = register("heart_eater", new HeartEaterStatusEffect());
        INFESTATION = register("infestation", new InfestationEffect());
        DISARM = register("disarm", new DisarmEffect());
        SUPER_REGENERATION = register("super_regeneration", new SuperRegenerationStatusEffect());
        PIG_SPIRIT_MARK = register("pig_spirit_mark", new PigSpiritMarkEffect());
        ICE = register("ice", new IceEffect());
        EXCITEMENT = register("excitement", new ExcitementEffect());
        FATIGUE = register("fatigue", new FatigueEffect());
        DIAMOND_MARK = register("diamond_mark", new DiamondMarkEffect());
        NETHERITE_MARK = register("netherite_mark", new NetheriteMarkEffect());
        BLINDNESS_IMMUNITY_FACTOR = register("blindness_immunity_factor", new BlindnessImmunityFactorEffect());
        DARKNESS_IMMUNITY_FACTOR = register("darkness_immunity_factor", new DarknessImmunityFactorEffect());
        STUTTER = register("stutter", new StutterEffect());
        DECAY = register("decay", new DecayEffect());

        INSECT_BITE_ENTRY = getEntry("insect_bite");
        PRO_INSECT_BITE_ENTRY = getEntry("pro_insect_bite");
        STUN_ENTRY = getEntry("stun");
        SELF_DESTRUCT_ENTRY = getEntry("self_destruct");
        SUPER_SELF_DESTRUCT_ENTRY = getEntry("super_self_destruct");
        SUGAR_ENTRY = getEntry("sugar");
        ARMOR_PIERCING_ENTRY = getEntry("armor_piercing");
        VOID_ARMOR_PIERCING_ENTRY = getEntry("void_armor_piercing");
        TRUE_INVISIBLE_ENTRY = getEntry("true_invisible");
        PROTEIN_ENTRY = getEntry("protein");
        BLOCK_ENTRY = getEntry("block");
        HEART_EATER_ENTRY = getEntry("heart_eater");
        INFESTATION_ENTRY = getEntry("infestation");
        DISARM_ENTRY = getEntry("disarm");
        SUPER_REGENERATION_ENTRY = getEntry("super_regeneration");
        PIG_SPIRIT_MARK_ENTRY = getEntry("pig_spirit_mark");
        ICE_ENTRY = getEntry("ice");
        EXCITEMENT_ENTRY = getEntry("excitement");
        FATIGUE_ENTRY = getEntry("fatigue");
        DIAMOND_MARK_ENTRY = getEntry("diamond_mark");
        NETHERITE_MARK_ENTRY = getEntry("netherite_mark");
        BLINDNESS_IMMUNITY_FACTOR_ENTRY = getEntry("blindness_immunity_factor");
        DARKNESS_IMMUNITY_FACTOR_ENTRY = getEntry("darkness_immunity_factor");
        STUTTER_ENTRY = getEntry("stutter");
        DECAY_ENTRY = getEntry("decay");
    }

    private static <T extends MobEffect> T register(String id, T effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, id), effect);
    }

    private static Holder<MobEffect> getEntry(String id) {
        return BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, id))
                .orElseThrow(() -> new IllegalStateException("Missing status effect entry: " + id));
    }
}
