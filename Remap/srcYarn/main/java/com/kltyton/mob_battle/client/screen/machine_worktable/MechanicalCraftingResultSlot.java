package com.kltyton.mob_battle.client.screen.machine_worktable;

import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MechanicalCraftingResultSlot extends Slot {
    private final RecipeInputInventory input;
    private final PlayerEntity player;
    private int amount;

    public MechanicalCraftingResultSlot(PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.input = input;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }

        return super.takeStack(amount);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onTake(int amount) {
        this.amount += amount;
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        if (this.amount > 0) {
            stack.onCraftByPlayer(this.player, this.amount);
        }

        if (this.inventory instanceof RecipeUnlocker recipeUnlocker) {
            recipeUnlocker.unlockLastRecipe(this.player, this.input.getHeldStacks());
        }

        this.amount = 0;
    }

    private DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input, World world) {
        return world instanceof ServerWorld serverWorld
                ? serverWorld.getRecipeManager()
                        .getFirstMatch(ModRecipeTypes.MECHANICAL_CRAFTING, input, serverWorld)
                        .map(recipe -> ((CraftingRecipe) recipe.value()).getRecipeRemainders(input))
                        .orElseGet(() -> CraftingRecipe.collectRecipeRemainders(input))
                : CraftingRecipe.collectRecipeRemainders(input);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        CraftingRecipeInput.Positioned positioned = this.input.createPositionedRecipeInput();
        CraftingRecipeInput craftingRecipeInput = positioned.input();
        int left = positioned.left();
        int top = positioned.top();
        DefaultedList<ItemStack> remainders = this.getRecipeRemainders(craftingRecipeInput, player.getWorld());

        for (int y = 0; y < craftingRecipeInput.getHeight(); y++) {
            for (int x = 0; x < craftingRecipeInput.getWidth(); x++) {
                int slotIndex = x + left + (y + top) * this.input.getWidth();
                ItemStack inputStack = this.input.getStack(slotIndex);
                ItemStack remainder = remainders.get(x + y * craftingRecipeInput.getWidth());
                if (!inputStack.isEmpty()) {
                    this.input.removeStack(slotIndex, 1);
                    inputStack = this.input.getStack(slotIndex);
                }

                if (!remainder.isEmpty()) {
                    if (inputStack.isEmpty()) {
                        this.input.setStack(slotIndex, remainder);
                    } else if (ItemStack.areItemsAndComponentsEqual(inputStack, remainder)) {
                        remainder.increment(inputStack.getCount());
                        this.input.setStack(slotIndex, remainder);
                    } else if (!this.player.getInventory().insertStack(remainder)) {
                        this.player.dropItem(remainder, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean disablesDynamicDisplay() {
        return true;
    }
}
