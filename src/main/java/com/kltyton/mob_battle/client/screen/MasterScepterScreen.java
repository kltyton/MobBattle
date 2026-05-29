package com.kltyton.mob_battle.client.screen;

import com.kltyton.mob_battle.network.packet.MasterScepterPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class MasterScepterScreen extends Screen {

    private EditBox commandField;

    public MasterScepterScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        // 居中输入框，大小适中
        this.commandField = new EditBox(this.font,
                this.width / 2 - 150, this.height / 2 - 10,
                300, 20, Component.literal("输入命令后按回车提交"));
        this.commandField.setMaxLength(64);
        this.commandField.setFocused(true); // 默认获得焦点

        this.addRenderableWidget(this.commandField);
        this.addWidget(this.commandField);
        this.setFocused(commandField);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 回车提交
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String command = this.commandField.getValue().trim();
            if (!command.isEmpty()) {
                ClientPlayNetworking.send(new MasterScepterPayload(command));
                this.commandField.setValue(""); // 清空输入框（可改为 close() 关闭屏幕）
                this.onClose();
            }
            return true;
        }

        // ESC 关闭屏幕
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        // 让输入框正常处理其他按键（删除、输入等）
        return this.commandField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // 渲染输入框
        this.commandField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
}
