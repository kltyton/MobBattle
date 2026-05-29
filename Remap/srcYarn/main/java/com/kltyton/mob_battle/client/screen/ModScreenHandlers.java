package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.screen.machine_worktable.MechanicalWorktableScreenHandler;
import com.kltyton.mob_battle.data.BackpackData;
import com.kltyton.mob_battle.items.tool.backpack.BackpackInventory;
import com.kltyton.mob_battle.items.tool.backpack.PagedBackpackScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    // 注册分页背包的 Handler 类型
    public static final ScreenHandlerType<PagedBackpackScreenHandler> PAGED_BACKPACK =
            new ExtendedScreenHandlerType<>(
                    (syncId, playerInventory, data) -> {
                        ItemStack stack = playerInventory.player.getStackInHand(data.hand());
                        BackpackInventory inventory = new BackpackInventory(stack, BackpackInventory.PAGED_TOTAL_SLOTS);
                        return new PagedBackpackScreenHandler(syncId, playerInventory, inventory);
                    },
                    BackpackData.CODEC
            );
    public static final ScreenHandlerType<MechanicalWorktableScreenHandler> MECHANICAL_WORKTABLE =
            new ScreenHandlerType<>(MechanicalWorktableScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static void init() {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Mob_battle.MOD_ID, "paged_backpack"), PAGED_BACKPACK);
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Mob_battle.MOD_ID, "mechanical_worktable"), MECHANICAL_WORKTABLE);
    }
}
