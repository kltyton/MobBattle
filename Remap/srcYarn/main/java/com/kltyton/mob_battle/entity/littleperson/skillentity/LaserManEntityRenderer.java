package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;

public class LaserManEntityRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends BaseSkillLittlePersonEntityRenderer<LaserManEntity, R> {
    private static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/little_person/laser_man.png");

    public LaserManEntityRenderer(EntityRendererFactory.Context context) {
        super(context, "laser_man", false);
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "laser_sword", TEXTURE) {
            @Override
            protected RenderLayer getRenderType(R renderState, Identifier texture) {
                return RenderLayer.getEntityTranslucentEmissive(texture);
            }
        });
    }
}
