package com.kltyton.mob_battle.items.tool.backpack;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class BackpackInventory extends SimpleInventory {
    private final ItemStack stack;

    public BackpackInventory(ItemStack stack, int size) {
        super(size);
        this.stack = stack;

        // 从物品组件读取数据
        ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
        if (component != null) {
            component.copyTo(this.getHeldStacks());
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // 当界面关闭或数据变动时，写回物品组件
        stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getHeldStacks()));
    }
}
