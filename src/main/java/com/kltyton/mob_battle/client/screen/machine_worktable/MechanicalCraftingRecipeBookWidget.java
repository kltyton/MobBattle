package com.kltyton.mob_battle.client.screen.machine_worktable;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MechanicalCraftingRecipeBookWidget extends RecipeBookWidget<MechanicalWorktableScreenHandler> {
    private static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.ofVanilla("recipe_book/filter_enabled"),
            Identifier.ofVanilla("recipe_book/filter_disabled"),
            Identifier.ofVanilla("recipe_book/filter_enabled_highlighted"),
            Identifier.ofVanilla("recipe_book/filter_disabled_highlighted")
    );
    private static final Text TOGGLE_CRAFTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.craftable");
    private static final List<RecipeBookWidget.Tab> TABS = List.of(
            new RecipeBookWidget.Tab(
                    new ItemStack(ModBlocks.MACHINE_WORKTABLE_BLOCK),
                    Optional.empty(),
                    ModRecipeTypes.MECHANICAL_CRAFTING_CATEGORY
            )
    );

    public MechanicalCraftingRecipeBookWidget(MechanicalWorktableScreenHandler screenHandler) {
        super(screenHandler, TABS);
    }

    @Override
    protected boolean isValid(Slot slot) {
        return this.craftingScreenHandler.getOutputSlot() == slot || this.craftingScreenHandler.getInputSlots().contains(slot);
    }

    private boolean canDisplay(RecipeDisplay display) {
        int width = this.craftingScreenHandler.getWidth();
        int height = this.craftingScreenHandler.getHeight();

        return switch (display) {
            case ShapedCraftingRecipeDisplay shaped -> width >= shaped.width() && height >= shaped.height();
            default -> false;
        };
    }

    @Override
    protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
        ghostRecipe.addResults(this.craftingScreenHandler.getOutputSlot(), context, display.result());
        if (display instanceof ShapedCraftingRecipeDisplay shaped) {
            List<Slot> slots = this.craftingScreenHandler.getInputSlots();
            RecipeGridAligner.alignRecipeToGrid(
                    this.craftingScreenHandler.getWidth(),
                    this.craftingScreenHandler.getHeight(),
                    shaped.width(),
                    shaped.height(),
                    shaped.ingredients(),
                    (slot, index, x, y) -> {
                        Slot inputSlot = slots.get(index);
                        ghostRecipe.addInputs(inputSlot, context, slot);
                    }
            );
        }
    }

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextures(TEXTURES);
    }

    @Override
    protected Text getToggleCraftableButtonText() {
        return TOGGLE_CRAFTABLE_TEXT;
    }

    @Override
    protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
        recipeResultCollection.populateRecipes(recipeFinder, this::canDisplay);
    }
}
