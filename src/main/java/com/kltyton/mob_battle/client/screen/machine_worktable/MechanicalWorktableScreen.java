package com.kltyton.mob_battle.client.screen.machine_worktable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MechanicalWorktableScreen extends RecipeBookScreen<MechanicalWorktableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");

    public MechanicalWorktableScreen(MechanicalWorktableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, new MechanicalCraftingRecipeBookWidget(handler), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 29;
    }

    @Override
    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 5, this.height / 2 - 49);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }
}
