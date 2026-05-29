package com.kltyton.mob_battle.client.screen.machine_worktable;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MechanicalCraftingRecipeBookWidget extends RecipeBookComponent<MechanicalWorktableScreenHandler> {
    private static final WidgetSprites TEXTURES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted")
    );
    private static final Component TOGGLE_CRAFTABLE_TEXT = Component.translatable("gui.recipebook.toggleRecipes.craftable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(
                    new ItemStack(ModBlocks.MACHINE_WORKTABLE_BLOCK),
                    Optional.empty(),
                    ModRecipeTypes.MECHANICAL_CRAFTING_CATEGORY
            )
    );

    public MechanicalCraftingRecipeBookWidget(MechanicalWorktableScreenHandler screenHandler) {
        super(screenHandler, TABS);
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return this.menu.getResultSlot() == slot || this.menu.getInputGridSlots().contains(slot);
    }

    private boolean canDisplay(RecipeDisplay display) {
        int width = this.menu.getGridWidth();
        int height = this.menu.getGridHeight();

        return switch (display) {
            case ShapedCraftingRecipeDisplay shaped -> width >= shaped.width() && height >= shaped.height();
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostRecipe, RecipeDisplay display, ContextMap context) {
        ghostRecipe.setResult(this.menu.getResultSlot(), context, display.result());
        if (display instanceof ShapedCraftingRecipeDisplay shaped) {
            List<Slot> slots = this.menu.getInputGridSlots();
            PlaceRecipeHelper.placeRecipe(
                    this.menu.getGridWidth(),
                    this.menu.getGridHeight(),
                    shaped.width(),
                    shaped.height(),
                    shaped.ingredients(),
                    (slot, index, x, y) -> {
                        Slot inputSlot = slots.get(index);
                        ghostRecipe.setInput(inputSlot, context, slot);
                    }
            );
        }
    }

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(TEXTURES);
    }

    @Override
    protected Component getRecipeFilterName() {
        return TOGGLE_CRAFTABLE_TEXT;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeResultCollection, StackedItemContents recipeFinder) {
        recipeResultCollection.selectRecipes(recipeFinder, this::canDisplay);
    }
}
