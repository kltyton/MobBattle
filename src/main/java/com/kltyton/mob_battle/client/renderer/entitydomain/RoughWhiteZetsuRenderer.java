package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.entity.roughwhitezetsu.RoughWhiteZetsuEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;

/** 使用原版玩家宽臂模型和普通人形姿势，保留粗糙白绝纹理。 */
@Environment(EnvType.CLIENT)
public final class RoughWhiteZetsuRenderer extends HumanoidMobRenderer<
        RoughWhiteZetsuEntity, AvatarRenderState, PlayerModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "mob_battle", "textures/entity/rough_white_zetsu.png");

    public RoughWhiteZetsuRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new AvatarRenderState();
    }

    @Override
    public Identifier getTextureLocation(AvatarRenderState state) {
        return TEXTURE;
    }
}
