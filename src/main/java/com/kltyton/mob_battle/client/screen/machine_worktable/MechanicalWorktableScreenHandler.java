package com.kltyton.mob_battle.client.screen.machine_worktable;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.client.screen.ModScreenHandlers;
import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class MechanicalWorktableScreenHandler extends AbstractCraftingMenu {
    private final ContainerLevelAccess context;
    private final Player player;
    private boolean filling;

    public MechanicalWorktableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public MechanicalWorktableScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(ModScreenHandlers.MECHANICAL_WORKTABLE, syncId, 3, 3);
        this.context = context;
        this.player = playerInventory.player;
        this.addSlot(new MechanicalCraftingResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        this.addCraftingGridSlots(30, 17);
        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    protected static void updateResult(
            MechanicalWorktableScreenHandler handler,
            ServerLevel world,
            Player player,
            CraftingContainer craftingInventory,
            ResultContainer resultInventory,
            @Nullable RecipeHolder<CraftingRecipe> recipe
    ) {
        CraftingInput craftingRecipeInput = craftingInventory.asCraftInput();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack resultStack = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> optional = world.getServer()
                .getRecipeManager()
                .getRecipeFor(ModRecipeTypes.MECHANICAL_CRAFTING, craftingRecipeInput, world);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeEntry = optional.get();
            CraftingRecipe craftingRecipe = recipeEntry.value();
            if (resultInventory.setRecipeUsed(serverPlayer, recipeEntry)) {
                ItemStack craftedStack = craftingRecipe.assemble(craftingRecipeInput, world.registryAccess());
                if (craftedStack.isItemEnabled(world.enabledFeatures())) {
                    resultStack = craftedStack;
                }
            }
        }

        resultInventory.setItem(0, resultStack);
        handler.setRemoteSlot(0, resultStack);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, resultStack));
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (!this.filling) {
            this.context.execute((world, pos) -> {
                if (world instanceof ServerLevel serverWorld) {
                    updateResult(this, serverWorld, this.player, this.craftSlots, this.resultSlots, null);
                }
            });
        }
    }

    @Override
    public void beginPlacingRecipe() {
        this.filling = true;
    }

    @Override
    public void finishPlacingRecipe(ServerLevel world, RecipeHolder<CraftingRecipe> recipe) {
        this.filling = false;
        updateResult(this, world, this.player, this.craftSlots, this.resultSlots, recipe);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, ModBlocks.MACHINE_WORKTABLE_BLOCK);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                itemStack2.getItem().onCraftedBy(itemStack2, player);
                if (!this.moveItemStackTo(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot >= 10 && slot < 46) {
                if (!this.moveItemStackTo(itemStack2, 1, 10, false)) {
                    if (slot < 37) {
                        if (!this.moveItemStackTo(itemStack2, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
            if (slot == 0) {
                player.drop(itemStack2, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public Slot getResultSlot() {
        return this.slots.get(0);
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return this.slots.subList(1, 10);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    protected Player owner() {
        return this.player;
    }
}
