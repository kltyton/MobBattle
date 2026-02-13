package com.kltyton.mob_battle.datagen.client.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;

public class ModLangUtils {
    public static String getTranslationKey(Item item) {
        return item.getTranslationKey();
    }
    public static String getTranslationKey(Block block) {
        return block.getTranslationKey();
    }
    public static String getTranslationKey(Entity entity) {
        return entity.getType().getTranslationKey();
    }
    public static String getTranslationKey(EntityType<?> entity) {
        return entity.getTranslationKey();
    }
    public static String getTranslationKey(StatusEffect effect) {
        return effect.getTranslationKey();
    }
    public static String getTranslationKey(KeyBinding keyBinding) {
        return keyBinding.getTranslationKey();
    }
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Item item, String translation) {
        builder.add(getTranslationKey(item), translation);
    }
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Block block, String translation) {
        builder.add(getTranslationKey(block), translation);
    }
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Entity entity, String translation) {
        builder.add(getTranslationKey(entity), translation);
    }
    public static void add(FabricLanguageProvider.TranslationBuilder builder, EntityType<?> entity, String translation) {
        builder.add(getTranslationKey(entity), translation);
    }
    public static void add(FabricLanguageProvider.TranslationBuilder builder, StatusEffect effect, String translation) {
        builder.add(getTranslationKey(effect), translation);
    }
}
