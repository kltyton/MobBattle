package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;

public class LaserManEntityRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends BaseSkillLittlePersonEntityRenderer<LaserManEntity, R> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/laser_man.png");

    public LaserManEntityRenderer(EntityRendererProvider.Context context) {
        super(context, "laser_man", false);
        withRenderLayer(new CustomBoneTextureGeoLayer<>(this, "laser_sword", TEXTURE) {
            @Override
            protected RenderType getRenderType(R renderState, Identifier texture) {
                return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(texture);
            }
        });
    }
}
