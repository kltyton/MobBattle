package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.items.tool.backpack.BackpackInventory;
import com.kltyton.mob_battle.items.tool.backpack.PagedBackpackScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PagedBackpackScreen extends HandledScreen<PagedBackpackScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private final int rows = 6;

    public PagedBackpackScreen(PagedBackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 114 + this.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // 添加翻页按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), (button) -> {
            if (this.client != null) {
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, 0);
                }
            }
        }).dimensions(this.x + 142, this.y + 4, 14, 12).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), (button) -> {
            if (this.client != null) {
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, 1);
                }
            }
        }).dimensions(this.x + 158, this.y + 4, 14, 12).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 创建复合文本：title + " " + page info
        Text pageTitle = Text.literal("").append(this.title).append(" ").append(Text.literal((this.handler.getPage() + 1) + "/" + BackpackInventory.MAX_PAGES));
        context.drawText(this.textRenderer, pageTitle, this.titleX, this.titleY, Colors.DARK_GRAY, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, Colors.DARK_GRAY, false);
    }
    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        // 使用你提供的 RenderPipelines 逻辑
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.rows * 18 + 17, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j + this.rows * 18 + 17, 0.0F, 126.0F, this.backgroundWidth, 96, 256, 256);
    }
}