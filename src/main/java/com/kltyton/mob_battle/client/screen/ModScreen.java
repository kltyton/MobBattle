package com.kltyton.mob_battle.client.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModScreen {
    public static void init() {
        HandledScreens.register(ModScreenHandlers.PAGED_BACKPACK, PagedBackpackScreen::new);
    }
}
