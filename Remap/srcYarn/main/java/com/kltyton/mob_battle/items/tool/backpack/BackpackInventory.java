package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.components.ModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class BackpackInventory extends SimpleInventory {
    private final ItemStack stack;
    public static final int PAGE_SIZE = 54;
    public static final int MAX_PAGES = 50;
    public static final int PAGED_TOTAL_SLOTS = PAGE_SIZE * MAX_PAGES;

    public BackpackInventory(ItemStack stack, int slotCount) {
        super(slotCount);
        this.stack = stack;
        DefaultedList<ItemStack> heldStacks = this.getHeldStacks();
        List<ItemStack> storedStacks = stack.get(ModComponents.BACKPACK_CONTENTS);
        if (storedStacks != null) {
            int count = Math.min(storedStacks.size(), heldStacks.size());
            for (int i = 0; i < count; i++) {
                heldStacks.set(i, storedStacks.get(i).copy());
            }
        } else {
            ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
            if (component != null) {
                component.copyTo(heldStacks);
            }
        }
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem) return false;
        return super.canInsert(stack);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof BackpackItem);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        List<ItemStack> storedStacks = new ArrayList<>(this.getHeldStacks().size());
        for (ItemStack heldStack : this.getHeldStacks()) {
            storedStacks.add(heldStack.copy());
        }
        stack.set(ModComponents.BACKPACK_CONTENTS, List.copyOf(storedStacks));
        stack.remove(DataComponentTypes.CONTAINER);
    }
}
