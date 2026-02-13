package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.client.screen.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PagedBackpackScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    // 使用 Property 自动同步页码
    private final Property page = Property.create();

    public PagedBackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.PAGED_BACKPACK, syncId);
        this.inventory = inventory;
        this.addProperty(this.page);
        inventory.onOpen(playerInventory.player);
        checkSize(inventory, 54);
        // 添加 54 个映射 Slot
        int rows = 6;
        // 1. 添加背包容器 Slot (保持不变)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new PagedSlot(inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // 2. 添加玩家主背包 (3x9)
        int playerInvY = 139; // 核心修正点
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, playerInvY + i * 18));
            }
        }

        // 3. 添加玩家快捷栏 (1x9)
        int hotbarY = playerInvY + 58; // 139 + 3*18 + 4
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, hotbarY));
        }
        this.page.set(0);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        int currentPage = this.page.get();
        if (id == 0) { // 上一页
            this.page.set(Math.max(0, currentPage - 1));
        } else if (id == 1) { // 下一页
            this.page.set(Math.min(BackpackInventory.MAX_PAGES - 1, currentPage + 1));
        }
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 54) {
                if (!this.insertItem(itemStack2, 54, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.insertItem(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return this.inventory.canPlayerUse(player); }

    public int getPage() { return this.page.get(); }

    private class PagedSlot extends Slot {
        public PagedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        private int getActualIndex() {
            return super.getIndex() + (page.get() * BackpackInventory.PAGE_SIZE);
        }

        @Override
        public ItemStack getStack() {
            return this.inventory.getStack(this.getActualIndex());
        }

        @Override
        public void setStack(ItemStack stack) {
            this.inventory.setStack(this.getActualIndex(), stack);
            this.markDirty();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.inventory.removeStack(this.getActualIndex(), amount);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.inventory.isValid(this.getActualIndex(), stack);
        }
    }
}