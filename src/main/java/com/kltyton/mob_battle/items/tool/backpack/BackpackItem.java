package com.kltyton.mob_battle.items.tool.backpack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BackpackItem extends Item {
    private final int rows; // 行数，漏斗为 0 (特殊处理)，大箱子为 6

    public BackpackItem(Settings settings, int rows) {
        super(settings);
        this.rows = rows;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            // 打开界面
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    if (rows == 0) {
                        // 漏斗：使用 SimpleInventory(5)
                        return new HopperScreenHandler(syncId, playerInventory, new BackpackInventory(stack, 5));
                    } else {
                        // 大箱子：使用 GenericContainerScreenHandler.createGeneric9x6
                        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new BackpackInventory(stack, 54), 6);
                    }
                }
            });
        }
        return ActionResult.SUCCESS;
    }
}
