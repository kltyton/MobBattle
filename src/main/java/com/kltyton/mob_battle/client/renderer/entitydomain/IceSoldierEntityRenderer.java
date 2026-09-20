package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.entity.littleperson.icesoldier.IceSoldierEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** 冰兵专用的小人 GeckoLib 渲染器，使用冰兵自己的模型、动画和纹理资源。 */
public final class IceSoldierEntityRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends BaseSkillLittlePersonEntityRenderer<IceSoldierEntity, R> {
    public IceSoldierEntityRenderer(EntityRendererProvider.Context context) {
        super(context, "ice_soldier", false);
    }
}
