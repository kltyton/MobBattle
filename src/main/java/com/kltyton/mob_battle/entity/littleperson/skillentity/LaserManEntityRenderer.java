package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;

public class LaserManEntityRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends BaseSkillLittlePersonEntityRenderer<LaserManEntity, R> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/laser_man.png");

    public LaserManEntityRenderer(EntityRendererProvider.Context context) {
        super(context, "laser_man", false);
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "laser_sword", TEXTURE) {
            @Override
            protected RenderType getRenderType(R renderState, ResourceLocation texture) {
                return RenderType.entityTranslucentEmissive(texture);
            }
        });
    }
}
