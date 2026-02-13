package com.kltyton.mob_battle.datagen.client.lang;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.client.keybinding.ModKeyBinding;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }


    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_BOOTS), "Ecredcultist Boots");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_CHESTPLATE), "Ecredcultist Chestplate");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_HELMET), "Ecredcultist Helmet");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_LEGGINGS), "Ecredcultist Leggings");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModBlocks.NEST_BLOCK), "Nest");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModBlocks.MUSHROOM_BLOCK), "Mushroom");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.LOBSTER_MAIN_COURSE), "Lobster Main Course");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COOKED_HIGHBIRD_EGG), "Cooked Highbird Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SELF_DESTRUCT), "Self Destruct");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SUPER_SELF_DESTRUCT), "Super Self Destruct");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SUGAR), "Sugar");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.ARMOR_PIERCING), "Armor Piercing");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.VOID_ARMOR_PIERCING), "Void Armor Piercing");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HULKBUSTER), "Hulkbuster");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.HULKBUSTER_SPAWN_EGG), "Hulkbuster Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MISSILE), "Missile");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SILENCE_PHANTOM), "Silence Phantom");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SILENCE_PHANTOM_SPAWN_EGG), "Silence Phantom Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.COAL_SILVERFISH), "Coal Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COAL_SILVERFISH_SPAWN_EGG), "Coal Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LIRUI_SILVERFISH), "Lirui Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("ruili_silverfish")), "Lirui Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.DRILL_SILVERFISH), "Drill Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("drill_silverfish")), "Drill Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.POISONOUS_SILVERFISH), "Poisonous Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_silverfish")), "Poisonous Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LOAD_SILVERFISH), "Load Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("load_silverfish")), "Load Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LONG_WHIP_SILVERFISH), "Long Whip Silverfish");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("long_whip_silverfish")), "Long Whip Silverfish Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.FLOWER_FAIRY), "Flower Fairy");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("flower_fairy")), "Flower Fairy Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SUPER_EVOKER), "Super Evoker");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("super_evoker")), "Super Evoker Spawn Egg");


        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.CYBORG), "Cyborg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("cyborg")), "Cyborg Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.FRENCH_SPHERE_FLOW), "French Sphere Flow");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("french_sphere_flow")), "French Sphere Flow Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.IRON_MAN), "Iron Man");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man")), "Iron Man Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.IRON_MAN_TRUE), "Iron Man True");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man_true")), "Iron Man True Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.POISONOUS_SLASH), "Poisonous Slash");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_slash")), "Poisonous Slash Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.TAI_LIN), "Tai Lin");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("tai_lin")), "Tai Lin Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.WILD_BOAR), "Wild Boar");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_boar")), "Wild Boar Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.WILD_MAN), "Wild Man");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_man")), "Wild Man Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SEX_ENTITY), "Sex Entity");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("sex_entity")), "Sex Entity Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HUMAN_SHIELD), "Human Shield");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("human_shield")), "Human Shield Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HUMAN_HAMMER), "Human Hammer");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("human_hammer")), "Human Hammer Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LITTLE_PERSON_SOLDIER), "Little Person Soldier");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("little_person_soldier")), "Little Person Soldier Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LITTLE_PERSON_SOLDIER_ARCHER), "Little Person Soldier Archer");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("little_person_soldier_archer")), "Little Person Soldier Archer Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.BLOODY_BLADE), "Bloddy Blade");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("bloody_blade")), "Bloddy Blade Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MAGIC_MAN), "Magic Man");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("magic_man")), "Magic Man Spawn Egg");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HEAVEN_CRIPPLED_FEET), "Heaven Crippled Feet");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("heaven_crippled_feet")), "Heaven Crippled Feet Spawn Egg");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.TRUE_INVISIBLE), "True Invisible");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.BLOCK), "Block");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.DISARM), "Disarm");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.INFESTATION), "Infestation");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.HEART_EATER), "Heart Eater");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.PROTEIN), "Protein");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SMALL_BACKPACK), "Small Backpack");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.LARGE_BACKPACK), "Large Backpack");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.FINE_KNIFE), "Fine Knife");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.WARLOCK_BOOK), "Warlock Book");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.GRAND_SUMMON_BOOK), "Grand Summon Book");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.GUARDIAN_SEAL), "Guardian Seal");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_HELMET), "Emerald Diamond Helmet");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_CHESTPLATE), "Emerald Diamond Chestplate");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_LEGGINGS), "Emerald Diamond Leggings");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_BOOTS), "Emerald Diamond Boots");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_SWORD), "Emerald Diamond Sword");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.shieldKey), "Spawn Shield");
    }
}

