package com.kltyton.mob_battle.mixin.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final protected EntityRenderDispatcher dispatcher;
    @Unique
    private static final float BAR_WIDTH = 40.0F;
    @Unique
    private static final float BAR_HEIGHT = 6.0F;
    @Unique
    private static final int BACKGROUND_COLOR = 0x80000000; // 半透明黑色
    @Unique
    private static final int HEALTH_COLOR = 0xFF00FF00;     // 绿色
    @Unique
    private static final float TEXT_SCALE = 0.02F;  // 文本缩放比例
    @Unique
    private static final int TEXT_COLOR = -1; // 白色文本
    @Unique
    private LivingEntity targetEntity;

    @Inject(
            method = "updateRenderState",
            at = @At("HEAD")
    )
    private void onUpdateRenderState(Entity livingEntity, EntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        if (livingEntity instanceof LivingEntity) {
            this.targetEntity = (LivingEntity) livingEntity;
        }
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void renderHealthBar(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        // 仅创造模式可见检查
        if (player == null || !(player.isCreative() || player.isSpectator()) || targetEntity == player) return;
        if (targetEntity == null) return;
        if (targetEntity.isInvisibleTo(player)) return;

        // 计算血条位置（基于实体碰撞箱）
        float yOffset = targetEntity.getHeight() + 0.65F;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        matrices.push();
        // 将血条定位到实体头顶
        matrices.translate(0, yOffset, 0);
        // 保持血条始终面向摄像机
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        // 缩放控制血条大小
        matrices.scale(-0.025F, -0.025F, 0.025F);
        // 计算生命值比例
        float healthRatio = targetEntity.getHealth() / targetEntity.getMaxHealth();
        float filledWidth = BAR_WIDTH * healthRatio;

        // 绘制背景条
        drawRectangle(matrices, vertexConsumers,
                -BAR_WIDTH/2, -BAR_HEIGHT/2, 0.02f,
                BAR_WIDTH, BAR_HEIGHT,
                BACKGROUND_COLOR);

        // 绘制当前血量条
        drawRectangle(matrices, vertexConsumers,
                -BAR_WIDTH/2, -BAR_HEIGHT/2, 0.01f,
                filledWidth, BAR_HEIGHT,
                HEALTH_COLOR);
        matrices.pop();
        renderHealthText(matrices, vertexConsumers, light);
    }
    // @Unique 注解表示这个方法是唯一重写的，确保代码不会与基类或其他类的方法冲突
/*    @Unique
    private void renderHealthText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        TextRenderer textRenderer = this.getTextRenderer();
        // 生成健康值文本
        String healthText = String.format("%.1f", targetEntity.getHealth());
        // 计算文本宽度
        int textWidth = textRenderer.getWidth(healthText);
        float yOffset = targetEntity.getHeight() + 0.65F;
        // 计算文本位置（血条中央偏上）
        matrices.push();
        matrices.translate(0, yOffset, 0);
        // 应用渲染器的旋转矩阵，使文本面向玩家
        matrices.multiply(this.dispatcher.getRotation());

        // 调整文本缩放（抵消血条整体缩放）
        matrices.scale(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
        matrices.scale(-1, -1, 1); // 反转Y轴方向

        // 计算居中位置
        float x = -textWidth / 2F;
        float y = -4F; // 微调垂直位置

        // 绘制文本
        textRenderer.draw(
                healthText,
                x,
                y,
                TEXT_COLOR,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }*/
    @Unique
    protected void renderHealthText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        String healthText = String.format("%.1f", targetEntity.getHealth());
        float yOffset = targetEntity.getHeight() + 0.65F;
        matrices.push();
        matrices.translate(0, yOffset, 0);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = this.getTextRenderer();
        float f = -textRenderer.getWidth(healthText) / 2.0F;
        float y = -4F; // 微调垂直位置
        textRenderer.draw(
                healthText,
                f,
                y,
                TEXT_COLOR,
                false,
                matrix4f,
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    @Unique
    private void drawRectangle(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                               float x, float y, float z,float width, float height, int color) {
        MatrixStack.Entry entry = matrices.peek();
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());

        // 构建四边形顶点数据
        buffer.vertex(entry.getPositionMatrix(), x, y + height, z)
                .color(red, green, blue, alpha);
        buffer.vertex(entry.getPositionMatrix(), x + width, y + height, z)
                .color(red, green, blue, alpha);
        buffer.vertex(entry.getPositionMatrix(), x + width, y, z)
                .color(red, green, blue, alpha);
        buffer.vertex(entry.getPositionMatrix(), x, y, z)
                .color(red, green, blue, alpha);
    }
}
