package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.entity.boss.dragon.IEnderDragonEntityRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonRenderer.class)
public abstract class EnderDragonEntityRendererMixin extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
    @Unique
    private static final Identifier MOB_BATTLE_DRAGON_TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
    @Unique
    private static final Identifier MOB_BATTLE_DRAGON_EXPLODING_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
    @Unique
    private static final Identifier MOB_BATTLE_DRAGON_EYES_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
    @Unique
    private static final RenderType MOB_BATTLE_DYING_RENDER_TYPE = RenderTypes.entityCutoutDissolve(MOB_BATTLE_DRAGON_TEXTURE_LOCATION, MOB_BATTLE_DRAGON_EXPLODING_LOCATION);
    @Unique
    private static final RenderType MOB_BATTLE_EYES = RenderTypes.eyes(MOB_BATTLE_DRAGON_EYES_LOCATION);
    @Unique
    private static final Identifier SHADOW_TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/ender_dragon_shadow/ender_dragon_shadow.png");
    @Unique
    private static final RenderType SHADOW_DRAGON_CUTOUT = RenderTypes.entityTranslucentEmissive(SHADOW_TEXTURE);
    @Unique
    private static final RenderType SHADOW_DRAGON_DECAL = RenderTypes.entityTranslucent(SHADOW_TEXTURE);
    @Unique
    private static final float VISUAL_SCALE = 1.5F;

    @Shadow
    @Final
    private EnderDragonModel model;

    protected EnderDragonEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    private static void submitRays(PoseStack poseStack, float deathTime, SubmitNodeCollector submitNodeCollector, RenderType renderType) {
        throw new AssertionError();
    }

    /**
     * @author Kltyton
     * @reason Preserve Mob Battle dragon visual scaling and shadow dragon textures on the 26.1.2 submit pipeline.
     */
    @Overwrite
    public void submit(EnderDragonRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        if (state.deathTime <= 0.0F) {
            poseStack.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);
        }

        float yr = state.getHistoricalPos(7).yRot();
        float rot2 = (float) (state.getHistoricalPos(5).y() - state.getHistoricalPos(10).y());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yr));
        poseStack.mulPose(Axis.XP.rotationDegrees(rot2 * 10.0F));
        poseStack.translate(0.0F, 0.0F, 1.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        int overlayCoords = OverlayTexture.pack(0.0F, state.hasRedOverlay);
        if (state.deathTime > 0.0F) {
            int color = ARGB.white(1.0F - state.deathTime / 200.0F);
            submitNodeCollector.submitModel(
                    this.model, state, poseStack, MOB_BATTLE_DYING_RENDER_TYPE, state.lightCoords, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, null
            );
        } else if (((IEnderDragonEntityRenderStateAccessor) state).isShadow()) {
            submitNodeCollector.submitModel(this.model, state, poseStack, SHADOW_DRAGON_DECAL, state.lightCoords, overlayCoords, state.outlineColor, null);
        } else {
            submitNodeCollector.submitModel(this.model, state, poseStack, MOB_BATTLE_DRAGON_TEXTURE_LOCATION, state.lightCoords, overlayCoords, state.outlineColor, null);
        }

        RenderType eyesRenderType = ((IEnderDragonEntityRenderStateAccessor) state).isShadow() ? SHADOW_DRAGON_CUTOUT : MOB_BATTLE_EYES;
        submitNodeCollector.submitModel(this.model, state, poseStack, eyesRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        if (state.deathTime > 0.0F) {
            float deathTime = state.deathTime / 200.0F;
            poseStack.pushPose();
            poseStack.translate(0.0F, -1.0F, -2.0F);
            submitRays(poseStack, deathTime, submitNodeCollector, RenderTypes.dragonRays());
            submitRays(poseStack, deathTime, submitNodeCollector, RenderTypes.dragonRaysDepth());
            poseStack.popPose();
        }

        poseStack.popPose();
        if (state.beamOffset != null) {
            EnderDragonRenderer.submitCrystalBeams(
                    (float) state.beamOffset.x, (float) state.beamOffset.y, (float) state.beamOffset.z, state.ageInTicks, poseStack, submitNodeCollector, state.lightCoords
            );
        }

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;F)V",
            at = @At("HEAD")
    )
    private void extractRenderState(EnderDragon entity, EnderDragonRenderState state, float tickDelta, CallbackInfo ci) {
        ((IEnderDragonEntityRenderStateAccessor) state).setShadow(((EnderDragonAccessor) entity).isShadow());
    }
}
