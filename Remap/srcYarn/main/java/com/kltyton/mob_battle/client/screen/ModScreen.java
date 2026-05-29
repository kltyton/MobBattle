package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.client.screen.machine_worktable.MechanicalWorktableScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModScreen {
    public static void init() {
        HandledScreens.register(ModScreenHandlers.PAGED_BACKPACK, PagedBackpackScreen::new);
        HandledScreens.register(ModScreenHandlers.MECHANICAL_WORKTABLE, MechanicalWorktableScreen::new);
    }
}
