package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    private static final TagKey<Item> ENCHANTABLE_HEAD_ARMOR = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/head_armor"));
    private static final TagKey<Item> ENCHANTABLE_CHEST_ARMOR = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/chest_armor"));
    private static final TagKey<Item> ENCHANTABLE_LEG_ARMOR = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/leg_armor"));
    private static final TagKey<Item> ENCHANTABLE_FOOT_ARMOR = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/foot_armor"));

    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(ItemTags.SWORDS).add(
                ModItems.EMERALD_DIAMOND_SWORD,
                ModItems.IRON_GOLD_SWORD,
                ModItems.METEORICORE_SWORD,
                ModItems.ZIJIN_SWORD,
                ModItems.COMPRESSED_IRON_SWORD,
                ModItems.COMPRESSED_GOLD_SWORD,
                ModItems.COMPRESSED_DIAMOND_SWORD,
                ModItems.COMPRESSED_NETHERITE_SWORD
        );
        valueLookupBuilder(ItemTags.SWORD_ENCHANTABLE).add(
                ModItems.COMPRESSED_IRON_SWORD,
                ModItems.COMPRESSED_GOLD_SWORD,
                ModItems.COMPRESSED_DIAMOND_SWORD,
                ModItems.COMPRESSED_NETHERITE_SWORD
        );
        valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR).add(
                ModItems.COMPRESSED_IRON_HELMET,
                ModItems.COMPRESSED_IRON_CHESTPLATE,
                ModItems.COMPRESSED_IRON_LEGGINGS,
                ModItems.COMPRESSED_IRON_BOOTS,
                ModItems.COMPRESSED_GOLD_HELMET,
                ModItems.COMPRESSED_GOLD_CHESTPLATE,
                ModItems.COMPRESSED_GOLD_LEGGINGS,
                ModItems.COMPRESSED_GOLD_BOOTS,
                ModItems.COMPRESSED_DIAMOND_HELMET,
                ModItems.COMPRESSED_DIAMOND_CHESTPLATE,
                ModItems.COMPRESSED_DIAMOND_LEGGINGS,
                ModItems.COMPRESSED_DIAMOND_BOOTS,
                ModItems.COMPRESSED_NETHERITE_HELMET,
                ModItems.COMPRESSED_NETHERITE_CHESTPLATE,
                ModItems.COMPRESSED_NETHERITE_LEGGINGS,
                ModItems.COMPRESSED_NETHERITE_BOOTS
        );
        valueLookupBuilder(ENCHANTABLE_HEAD_ARMOR).add(
                ModItems.COMPRESSED_IRON_HELMET,
                ModItems.COMPRESSED_GOLD_HELMET,
                ModItems.COMPRESSED_DIAMOND_HELMET,
                ModItems.COMPRESSED_NETHERITE_HELMET
        );
        valueLookupBuilder(ENCHANTABLE_CHEST_ARMOR).add(
                ModItems.COMPRESSED_IRON_CHESTPLATE,
                ModItems.COMPRESSED_GOLD_CHESTPLATE,
                ModItems.COMPRESSED_DIAMOND_CHESTPLATE,
                ModItems.COMPRESSED_NETHERITE_CHESTPLATE
        );
        valueLookupBuilder(ENCHANTABLE_LEG_ARMOR).add(
                ModItems.COMPRESSED_IRON_LEGGINGS,
                ModItems.COMPRESSED_GOLD_LEGGINGS,
                ModItems.COMPRESSED_DIAMOND_LEGGINGS,
                ModItems.COMPRESSED_NETHERITE_LEGGINGS
        );
        valueLookupBuilder(ENCHANTABLE_FOOT_ARMOR).add(
                ModItems.COMPRESSED_IRON_BOOTS,
                ModItems.COMPRESSED_GOLD_BOOTS,
                ModItems.COMPRESSED_DIAMOND_BOOTS,
                ModItems.COMPRESSED_NETHERITE_BOOTS
        );
        valueLookupBuilder(ItemTags.PIGLIN_LOVED).add(
                ModItems.COMPRESSED_GOLD_HELMET,
                ModItems.COMPRESSED_GOLD_CHESTPLATE,
                ModItems.COMPRESSED_GOLD_LEGGINGS,
                ModItems.COMPRESSED_GOLD_BOOTS
        );
        valueLookupBuilder(ItemTags.PIGLIN_SAFE_ARMOR).add(
                ModItems.COMPRESSED_GOLD_HELMET,
                ModItems.COMPRESSED_GOLD_CHESTPLATE,
                ModItems.COMPRESSED_GOLD_LEGGINGS,
                ModItems.COMPRESSED_GOLD_BOOTS
        );
        /*
        valueLookupBuilder(ItemTags.WOODEN_SLABS)  // 创建一个标签构建器，用于处理WOODEN_SLABS标签
            .add(Items.SLIME_BALL)                 // 将SLIME_BALL物品添加到当前标签中
            .add(Items.ROTTEN_FLESH)               // 将ROTTEN_FLESH物品添加到当前标签中
            .add(Items.OAK_PLANKS)                 // 将OAK_PLANKS物品添加到当前标签中
            .addOptionalTag(ItemTags.DIRT)         // 可选地添加DIRT标签中的所有物品（如果存在）
            .forceAddTag(ItemTags.BANNERS)         // 强制添加BANNERS标签中的所有物品
            .setReplace(true);                     // 设置替换模式为true，表示完全替换原有标签内容
        */
    }
}
