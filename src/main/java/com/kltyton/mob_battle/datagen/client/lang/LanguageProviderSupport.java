package com.kltyton.mob_battle.datagen.client.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 语言数据生成的描述键适配层。
 *
 * <p>集中处理不同注册对象到 translation key 的转换，避免中英文 Provider 重复拼接键。</p>
 */
public final class LanguageProviderSupport {
    private LanguageProviderSupport() {
    }

    /** 返回物品描述键。 */
    public static String getTranslationKey(Item item) {
        return item.getDescriptionId();
    }
    /** 返回方块描述键。 */
    public static String getTranslationKey(Block block) {
        return block.getDescriptionId();
    }
    /** 返回实体实例对应类型的描述键。 */
    public static String getTranslationKey(Entity entity) {
        return entity.getType().getDescriptionId();
    }
    /** 返回实体类型描述键。 */
    public static String getTranslationKey(EntityType<?> entity) {
        return entity.getDescriptionId();
    }
    /** 返回状态效果描述键。 */
    public static String getTranslationKey(MobEffect effect) {
        return effect.getDescriptionId();
    }
    /** 返回按键绑定的翻译键。 */
    public static String getTranslationKey(KeyMapping keyBinding) {
        return keyBinding.getName();
    }
    /** 添加物品翻译。 */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Item item, String translation) {
        builder.add(getTranslationKey(item), translation);
    }
    /** 添加方块翻译。 */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Block block, String translation) {
        builder.add(getTranslationKey(block), translation);
    }
    /** 添加实体实例对应类型的翻译。 */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, Entity entity, String translation) {
        builder.add(getTranslationKey(entity), translation);
    }
    /** 添加实体类型翻译。 */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, EntityType<?> entity, String translation) {
        builder.add(getTranslationKey(entity), translation);
    }
    /** 添加状态效果翻译。 */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, MobEffect effect, String translation) {
        builder.add(getTranslationKey(effect), translation);
    }
}
