package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.data.BackpackData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BackpackItem extends Item {
    private final boolean isBigBackpack;

    public BackpackItem(Settings settings, boolean isBigBackpack) {
        super(settings);
        this.isBigBackpack = isBigBackpack;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (!isBigBackpack) {
                user.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return stack.getName();
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        // 漏斗背包：5 格
                        return new HopperScreenHandler(syncId, playerInventory,
                                new BackpackInventory(stack, 5));
                    }

                });
            } else {
                user.openHandledScreen(new ExtendedScreenHandlerFactory<BackpackData>() {
                    @Override
                    public Text getDisplayName() {
                        return stack.getName();
                    }

                    @Override
                    public PagedBackpackScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        BackpackInventory inventory = new BackpackInventory(stack, BackpackInventory.PAGED_TOTAL_SLOTS);
                        return new PagedBackpackScreenHandler(syncId, playerInventory, inventory);
                    }

                    @Override
                    public BackpackData getScreenOpeningData(ServerPlayerEntity player) {
                        return new BackpackData(hand.ordinal());
                    }
                });
            }
        }
        return ActionResult.SUCCESS;
    }
}
