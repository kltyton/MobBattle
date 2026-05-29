package com.kltyton.mob_battle.client.screen.machine_worktable;

import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

public class MechanicalCraftingResultSlot extends Slot {
    private final CraftingContainer input;
    private final Player player;
    private int amount;

    public MechanicalCraftingResultSlot(Player player, CraftingContainer input, Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.input = input;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void onSwapCraft(int amount) {
        this.amount += amount;
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.amount > 0) {
            stack.onCraftedBy(this.player, this.amount);
        }

        if (this.container instanceof RecipeCraftingHolder recipeUnlocker) {
            recipeUnlocker.awardUsedRecipes(this.player, this.input.getItems());
        }

        this.amount = 0;
    }

    private NonNullList<ItemStack> getRecipeRemainders(CraftingInput input, Level world) {
        return world instanceof ServerLevel serverWorld
                ? serverWorld.recipeAccess()
                        .getRecipeFor(ModRecipeTypes.MECHANICAL_CRAFTING, input, serverWorld)
                        .map(recipe -> ((CraftingRecipe) recipe.value()).getRemainingItems(input))
                        .orElseGet(() -> CraftingRecipe.defaultCraftingReminder(input))
                : CraftingRecipe.defaultCraftingReminder(input);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        CraftingInput.Positioned positioned = this.input.asPositionedCraftInput();
        CraftingInput craftingRecipeInput = positioned.input();
        int left = positioned.left();
        int top = positioned.top();
        NonNullList<ItemStack> remainders = this.getRecipeRemainders(craftingRecipeInput, player.level());

        for (int y = 0; y < craftingRecipeInput.height(); y++) {
            for (int x = 0; x < craftingRecipeInput.width(); x++) {
                int slotIndex = x + left + (y + top) * this.input.getWidth();
                ItemStack inputStack = this.input.getItem(slotIndex);
                ItemStack remainder = remainders.get(x + y * craftingRecipeInput.width());
                if (!inputStack.isEmpty()) {
                    this.input.removeItem(slotIndex, 1);
                    inputStack = this.input.getItem(slotIndex);
                }

                if (!remainder.isEmpty()) {
                    if (inputStack.isEmpty()) {
                        this.input.setItem(slotIndex, remainder);
                    } else if (ItemStack.isSameItemSameComponents(inputStack, remainder)) {
                        remainder.grow(inputStack.getCount());
                        this.input.setItem(slotIndex, remainder);
                    } else if (!this.player.getInventory().add(remainder)) {
                        this.player.drop(remainder, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
