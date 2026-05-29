package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.client.screen.ModScreenHandlers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PagedBackpackScreenHandler extends AbstractContainerMenu {
    private final BackpackInventory inventory;
    private final PageInventory pageInventory;
    private final Player player;
    private final DataSlot page = DataSlot.standalone();
    private boolean loadingPage;
    private int lastBackpackSlotClickAge = -100;
    private int lastPageChangeAge = -100;

    public PagedBackpackScreenHandler(int syncId, Inventory playerInventory, BackpackInventory inventory) {
        super(ModScreenHandlers.PAGED_BACKPACK, syncId);
        this.inventory = inventory;
        this.pageInventory = new PageInventory();
        this.player = playerInventory.player;
        this.addDataSlot(this.page);
        inventory.startOpen(playerInventory.player);
        checkContainerSize(inventory, BackpackInventory.PAGED_TOTAL_SLOTS);
        this.page.set(0);
        this.loadVisiblePage();

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new BackpackSlot(this.pageInventory, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }

        int playerInvY = 139;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = playerInvY + 58;
        for (int slot = 0; slot < 9; slot++) {
            this.addSlot(new Slot(playerInventory, slot, 8 + slot * 18, hotbarY));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.tickCount - this.lastBackpackSlotClickAge <= 2) {
            return true;
        }

        int currentPage = this.page.get();
        int nextPage = switch (id) {
            case 0 -> Math.max(0, currentPage - 1);
            case 1 -> Math.min(BackpackInventory.MAX_PAGES - 1, currentPage + 1);
            default -> currentPage;
        };

        if (nextPage == currentPage) {
            return id == 0 || id == 1;
        }

        this.resetQuickCraft();
        this.saveVisiblePage();
        this.page.set(nextPage);
        this.loadVisiblePage();
        this.lastPageChangeAge = player.tickCount;
        this.broadcastChanges();
        return true;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (slotIndex >= 0 && slotIndex < BackpackInventory.PAGE_SIZE) {
            if (player.tickCount - this.lastPageChangeAge <= 2) {
                return;
            }
            this.lastBackpackSlotClickAge = player.tickCount;
        }
        super.clicked(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index < BackpackInventory.PAGE_SIZE) {
                if (!this.moveItemStackTo(slotStack, BackpackInventory.PAGE_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, BackpackInventory.PAGE_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.saveVisiblePage();
        this.inventory.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    public int getPage() {
        return this.page.get();
    }

    private int getPageStart() {
        return this.page.get() * BackpackInventory.PAGE_SIZE;
    }

    private void loadVisiblePage() {
        this.loadingPage = true;
        try {
            int pageStart = this.getPageStart();
            for (int slot = 0; slot < BackpackInventory.PAGE_SIZE; slot++) {
                this.pageInventory.setItem(slot, this.inventory.getItem(pageStart + slot).copy());
            }
        } finally {
            this.loadingPage = false;
        }
    }

    private void saveVisiblePage() {
        if (this.player.level().isClientSide()) {
            return;
        }

        int pageStart = this.getPageStart();
        NonNullList<ItemStack> storedStacks = this.inventory.getItems();
        for (int slot = 0; slot < BackpackInventory.PAGE_SIZE; slot++) {
            storedStacks.set(pageStart + slot, this.pageInventory.getItem(slot).copy());
        }
        this.inventory.setChanged();
    }

    private class PageInventory extends SimpleContainer {
        private PageInventory() {
            super(BackpackInventory.PAGE_SIZE);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (!loadingPage) {
                saveVisiblePage();
            }
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem);
        }
    }

    private static class BackpackSlot extends Slot {
        private BackpackSlot(PageInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem) && super.mayPlace(stack);
        }
    }
}
