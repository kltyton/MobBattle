package com.kltyton.mob_battle.data.client.lang;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModChineseLangProvider extends FabricLanguageProvider {
    public ModChineseLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_BOOTS), "拜火教徒靴子");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_CHESTPLATE), "拜火教徒胸甲");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_HELMET), "拜火教徒头盔");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.ECREDCULTIST_LEGGINGS), "拜火教徒护腿");
        translationBuilder.add(ModLangUtils.getBlockTranslationKey(ModBlocks.NEST_BLOCK), "巢穴");
        translationBuilder.add(ModLangUtils.getBlockTranslationKey(ModBlocks.MUSHROOM_BLOCK), "蘑菇");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.LOBSTER_MAIN_COURSE), "龙虾正餐");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.COOKED_HIGHBIRD_EGG), "烤高鸟蛋");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SELF_DESTRUCT), "玉碎");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SUPER_SELF_DESTRUCT), "玉石俱碎");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.SUGAR), "糖分");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.ARMOR_PIERCING), "破甲");
        translationBuilder.add(ModLangUtils.getEffectTranslationKey(ModEffects.VOID_ARMOR_PIERCING), "虚无破甲");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.HULKBUSTER), "钢铁巨人傀儡");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.HULKBUSTER_SPAWN_EGG), "钢铁巨人傀儡刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.MISSILE), "导弹");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.SILENCE_PHANTOM), "潜声幻翼");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SILENCE_PHANTOM_SPAWN_EGG), "潜声幻翼刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.COAL_SILVERFISH), "煤蠹虫");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.COAL_SILVERFISH_SPAWN_EGG), "煤蠹虫刷怪蛋");

        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.CYBORG), "生化人");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("cyborg")), "生化人刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.FRENCH_SPHERE_FLOW), "法球流");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("french_sphere_flow")), "法球流刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.IRON_MAN), "铁人");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man")), "铁人刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.IRON_MAN_TRUE), "铁人（二阶段）");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man_true")), "铁人（二阶段刷怪蛋）");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.POISONOUS_SLASH), "毒砍");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_slash")), "毒砍刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.TAI_LIN), "泰林");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("tai_lin")), "泰林刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.WILD_BOAR), "野猪");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_boar")), "野猪刷怪蛋");
        translationBuilder.add(ModLangUtils.getEntityTranslationKey(ModEntities.WILD_MAN), "野人");
        translationBuilder.add(ModLangUtils.getItemTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_man")), "野人刷怪蛋");
    }
}
