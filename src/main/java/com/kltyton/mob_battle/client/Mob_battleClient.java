package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntityRenderer;
import com.kltyton.mob_battle.entity.customfireball.render.CustomSuperBigFireballEntityRenderer;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntityRenderer;
import com.kltyton.mob_battle.entity.firewall.FireWallEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntityRenderer;
import com.kltyton.mob_battle.entity.sugarmanscorpion.SugarManScorpionRenderer;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillagerRenderer;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillagerRenderer;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingRender;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntityRenderer;
import com.kltyton.mob_battle.event.ModClientEvents;
import com.kltyton.mob_battle.network.ClientPlayNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class Mob_battleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetwork.init();
        ModClientEvents.clientInit();
        EntityRendererRegistry.register(ModEntities.WARRIOR_VILLAGER, WarriorVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARCHER_VILLAGER, ArcherVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.XUN_SHENG, XunShengEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.DEEP_CREATURE, DeepCreatureEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.WITHER_SKELETON_KING, WitherSkeletonKingRender::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_BABY, HighbirdBabyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_EGG, HighbirdEggEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_TEENAGE, HighbirdTeenageEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_ADULTHOOD, HighbirdAdulthoodEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BIG_CUSTOM_FIREBALL, CustomSuperBigFireballEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRE_WALL, FireWallEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLUE_IRON_GOLEM, BlueIronGolemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUGAR_MAN_SCORPION, SugarManScorpionRenderer::new);
    }
}
