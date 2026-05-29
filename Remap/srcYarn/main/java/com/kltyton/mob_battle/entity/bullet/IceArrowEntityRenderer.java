package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IceArrowEntityRenderer extends ProjectileEntityRenderer<IceArrowEntity, ArrowEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/projectiles/ice_arrow.png");

    public IceArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    @Override
    protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
        return TEXTURE;
    }
    @Override
    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }
    @Override
    public void updateRenderState(IceArrowEntity arrowEntity, ArrowEntityRenderState arrowEntityRenderState, float f) {
        super.updateRenderState(arrowEntity, arrowEntityRenderState, f);
    }
}
