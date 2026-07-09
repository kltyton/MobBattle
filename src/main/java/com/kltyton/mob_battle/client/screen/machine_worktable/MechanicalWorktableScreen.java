package com.kltyton.mob_battle.client.screen.machine_worktable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class MechanicalWorktableScreen extends AbstractRecipeBookScreen<MechanicalWorktableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");

    public MechanicalWorktableScreen(MechanicalWorktableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, new MechanicalCraftingRecipeBookWidget(handler), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 29;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        super.extractBackground(context, mouseX, mouseY, deltaTicks);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
