package com.kltyton.mob_battle.datagen.server.recipe;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

/*    // 此处也可以使用 List.of(...) 方法
    public static final List<ItemConvertible> SMELTABLE_TO_EXAMPLE_INGOT = Util.make(Lists.newArrayList(), list -> {
        list.add(EXAMPLE_ORE);
        list.add(DEEPSLATE_EXAMPLE_ORE);
        list.add(RAW_EXAMPLE);
    });*/
    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                offerSmelting(
                        List.of(ModItems.INCUBATION_EGG), // Inputs
                        RecipeCategory.FOOD, // Category
                        ModItems.COOKED_HIGHBIRD_EGG, // Output
                        0.1f, // Experience
                        300, // Cooking time
                        "food" // group
                );
                createShaped(RecipeCategory.TOOLS, ModItems.FINE_KNIFE, 1)
                        .pattern("a")
                        .pattern("b")
                        .input('a', Items.IRON_INGOT)
                        .input('b', Items.GOLD_INGOT)
                        .group("fine_knife") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.DECORATIONS, ModBlocks.MACHINE_WORKTABLE_BLOCK, 1)
                        .pattern("aba")
                        .pattern("cdc")
                        .pattern("efe")
                        .input('a', ModItems.WIRE)
                        .input('b', ModItems.COMPRESSED_COPPER_INGOT)
                        .input('c', ModItems.COMPRESSED_REDSTONE)
                        .input('d', ModItems.COMPRESSED_IRON_INGOT)
                        .input('e', ModItems.ELECTRONIC_COMPONENTS)
                        .input('f', Items.CRAFTING_TABLE)
                        .group("machine_worktable")
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.TOOLS, ModItems.HEART_STONE, 1)
                        .pattern(" a ")
                        .pattern("aba")
                        .pattern(" a ")
                        .input('a', Items.REDSTONE)
                        .input('b', Items.COBBLESTONE)
                        .group("heart_stone")
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.TOOLS, ModItems.SMALL_BACKPACK, 1)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("xxx")
                        .input('x', Items.RABBIT_HIDE)
                        .group("backpack") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.TRAIN_BULLET, 1)
                        .pattern("a")
                        .pattern("b")
                        .input('a', Items.IRON_INGOT)
                        .input('b', Items.LAPIS_LAZULI)
                        .group("bullet") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.WIRE, 1)
                        .pattern(" c ")
                        .pattern("aba")
                        .pattern(" c ")
                        .input('a', Items.REDSTONE)
                        .input('b', Items.COPPER_INGOT)
                        .input('c', Items.RESIN_CLUMP)
                        .group("wire") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.ELECTRONIC_COMPONENTS, 1)
                        .pattern("dad")
                        .pattern("cbc")
                        .pattern("dad")
                        .input('a', Items.GOLD_BLOCK)
                        .input('b', Items.CRAFTING_TABLE)
                        .input('c', Items.REDSTONE_BLOCK)
                        .input('d', ModItems.WIRE)
                        .group("electronic_components") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(exporter);

                // 机械工作台专用配方示例：这些配方只能在机械工作台中合成。
                // 写法和 createShaped 基本相同，但会生成 type 为 mob_battle:mechanical_crafting_shaped 的 JSON。
                // 如果输出物品已经有普通配方，offerTo 第二个参数请写一个独立 ID，避免和普通配方重名。
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_IRON_BLOCK)
                        .pattern("iii")
                        .pattern("iii")
                        .pattern("iii")
                        .input('i', ModItems.COMPRESSED_IRON_INGOT)
                        .group("mechanical_compressed_blocks")
                        .criterion(hasItem(ModItems.COMPRESSED_IRON_INGOT), conditionsFromItem(ModItems.COMPRESSED_IRON_INGOT))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_iron_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_GOLD_BLOCK)
                        .pattern("ggg")
                        .pattern("ggg")
                        .pattern("ggg")
                        .input('g', ModItems.COMPRESSED_GOLD_INGOT)
                        .group("mechanical_compressed_blocks")
                        .criterion(hasItem(ModItems.COMPRESSED_GOLD_INGOT), conditionsFromItem(ModItems.COMPRESSED_GOLD_INGOT))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_gold_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_DIAMOND_BLOCK)
                        .pattern("ddd")
                        .pattern("ddd")
                        .pattern("ddd")
                        .input('d', ModItems.COMPRESSED_DIAMOND)
                        .group("mechanical_compressed_blocks")
                        .criterion(hasItem(ModItems.COMPRESSED_DIAMOND), conditionsFromItem(ModItems.COMPRESSED_DIAMOND))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_diamond_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_NETHERITE_BLOCK)
                        .pattern("nnn")
                        .pattern("nnn")
                        .pattern("nnn")
                        .input('n', ModItems.COMPRESSED_NETHERITE_INGOT)
                        .group("mechanical_compressed_blocks")
                        .criterion(hasItem(ModItems.COMPRESSED_NETHERITE_INGOT), conditionsFromItem(ModItems.COMPRESSED_NETHERITE_INGOT))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_netherite_block");

                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.DIAMOND_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_DIAMOND
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.IRON_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_IRON_INGOT
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.GOLD_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_GOLD_INGOT
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.COPPER_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_COPPER_INGOT
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.NETHERITE_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_NETHERITE_INGOT
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.REDSTONE_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_REDSTONE
                );
                offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        Items.LAPIS_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_LAPIS_LAZULI
                );
            }
        };
    }

    @Override
    public String getName() {
        return Mob_battle.MOD_ID + " Recipes";
    }
}
