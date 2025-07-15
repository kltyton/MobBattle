package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.client.render.WarriorVillagerRenderer;
import com.kltyton.mob_battle.client.render.ArcherVillagerRenderer;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class Mob_battleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.WARRIOR_VILLAGER, WarriorVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARCHER_VILLAGER, ArcherVillagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.XUN_SHENG, XunShengEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIGHBIRD_BABY, HighbirdBabyEntityRenderer::new);
    }
}
