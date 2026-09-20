package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.registry.ChuanRenGongEntityTypes;
import com.kltyton.mob_battle.entity.registry.DiamondGiantEntityTypes;
import com.kltyton.mob_battle.entity.registry.RoughWhiteZetsuEntityTypes;
import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.kltyton.mob_battle.entity.general.GeneralEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;

/** 新实体领域的客户端模型层与渲染器注册器；不修改共享 ModEntityRenderer。 */
@Environment(EnvType.CLIENT)
public final class EntityDomainRendererRegistrar {
    private static final ModelLayerLocation SMALL_PROJECTILE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "chuan_ren_gong_small_projectile"), "main");
    private static final ModelLayerLocation LARGE_PROJECTILE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "chuan_ren_gong_large_projectile"), "main");
    private static final Identifier SMALL_TEXTURE = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID, "textures/entity/projectiles/chuan_ren_gong_small.png");
    private static final Identifier LARGE_TEXTURE = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID, "textures/entity/projectiles/chuan_ren_gong_large.png");

    private EntityDomainRendererRegistrar() {
    }

    public static void init() {
        EntityRenderers.register(DiamondGiantEntityTypes.DIAMOND_GIANT, DiamondGiantRenderer::new);
        EntityRenderers.register(DiamondGiantEntityTypes.DIAMOND_GIANT_SPIKE,
                context -> new GeneralEntityRenderer<>(context, "diamond_giant_spike", false,
                        GeneralEntityModel.RenderTypes.CUTOUT));
        ModelLayerRegistry.registerModelLayer(SMALL_PROJECTILE, ChuanRenGongProjectileModel::createSmallLayer);
        ModelLayerRegistry.registerModelLayer(LARGE_PROJECTILE, ChuanRenGongProjectileModel::createLargeLayer);
        EntityRenderers.register(ChuanRenGongEntityTypes.CHUAN_REN_GONG,
                context -> new com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer<>(
                        context, "chuan_ren_gong", false));
        EntityRenderers.register(ChuanRenGongEntityTypes.SMALL_PROJECTILE,
                context -> new ChuanRenGongProjectileRenderer<>(context, SMALL_PROJECTILE, SMALL_TEXTURE));
        EntityRenderers.register(ChuanRenGongEntityTypes.LARGE_PROJECTILE,
                context -> new ChuanRenGongProjectileRenderer<>(context, LARGE_PROJECTILE, LARGE_TEXTURE));
        EntityRenderers.register(RoughWhiteZetsuEntityTypes.ROUGH_WHITE_ZETSU,
                RoughWhiteZetsuRenderer::new);
    }
}
