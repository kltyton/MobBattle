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
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
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
                        return new HopperScreenHandler(syncId, playerInventory, new BackpackInventory(stack, 5)) {
                            @Override
                            public ItemStack quickMove(PlayerEntity player, int slot) {
                                Slot slot2 = this.slots.get(slot);
                                if (slot2.hasStack()) {
                                    ItemStack itemStack = slot2.getStack();
                                    if (itemStack.getItem() instanceof BackpackItem) return ItemStack.EMPTY;
                                }

                                return super.quickMove(player, slot);
                            }
                            @Override
                            protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
                                 if (stack.getItem() instanceof BackpackItem) return false;
                                 return super.insertItem(stack, startIndex, endIndex, fromLast);
                            }
                            @Override
                            public void onSlotClick(int slotId, int hotbarSlot, SlotActionType actionType, PlayerEntity player) {
                                if (slotId >= 0 && slotId < this.slots.size()) {
                                    Slot slot = this.slots.get(slotId);

                                    if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.PICKUP_ALL) {
                                        ItemStack cursorStack = this.getCursorStack();
                                        if (cursorStack.getItem() instanceof BackpackItem) {
                                            return;
                                        }

                                        if (slot.hasStack() && slot.getStack().getItem() instanceof BackpackItem) {
                                            return;
                                        }
                                    } else if (actionType == SlotActionType.SWAP) {
                                        if (hotbarSlot >= 0 && hotbarSlot < 9) {
                                            ItemStack hotbarStack = player.getInventory().getStack(hotbarSlot);
                                            if (hotbarStack.getItem() instanceof BackpackItem) {
                                                return;
                                            }

                                            if (slot.hasStack() && slot.getStack().getItem() instanceof BackpackItem) {
                                                return;
                                            }
                                        }
                                    }
                                }
                                super.onSlotClick(slotId, hotbarSlot, actionType, player);
                            }
                        };
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
                        return new PagedBackpackScreenHandler(syncId, playerInventory, inventory) {
                            @Override
                            public ItemStack quickMove(PlayerEntity player, int slot) {
                                Slot slot2 = this.slots.get(slot);
                                if (slot2.hasStack()) {
                                    ItemStack itemStack = slot2.getStack();
                                    if (itemStack.getItem() instanceof BackpackItem) return ItemStack.EMPTY;
                                }

                                return super.quickMove(player, slot);
                            }
                            @Override
                            protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
                                if (stack.getItem() instanceof BackpackItem) return false;
                                return super.insertItem(stack, startIndex, endIndex, fromLast);
                            }
                            @Override
                            public void onSlotClick(int slotId, int hotbarSlot, SlotActionType actionType, PlayerEntity player) {
                                if (slotId >= 0 && slotId < this.slots.size()) {
                                    Slot slot = this.slots.get(slotId);

                                    if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.PICKUP_ALL) {
                                        ItemStack cursorStack = this.getCursorStack();
                                        if (cursorStack.getItem() instanceof BackpackItem) {
                                            return;
                                        }

                                        if (slot.hasStack() && slot.getStack().getItem() instanceof BackpackItem) {
                                            return;
                                        }
                                    } else if (actionType == SlotActionType.SWAP) {
                                        if (hotbarSlot >= 0 && hotbarSlot < 9) {
                                            ItemStack hotbarStack = player.getInventory().getStack(hotbarSlot);
                                            if (hotbarStack.getItem() instanceof BackpackItem) {
                                                return;
                                            }

                                            if (slot.hasStack() && slot.getStack().getItem() instanceof BackpackItem) {
                                                return;
                                            }
                                        }
                                    }
                                }
                                super.onSlotClick(slotId, hotbarSlot, actionType, player);
                            }
                        };
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
