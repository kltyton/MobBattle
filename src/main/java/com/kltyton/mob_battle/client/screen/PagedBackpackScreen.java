package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.items.tool.backpack.BackpackInventory;
import com.kltyton.mob_battle.items.tool.backpack.PagedBackpackScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class PagedBackpackScreen extends AbstractContainerScreen<PagedBackpackScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final int rows = 6;

    public PagedBackpackScreen(PagedBackpackScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // 添加翻页按钮
        this.addRenderableWidget(Button.builder(Component.literal("<"), (button) -> {
            if (this.minecraft != null) {
                if (this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                }
            }
        }).bounds(this.leftPos + 142, this.topPos + 4, 14, 12).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), (button) -> {
            if (this.minecraft != null) {
                if (this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                }
            }
        }).bounds(this.leftPos + 158, this.topPos + 4, 14, 12).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderTooltip(context, mouseX, mouseY);
    }
    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        // 创建复合文本：title + " " + page info
        Component pageTitle = Component.literal("").append(this.title).append(" ").append(Component.literal((this.menu.getPage() + 1) + "/" + BackpackInventory.MAX_PAGES));
        context.drawString(this.font, pageTitle, this.titleLabelX, this.titleLabelY, CommonColors.DARK_GRAY, false);
        context.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CommonColors.DARK_GRAY, false);
    }
    @Override
    protected void renderBg(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        // 使用你提供的 RenderPipelines 逻辑
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.rows * 18 + 17, 256, 256);
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j + this.rows * 18 + 17, 0.0F, 126.0F, this.imageWidth, 96, 256, 256);
    }
}