package com.kltyton.mob_battle.recipe;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<CraftingRecipe> MECHANICAL_CRAFTING = Registry.register(
            Registries.RECIPE_TYPE,
            Identifier.of(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeType<>() {
                @Override
                public String toString() {
                    return Mob_battle.MOD_ID + ":mechanical_crafting";
                }
            }
    );

    public static final RecipeBookCategory MECHANICAL_CRAFTING_CATEGORY = Registry.register(
            Registries.RECIPE_BOOK_CATEGORY,
            Identifier.of(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeBookCategory()
    );

    public static final RecipeSerializer<MechanicalShapedRecipe> MECHANICAL_SHAPED_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier.of(Mob_battle.MOD_ID, "mechanical_crafting_shaped"),
            new MechanicalShapedRecipe.Serializer()
    );
    public static void init() {
    }
}
