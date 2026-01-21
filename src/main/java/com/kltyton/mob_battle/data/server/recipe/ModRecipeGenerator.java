package com.kltyton.mob_battle.data.server.recipe;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
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
            }
        };
    }

    @Override
    public String getName() {
        return Mob_battle.MOD_ID + " Recipes";
    }
}