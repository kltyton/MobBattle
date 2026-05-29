package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.client.screen.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class PagedBackpackScreenHandler extends ScreenHandler {
    private final BackpackInventory inventory;
    private final PageInventory pageInventory;
    private final PlayerEntity player;
    private final Property page = Property.create();
    private boolean loadingPage;
    private int lastBackpackSlotClickAge = -100;
    private int lastPageChangeAge = -100;

    public PagedBackpackScreenHandler(int syncId, PlayerInventory playerInventory, BackpackInventory inventory) {
        super(ModScreenHandlers.PAGED_BACKPACK, syncId);
        this.inventory = inventory;
        this.pageInventory = new PageInventory();
        this.player = playerInventory.player;
        this.addProperty(this.page);
        inventory.onOpen(playerInventory.player);
        checkSize(inventory, BackpackInventory.PAGED_TOTAL_SLOTS);
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
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (player.age - this.lastBackpackSlotClickAge <= 2) {
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

        this.endQuickCraft();
        this.saveVisiblePage();
        this.page.set(nextPage);
        this.loadVisiblePage();
        this.lastPageChangeAge = player.age;
        this.sendContentUpdates();
        return true;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < BackpackInventory.PAGE_SIZE) {
            if (player.age - this.lastPageChangeAge <= 2) {
                return;
            }
            this.lastBackpackSlotClickAge = player.age;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (index < BackpackInventory.PAGE_SIZE) {
                if (!this.insertItem(slotStack, BackpackInventory.PAGE_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 0, BackpackInventory.PAGE_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.saveVisiblePage();
        this.inventory.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
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
                this.pageInventory.setStack(slot, this.inventory.getStack(pageStart + slot).copy());
            }
        } finally {
            this.loadingPage = false;
        }
    }

    private void saveVisiblePage() {
        if (this.player.getWorld().isClient()) {
            return;
        }

        int pageStart = this.getPageStart();
        DefaultedList<ItemStack> storedStacks = this.inventory.getHeldStacks();
        for (int slot = 0; slot < BackpackInventory.PAGE_SIZE; slot++) {
            storedStacks.set(pageStart + slot, this.pageInventory.getStack(slot).copy());
        }
        this.inventory.markDirty();
    }

    private class PageInventory extends SimpleInventory {
        private PageInventory() {
            super(BackpackInventory.PAGE_SIZE);
        }

        @Override
        public void markDirty() {
            super.markDirty();
            if (!loadingPage) {
                saveVisiblePage();
            }
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem);
        }
    }

    private static class BackpackSlot extends Slot {
        private BackpackSlot(PageInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem) && super.canInsert(stack);
        }
    }
}
