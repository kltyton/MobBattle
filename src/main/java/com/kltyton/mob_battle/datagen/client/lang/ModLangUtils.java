package com.kltyton.mob_battle.datagen.client.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModLangUtils {
    public static String getTranslationKey(Item item) {
        return item.getDescriptionId();
    }
    public static String getTranslationKey(Block block) {
        return block.getDescriptionId();
    }
    public static String getTranslationKey(Entity entity) {
        return entity.getType().getDescriptionId();
    }
    public static String getTranslationKey(EntityType<?> entity) {
        return entity.getDescriptionId();
    }
    public static String getTranslationKey(MobEffect effect) {
        return effect.getDescriptionId();
    }
    public static String getTranslationKey(KeyMapping keyBinding) {
        return keyBinding.getName();
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
    public static void add(FabricLanguageProvider.TranslationBuilder builder, MobEffect effect, String translation) {
        builder.add(getTranslationKey(effect), translation);
    }
}
