package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ItemTags.SWORDS).add(ModItems.EMERALD_DIAMOND_SWORD, ModItems.IRON_GOLD_SWORD, ModItems.METEORICORE_SWORD);
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
