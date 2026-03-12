package com.kltyton.mob_battle.items.tool.backpack;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BackpackInventory extends SimpleInventory {
    private final ItemStack stack;
    public static final int PAGE_SIZE = 54;
    public static final int MAX_PAGES = 50;
    public static final int PAGED_TOTAL_SLOTS = PAGE_SIZE * MAX_PAGES;

    public BackpackInventory(ItemStack stack, int slotCount) {
        super(slotCount);
        this.stack = stack;
        ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
        if (component != null) {
            DefaultedList<ItemStack> heldStacks = this.getHeldStacks();
            component.copyTo(heldStacks);
        }
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem) return false;
        return super.canInsert(stack);
    }
    @Override
    public void markDirty() {
        super.markDirty();
        stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getHeldStacks()));
    }
}