package com.kltyton.mob_battle.recipe;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;

public interface MechanicalCraftingRecipe extends CraftingRecipe {
    @Override
    default RecipeType<CraftingRecipe> getType() {
        return ModRecipeTypes.MECHANICAL_CRAFTING;
    }
}
