package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.components.ModComponents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackInventory extends SimpleContainer {
    private final ItemStack stack;
    public static final int PAGE_SIZE = 54;
    public static final int MAX_PAGES = 50;
    public static final int PAGED_TOTAL_SLOTS = PAGE_SIZE * MAX_PAGES;

    public BackpackInventory(ItemStack stack, int slotCount) {
        super(slotCount);
        this.stack = stack;
        NonNullList<ItemStack> heldStacks = this.getItems();
        List<ItemStack> storedStacks = stack.get(ModComponents.BACKPACK_CONTENTS);
        if (storedStacks != null) {
            int count = Math.min(storedStacks.size(), heldStacks.size());
            for (int i = 0; i < count; i++) {
                heldStacks.set(i, storedStacks.get(i).copy());
            }
        } else {
            ItemContainerContents component = stack.get(DataComponents.CONTAINER);
            if (component != null) {
                component.copyInto(heldStacks);
            }
        }
    }
    @Override
    public boolean canAddItem(ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem) return false;
        return super.canAddItem(stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof BackpackItem);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        List<ItemStack> storedStacks = new ArrayList<>(this.getItems().size());
        for (ItemStack heldStack : this.getItems()) {
            storedStacks.add(heldStack.copy());
        }
        stack.set(ModComponents.BACKPACK_CONTENTS, List.copyOf(storedStacks));
        stack.remove(DataComponents.CONTAINER);
    }
}
