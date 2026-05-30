package com.kltyton.mob_battle.recipe;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final RecipeType<CraftingRecipe> MECHANICAL_CRAFTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeType<>() {
                @Override
                public String toString() {
                    return Mob_battle.MOD_ID + ":mechanical_crafting";
                }
            }
    );

    public static final RecipeBookCategory MECHANICAL_CRAFTING_CATEGORY = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting"),
            new RecipeBookCategory()
    );

    public static final RecipeSerializer<MechanicalShapedRecipe> MECHANICAL_SHAPED_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_crafting_shaped"),
            new RecipeSerializer<>(MechanicalShapedRecipe.Serializer.CODEC, MechanicalShapedRecipe.Serializer.PACKET_CODEC)
    );
    public static void init() {
    }
}
