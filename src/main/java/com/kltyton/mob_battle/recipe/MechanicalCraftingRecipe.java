package com.kltyton.mob_battle.recipe;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface MechanicalCraftingRecipe extends CraftingRecipe {
    @Override
    default RecipeType<CraftingRecipe> getType() {
        return ModRecipeTypes.MECHANICAL_CRAFTING;
    }
}
