package com.kltyton.mob_battle.client.renderer.littleperson.zombie;

import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** 感染族使用独立资源子目录，保留各职业自身的动画控制器与原版实体缩放属性。 */
public final class ZombieLittlePersonRenderer<T extends LittlePersonMilitiaEntity,
        R extends LivingEntityRenderState & GeoRenderState> extends BaseSkillLittlePersonEntityRenderer<T, R> {
    public ZombieLittlePersonRenderer(EntityRendererProvider.Context context, String id) {
        super(context, "zombie/" + id, false);
    }
}
