package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.render.CustomSuperBigFireballEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntityRenderer;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillagerRenderer;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillagerRenderer;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class Mob_battleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.WARRIOR_VILLAGER, WarriorVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARCHER_VILLAGER, ArcherVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.XUN_SHENG, XunShengEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_BABY, HighbirdBabyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_EGG, HighbirdEggEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_TEENAGE, HighbirdTeenageEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_ADULTHOOD, HighbirdAdulthoodEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BIG_CUSTOM_FIREBALL, CustomSuperBigFireballEntityRenderer::new);
    }
}
