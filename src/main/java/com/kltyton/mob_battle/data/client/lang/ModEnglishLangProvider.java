package com.kltyton.mob_battle.data.client.lang;

import com.kltyton.mob_battle.block.ModBlocks;
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
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_BOOTS), "Ecredcultist Boots");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_CHESTPLATE), "Ecredcultist Chestplate");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_HELMET), "Ecredcultist Helmet");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_LEGGINGS), "Ecredcultist Leggings");
        translationBuilder.add(ModLangUtils.getBlockTranslationKey(ModBlocks.NEST_BLOCK), "Nest");
        translationBuilder.add(ModLangUtils.getBlockTranslationKey(ModBlocks.MUSHROOM_BLOCK), "Mushroom");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.LOBSTER_MAIN_COURSE), "Lobster Main Course");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.COOKED_HIGHBIRD_EGG), "Cooked Highbird Egg");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SELF_DESTRUCT), "Self Destruct");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SUPER_SELF_DESTRUCT), "Super Self Destruct");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SUGAR), "Sugar");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.ARMOR_PIERCING), "Armor Piercing");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.VOID_ARMOR_PIERCING), "Void Armor Piercing");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.HULKBUSTER), "Hulkbuster");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.HULKBUSTER_SPAWN_EGG), "Hulkbuster Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.MISSILE), "Missile");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.SILENCE_PHANTOM), "Silence Phantom");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SILENCE_PHANTOM_SPAWN_EGG), "Silence Phantom Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.COAL_SILVERFISH), "Coal Silverfish");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.COAL_SILVERFISH_SPAWN_EGG), "Coal Silverfish Spawn Egg");

        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.CYBORG), "Cyborg");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("cyborg")), "Cyborg Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.FRENCH_SPHERE_FLOW), "French Sphere Flow");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("french_sphere_flow")), "French Sphere Flow Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.IRON_MAN), "Iron Man");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man")), "Iron Man Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.IRON_MAN_TRUE), "Iron Man True");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man_true")), "Iron Man True Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.POISONOUS_SLASH), "Poisonous Slash");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_slash")), "Poisonous Slash Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.TAI_LIN), "Tai Lin");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("tai_lin")), "Tai Lin Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.WILD_BOAR), "Wild Boar");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_boar")), "Wild Boar Spawn Egg");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.WILD_MAN), "Wild Man");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_man")), "Wild Man Spawn Egg");
    }
}

