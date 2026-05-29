package com.kltyton.mob_battle.items.tool.backpack;

import com.kltyton.mob_battle.data.BackpackData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackItem extends Item {
    private final boolean isBigBackpack;

    public BackpackItem(Properties settings, boolean isBigBackpack) {
        super(settings);
        this.isBigBackpack = isBigBackpack;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!world.isClientSide) {
            if (!isBigBackpack) {
                user.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return stack.getHoverName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                        return new HopperMenu(syncId, playerInventory, new BackpackInventory(stack, 5)) {
                            @Override
                            public ItemStack quickMoveStack(Player player, int slot) {
                                Slot slot2 = this.slots.get(slot);
                                if (slot2.hasItem()) {
                                    ItemStack itemStack = slot2.getItem();
                                    if (itemStack.getItem() instanceof BackpackItem) return ItemStack.EMPTY;
                                }

                                return super.quickMoveStack(player, slot);
                            }
                            @Override
                            protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
                                 if (stack.getItem() instanceof BackpackItem) return false;
                                 return super.moveItemStackTo(stack, startIndex, endIndex, fromLast);
                            }
                            @Override
                            public void clicked(int slotId, int hotbarSlot, ClickType actionType, Player player) {
                                if (slotId >= 0 && slotId < this.slots.size()) {
                                    Slot slot = this.slots.get(slotId);

                                    if (actionType == ClickType.PICKUP || actionType == ClickType.PICKUP_ALL) {
                                        ItemStack cursorStack = this.getCarried();
                                        if (cursorStack.getItem() instanceof BackpackItem) {
                                            return;
                                        }

                                        if (slot.hasItem() && slot.getItem().getItem() instanceof BackpackItem) {
                                            return;
                                        }
                                    } else if (actionType == ClickType.SWAP) {
                                        if (hotbarSlot >= 0 && hotbarSlot < 9) {
                                            ItemStack hotbarStack = player.getInventory().getItem(hotbarSlot);
                                            if (hotbarStack.getItem() instanceof BackpackItem) {
                                                return;
                                            }

                                            if (slot.hasItem() && slot.getItem().getItem() instanceof BackpackItem) {
                                                return;
                                            }
                                        }
                                    }
                                }
                                super.clicked(slotId, hotbarSlot, actionType, player);
                            }
                        };
                    }

                });
            } else {
                user.openMenu(new ExtendedScreenHandlerFactory<BackpackData>() {
                    @Override
                    public Component getDisplayName() {
                        return stack.getHoverName();
                    }

                    @Override
                    public PagedBackpackScreenHandler createMenu(int syncId, Inventory playerInventory, Player player) {
                        BackpackInventory inventory = new BackpackInventory(stack, BackpackInventory.PAGED_TOTAL_SLOTS);
                        return new PagedBackpackScreenHandler(syncId, playerInventory, inventory) {
                            @Override
                            public ItemStack quickMoveStack(Player player, int slot) {
                                Slot slot2 = this.slots.get(slot);
                                if (slot2.hasItem()) {
                                    ItemStack itemStack = slot2.getItem();
                                    if (itemStack.getItem() instanceof BackpackItem) return ItemStack.EMPTY;
                                }

                                return super.quickMoveStack(player, slot);
                            }
                            @Override
                            protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
                                if (stack.getItem() instanceof BackpackItem) return false;
                                return super.moveItemStackTo(stack, startIndex, endIndex, fromLast);
                            }
                            @Override
                            public void clicked(int slotId, int hotbarSlot, ClickType actionType, Player player) {
                                if (slotId >= 0 && slotId < this.slots.size()) {
                                    Slot slot = this.slots.get(slotId);

                                    if (actionType == ClickType.PICKUP || actionType == ClickType.PICKUP_ALL) {
                                        ItemStack cursorStack = this.getCarried();
                                        if (cursorStack.getItem() instanceof BackpackItem) {
                                            return;
                                        }

                                        if (slot.hasItem() && slot.getItem().getItem() instanceof BackpackItem) {
                                            return;
                                        }
                                    } else if (actionType == ClickType.SWAP) {
                                        if (hotbarSlot >= 0 && hotbarSlot < 9) {
                                            ItemStack hotbarStack = player.getInventory().getItem(hotbarSlot);
                                            if (hotbarStack.getItem() instanceof BackpackItem) {
                                                return;
                                            }

                                            if (slot.hasItem() && slot.getItem().getItem() instanceof BackpackItem) {
                                                return;
                                            }
                                        }
                                    }
                                }
                                super.clicked(slotId, hotbarSlot, actionType, player);
                            }
                        };
                    }

                    @Override
                    public BackpackData getScreenOpeningData(ServerPlayer player) {
                        return new BackpackData(hand.ordinal());
                    }
                });
            }
        }
        return InteractionResult.SUCCESS;
    }
}
