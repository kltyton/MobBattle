package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class IceArrowEntityRenderer extends ArrowRenderer<IceArrowEntity, TippableArrowRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/ice_arrow.png");

    public IceArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    protected ResourceLocation getTextureLocation(TippableArrowRenderState arrowEntityRenderState) {
        return TEXTURE;
    }
    @Override
    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
    @Override
    public void extractRenderState(IceArrowEntity arrowEntity, TippableArrowRenderState arrowEntityRenderState, float f) {
        super.extractRenderState(arrowEntity, arrowEntityRenderState, f);
    }
}
