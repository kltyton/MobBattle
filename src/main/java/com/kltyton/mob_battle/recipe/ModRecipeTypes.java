package com.kltyton.mob_battle.recipe;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final RecipeType<CraftingRecipe> MECHANICAL_CRAFTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeType<>() {
                @Override
                public String toString() {
                    return Mob_battle.MOD_ID + ":mechanical_crafting";
                }
            }
    );

    public static final RecipeBookCategory MECHANICAL_CRAFTING_CATEGORY = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeBookCategory()
    );

    public static final RecipeSerializer<MechanicalShapedRecipe> MECHANICAL_SHAPED_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting_shaped"),
            new MechanicalShapedRecipe.Serializer()
    );
    public static void init() {
    }
}
