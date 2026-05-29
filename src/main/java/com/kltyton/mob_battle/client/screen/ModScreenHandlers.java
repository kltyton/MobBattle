package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.screen.machine_worktable.MechanicalWorktableScreenHandler;
import com.kltyton.mob_battle.data.BackpackData;
import com.kltyton.mob_battle.items.tool.backpack.BackpackInventory;
import com.kltyton.mob_battle.items.tool.backpack.PagedBackpackScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ModScreenHandlers {
    // 注册分页背包的 Handler 类型
    public static final MenuType<PagedBackpackScreenHandler> PAGED_BACKPACK =
            new ExtendedScreenHandlerType<>(
                    (syncId, playerInventory, data) -> {
                        ItemStack stack = playerInventory.player.getItemInHand(data.hand());
                        BackpackInventory inventory = new BackpackInventory(stack, BackpackInventory.PAGED_TOTAL_SLOTS);
                        return new PagedBackpackScreenHandler(syncId, playerInventory, inventory);
                    },
                    BackpackData.CODEC
            );
    public static final MenuType<MechanicalWorktableScreenHandler> MECHANICAL_WORKTABLE =
            new MenuType<>(MechanicalWorktableScreenHandler::new, FeatureFlags.VANILLA_SET);

    public static void init() {
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "paged_backpack"), PAGED_BACKPACK);
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mechanical_worktable"), MECHANICAL_WORKTABLE);
    }
}
