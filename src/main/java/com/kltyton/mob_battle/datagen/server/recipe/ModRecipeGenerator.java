package com.kltyton.mob_battle.datagen.server.recipe;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

/*    // 此处也可以使用 List.of(...) 方法
    public static final List<ItemConvertible> SMELTABLE_TO_EXAMPLE_INGOT = Util.make(Lists.newArrayList(), list -> {
        list.add(EXAMPLE_ORE);
        list.add(DEEPSLATE_EXAMPLE_ORE);
        list.add(RAW_EXAMPLE);
    });*/
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                oreSmelting(
                        List.of(ModItems.INCUBATION_EGG), // Inputs
                        RecipeCategory.FOOD, // Category
                        ModItems.COOKED_HIGHBIRD_EGG, // Output
                        0.1f, // Experience
                        300, // Cooking time
                        "food" // group
                );
                shaped(RecipeCategory.TOOLS, ModItems.FINE_KNIFE, 1)
                        .pattern("a")
                        .pattern("b")
                        .define('a', Items.IRON_INGOT)
                        .define('b', Items.GOLD_INGOT)
                        .group("fine_knife") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.DECORATIONS, ModBlocks.MACHINE_WORKTABLE_BLOCK, 1)
                        .pattern("aba")
                        .pattern("cdc")
                        .pattern("efe")
                        .define('a', ModItems.WIRE)
                        .define('b', ModItems.COMPRESSED_COPPER_INGOT)
                        .define('c', ModItems.COMPRESSED_REDSTONE)
                        .define('d', ModItems.COMPRESSED_IRON_INGOT)
                        .define('e', ModItems.ELECTRONIC_COMPONENTS)
                        .define('f', Items.CRAFTING_TABLE)
                        .group("machine_worktable")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.HEART_STONE, 1)
                        .pattern(" a ")
                        .pattern("aba")
                        .pattern(" a ")
                        .define('a', Items.REDSTONE)
                        .define('b', Items.COBBLESTONE)
                        .group("heart_stone")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.SMALL_BACKPACK, 1)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("xxx")
                        .define('x', Items.RABBIT_HIDE)
                        .group("backpack") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.TRAIN_BULLET, 1)
                        .pattern("a")
                        .pattern("b")
                        .define('a', Items.IRON_INGOT)
                        .define('b', Items.LAPIS_LAZULI)
                        .group("bullet") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.WIRE, 1)
                        .pattern(" c ")
                        .pattern("aba")
                        .pattern(" c ")
                        .define('a', Items.REDSTONE)
                        .define('b', Items.COPPER_INGOT)
                        .define('c', Items.RESIN_CLUMP)
                        .group("wire") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.ELECTRONIC_COMPONENTS, 1)
                        .pattern("dad")
                        .pattern("cbc")
                        .pattern("dad")
                        .define('a', Items.GOLD_BLOCK)
                        .define('b', Items.CRAFTING_TABLE)
                        .define('c', Items.REDSTONE_BLOCK)
                        .define('d', ModItems.WIRE)
                        .group("electronic_components") //将其放入名为“multi_bench”的组中-组显示在配方书的一个槽中
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                // 机械工作台专用配方示例：这些配方只能在机械工作台中合成。
                // 写法和 createShaped 基本相同，但会生成 type 为 mob_battle:mechanical_crafting_shaped 的 JSON。
                // 如果输出物品已经有普通配方，offerTo 第二个参数请写一个独立 ID，避免和普通配方重名。
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_IRON_BLOCK)
                        .pattern("iii")
                        .pattern("iii")
                        .pattern("iii")
                        .input('i', ModItems.COMPRESSED_IRON_INGOT)
                        .group("mechanical_compressed_blocks")
                        .unlockedBy(getHasName(ModItems.COMPRESSED_IRON_INGOT), has(ModItems.COMPRESSED_IRON_INGOT))
                        .save(output, "mob_battle:mechanical/compressed_iron_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_GOLD_BLOCK)
                        .pattern("ggg")
                        .pattern("ggg")
                        .pattern("ggg")
                        .input('g', ModItems.COMPRESSED_GOLD_INGOT)
                        .group("mechanical_compressed_blocks")
                        .unlockedBy(getHasName(ModItems.COMPRESSED_GOLD_INGOT), has(ModItems.COMPRESSED_GOLD_INGOT))
                        .save(output, "mob_battle:mechanical/compressed_gold_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_DIAMOND_BLOCK)
                        .pattern("ddd")
                        .pattern("ddd")
                        .pattern("ddd")
                        .input('d', ModItems.COMPRESSED_DIAMOND)
                        .group("mechanical_compressed_blocks")
                        .unlockedBy(getHasName(ModItems.COMPRESSED_DIAMOND), has(ModItems.COMPRESSED_DIAMOND))
                        .save(output, "mob_battle:mechanical/compressed_diamond_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_NETHERITE_BLOCK)
                        .pattern("nnn")
                        .pattern("nnn")
                        .pattern("nnn")
                        .input('n', ModItems.COMPRESSED_NETHERITE_INGOT)
                        .group("mechanical_compressed_blocks")
                        .unlockedBy(getHasName(ModItems.COMPRESSED_NETHERITE_INGOT), has(ModItems.COMPRESSED_NETHERITE_INGOT))
                        .save(output, "mob_battle:mechanical/compressed_netherite_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_IRON_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_IRON_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .unlockedBy(getHasName(ModBlocks.COMPRESSED_IRON_BLOCK), has(ModBlocks.COMPRESSED_IRON_BLOCK))
                        .save(output, "mob_battle:mechanical/compressed_iron_ingot_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_GOLD_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_GOLD_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .unlockedBy(getHasName(ModBlocks.COMPRESSED_GOLD_BLOCK), has(ModBlocks.COMPRESSED_GOLD_BLOCK))
                        .save(output, "mob_battle:mechanical/compressed_gold_ingot_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_DIAMOND, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_DIAMOND_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .unlockedBy(getHasName(ModBlocks.COMPRESSED_DIAMOND_BLOCK), has(ModBlocks.COMPRESSED_DIAMOND_BLOCK))
                        .save(output, "mob_battle:mechanical/compressed_diamond_from_block");

                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.COMPRESSED_NETHERITE_INGOT, 9)
                        .pattern("b")
                        .input('b', ModBlocks.COMPRESSED_NETHERITE_BLOCK)
                        .group("mechanical_compressed_block_unpacking")
                        .unlockedBy(getHasName(ModBlocks.COMPRESSED_NETHERITE_BLOCK), has(ModBlocks.COMPRESSED_NETHERITE_BLOCK))
                        .save(output, "mob_battle:mechanical/compressed_netherite_ingot_from_block");

                offerMechanicalArmorRecipes(itemLookup, output, ModItems.COMPRESSED_IRON_INGOT, "compressed_iron", ModItems.COMPRESSED_IRON_HELMET, ModItems.COMPRESSED_IRON_CHESTPLATE, ModItems.COMPRESSED_IRON_LEGGINGS, ModItems.COMPRESSED_IRON_BOOTS);
                offerMechanicalArmorRecipes(itemLookup, output, ModItems.COMPRESSED_GOLD_INGOT, "compressed_gold", ModItems.COMPRESSED_GOLD_HELMET, ModItems.COMPRESSED_GOLD_CHESTPLATE, ModItems.COMPRESSED_GOLD_LEGGINGS, ModItems.COMPRESSED_GOLD_BOOTS);
                offerMechanicalArmorRecipes(itemLookup, output, ModItems.COMPRESSED_DIAMOND, "compressed_diamond", ModItems.COMPRESSED_DIAMOND_HELMET, ModItems.COMPRESSED_DIAMOND_CHESTPLATE, ModItems.COMPRESSED_DIAMOND_LEGGINGS, ModItems.COMPRESSED_DIAMOND_BOOTS);
                offerMechanicalSwordRecipe(itemLookup, output, ModItems.COMPRESSED_IRON_INGOT, ModItems.COMPRESSED_IRON_SWORD, "compressed_iron");
                offerMechanicalSwordRecipe(itemLookup, output, ModItems.COMPRESSED_GOLD_INGOT, ModItems.COMPRESSED_GOLD_SWORD, "compressed_gold");
                offerMechanicalSwordRecipe(itemLookup, output, ModItems.COMPRESSED_DIAMOND, ModItems.COMPRESSED_DIAMOND_SWORD, "compressed_diamond");

                offerCompressedNetheriteUpgrade(output, ModItems.COMPRESSED_DIAMOND_HELMET, ModItems.COMPRESSED_NETHERITE_HELMET);
                offerCompressedNetheriteUpgrade(output, ModItems.COMPRESSED_DIAMOND_CHESTPLATE, ModItems.COMPRESSED_NETHERITE_CHESTPLATE);
                offerCompressedNetheriteUpgrade(output, ModItems.COMPRESSED_DIAMOND_LEGGINGS, ModItems.COMPRESSED_NETHERITE_LEGGINGS);
                offerCompressedNetheriteUpgrade(output, ModItems.COMPRESSED_DIAMOND_BOOTS, ModItems.COMPRESSED_NETHERITE_BOOTS);
                offerCompressedNetheriteUpgrade(output, ModItems.COMPRESSED_DIAMOND_SWORD, ModItems.COMPRESSED_NETHERITE_SWORD);

                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.DIAMOND_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_DIAMOND
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.IRON_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_IRON_INGOT
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.GOLD_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_GOLD_INGOT
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.COPPER_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_COPPER_INGOT
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.NETHERITE_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_NETHERITE_INGOT
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.REDSTONE_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_REDSTONE
                );
                nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        Items.LAPIS_BLOCK,
                        RecipeCategory.BUILDING_BLOCKS,
                        ModItems.COMPRESSED_LAPIS_LAZULI
                );
            }

            private void offerMechanicalArmorRecipes(
                    HolderLookup.RegistryLookup<Item> itemLookup,
                    RecipeOutput exporter,
                    ItemLike ingredient,
                    String materialName,
                    ItemLike helmet,
                    ItemLike chestplate,
                    ItemLike leggings,
                    ItemLike boots
            ) {
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, helmet)
                        .pattern("xxx")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .unlockedBy(getHasName(ingredient), has(ingredient))
                        .save(exporter, "mob_battle:mechanical/" + materialName + "_helmet");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, chestplate)
                        .pattern("x x")
                        .pattern("xxx")
                        .pattern("xxx")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .unlockedBy(getHasName(ingredient), has(ingredient))
                        .save(exporter, "mob_battle:mechanical/" + materialName + "_chestplate");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, leggings)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .unlockedBy(getHasName(ingredient), has(ingredient))
                        .save(exporter, "mob_battle:mechanical/" + materialName + "_leggings");
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, boots)
                        .pattern("x x")
                        .pattern("x x")
                        .input('x', ingredient)
                        .group("mechanical_" + materialName + "_armor")
                        .unlockedBy(getHasName(ingredient), has(ingredient))
                        .save(exporter, "mob_battle:mechanical/" + materialName + "_boots");
            }

            private void offerCompressedNetheriteUpgrade(RecipeOutput exporter, ItemLike base, Item result) {
                SmithingTransformRecipeBuilder.smithing(
                                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.of(base),
                                Ingredient.of(ModItems.COMPRESSED_NETHERITE_INGOT),
                                RecipeCategory.COMBAT,
                                result
                        )
                        .unlocks(getHasName(ModItems.COMPRESSED_NETHERITE_INGOT), has(ModItems.COMPRESSED_NETHERITE_INGOT))
                        .save(exporter, "mob_battle:compressed_netherite_upgrade_" + BuiltInRegistries.ITEM.getKey(result).getPath());
            }

            private void offerMechanicalSwordRecipe(HolderLookup.RegistryLookup<Item> itemLookup, RecipeOutput exporter, ItemLike ingredient, ItemLike sword, String materialName) {
                MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.COMBAT, sword)
                        .pattern("x")
                        .pattern("x")
                        .pattern("s")
                        .input('x', ingredient)
                        .input('s', Items.STICK)
                        .group("mechanical_" + materialName + "_sword")
                        .unlockedBy(getHasName(ingredient), has(ingredient))
                        .save(exporter, "mob_battle:mechanical/" + materialName + "_sword");
            }
        };
    }

    @Override
    public String getName() {
        return Mob_battle.MOD_ID + " Recipes";
    }
}
