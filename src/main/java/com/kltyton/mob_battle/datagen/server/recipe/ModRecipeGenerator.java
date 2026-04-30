package com.kltyton.mob_battle.datagen.server.recipe;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
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

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_IRON_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_IRON_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .criterion(hasItem(ModBlocks.COMPRESSED_IRON_BLOCK), conditionsFromItem(ModBlocks.COMPRESSED_IRON_BLOCK))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_iron_ingot_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_GOLD_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_GOLD_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .criterion(hasItem(ModBlocks.COMPRESSED_GOLD_BLOCK), conditionsFromItem(ModBlocks.COMPRESSED_GOLD_BLOCK))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_gold_ingot_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_DIAMOND, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_DIAMOND_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .criterion(hasItem(ModBlocks.COMPRESSED_DIAMOND_BLOCK), conditionsFromItem(ModBlocks.COMPRESSED_DIAMOND_BLOCK))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_diamond_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_NETHERITE_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_NETHERITE_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .criterion(hasItem(ModBlocks.COMPRESSED_NETHERITE_BLOCK), conditionsFromItem(ModBlocks.COMPRESSED_NETHERITE_BLOCK))
                        .offerTo(exporter, "mob_battle:mechanical/compressed_netherite_ingot_from_block");

                offerMechanicalArmorRecipes(itemLookup, exporter, ModItems.COMPRESSED_IRON_INGOT, "compressed_iron", ModItems.COMPRESSED_IRON_HELMET, ModItems.COMPRESSED_IRON_CHESTPLATE, ModItems.COMPRESSED_IRON_LEGGINGS, ModItems.COMPRESSED_IRON_BOOTS);
                offerMechanicalArmorRecipes(itemLookup, exporter, ModItems.COMPRESSED_GOLD_INGOT, "compressed_gold", ModItems.COMPRESSED_GOLD_HELMET, ModItems.COMPRESSED_GOLD_CHESTPLATE, ModItems.COMPRESSED_GOLD_LEGGINGS, ModItems.COMPRESSED_GOLD_BOOTS);
                offerMechanicalArmorRecipes(itemLookup, exporter, ModItems.COMPRESSED_DIAMOND, "compressed_diamond", ModItems.COMPRESSED_DIAMOND_HELMET, ModItems.COMPRESSED_DIAMOND_CHESTPLATE, ModItems.COMPRESSED_DIAMOND_LEGGINGS, ModItems.COMPRESSED_DIAMOND_BOOTS);
                offerMechanicalSwordRecipe(itemLookup, exporter, ModItems.COMPRESSED_IRON_INGOT, ModItems.COMPRESSED_IRON_SWORD, "compressed_iron");
                offerMechanicalSwordRecipe(itemLookup, exporter, ModItems.COMPRESSED_GOLD_INGOT, ModItems.COMPRESSED_GOLD_SWORD, "compressed_gold");
                offerMechanicalSwordRecipe(itemLookup, exporter, ModItems.COMPRESSED_DIAMOND, ModItems.COMPRESSED_DIAMOND_SWORD, "compressed_diamond");

                offerCompressedNetheriteUpgrade(exporter, ModItems.COMPRESSED_DIAMOND_HELMET, ModItems.COMPRESSED_NETHERITE_HELMET);
                offerCompressedNetheriteUpgrade(exporter, ModItems.COMPRESSED_DIAMOND_CHESTPLATE, ModItems.COMPRESSED_NETHERITE_CHESTPLATE);
                offerCompressedNetheriteUpgrade(exporter, ModItems.COMPRESSED_DIAMOND_LEGGINGS, ModItems.COMPRESSED_NETHERITE_LEGGINGS);
                offerCompressedNetheriteUpgrade(exporter, ModItems.COMPRESSED_DIAMOND_BOOTS, ModItems.COMPRESSED_NETHERITE_BOOTS);
                offerCompressedNetheriteUpgrade(exporter, ModItems.COMPRESSED_DIAMOND_SWORD, ModItems.COMPRESSED_NETHERITE_SWORD);

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

            private void offerMechanicalArmorRecipes(
                    RegistryWrapper.Impl<Item> itemLookup,
                    RecipeExporter exporter,
                    ItemConvertible ingredient,
                    String materialName,
                    ItemConvertible helmet,
                    ItemConvertible chestplate,
                    ItemConvertible leggings,
                    ItemConvertible boots
            ) {
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, helmet)
                        .pattern("xxx")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                        .offerTo(exporter, "mob_battle:mechanical/" + materialName + "_helmet");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, chestplate)
                        .pattern("x x")
                        .pattern("xxx")
                        .pattern("xxx")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                        .offerTo(exporter, "mob_battle:mechanical/" + materialName + "_chestplate");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, leggings)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                        .offerTo(exporter, "mob_battle:mechanical/" + materialName + "_leggings");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, boots)
                        .pattern("x x")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                        .offerTo(exporter, "mob_battle:mechanical/" + materialName + "_boots");
            }

            private void offerCompressedNetheriteUpgrade(RecipeExporter exporter, ItemConvertible base, Item result) {
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(base),
                                Ingredient.ofItem(ModItems.COMPRESSED_NETHERITE_INGOT),
                                RecipeCategory.COMBAT,
                                result
                        )
                        .criterion(hasItem(ModItems.COMPRESSED_NETHERITE_INGOT), conditionsFromItem(ModItems.COMPRESSED_NETHERITE_INGOT))
                        .offerTo(exporter, "mob_battle:compressed_netherite_upgrade_" + Registries.ITEM.getId(result).getPath());
            }

            private void offerMechanicalSwordRecipe(RegistryWrapper.Impl<Item> itemLookup, RecipeExporter exporter, ItemConvertible ingredient, ItemConvertible sword, String materialName) {
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, sword)
                        .pattern("x")
                        .pattern("x")
                        .pattern("s")
                        .input('x', ingredient)
                        .input('s', Items.STICK)
                        .group("mechanical_" + materialName + "_sword")
                        .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                        .offerTo(exporter, "mob_battle:mechanical/" + materialName + "_sword");
            }
        };
    }

    @Override
    public String getName() {
        return Mob_battle.MOD_ID + " Recipes";
    }
}
