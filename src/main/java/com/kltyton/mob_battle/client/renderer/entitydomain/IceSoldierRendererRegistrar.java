package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.entity.registry.IceSoldierEntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/** 冰兵客户端渲染器独立装配入口。 */
public final class IceSoldierRendererRegistrar {
    private IceSoldierRendererRegistrar() {
    }

    public static void init() {
        EntityRendererRegistry.register(IceSoldierEntityTypes.ICE_SOLDIER, IceSoldierEntityRenderer::new);
    }
}
