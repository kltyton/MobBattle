package com.kltyton.mob_battle.datagen.client.lang;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.client.keybinding.ModKeyBinding;
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
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_BOOTS), "拜火教徒靴子");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_CHESTPLATE), "拜火教徒胸甲");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_HELMET), "拜火教徒头盔");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ECREDCULTIST_LEGGINGS), "拜火教徒护腿");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModBlocks.NEST_BLOCK), "巢穴");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModBlocks.MUSHROOM_BLOCK), "蘑菇");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.LOBSTER_MAIN_COURSE), "龙虾正餐");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COOKED_HIGHBIRD_EGG), "烤高鸟蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SELF_DESTRUCT), "玉碎");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SUPER_SELF_DESTRUCT), "玉石俱碎");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.SUGAR), "糖分");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.ARMOR_PIERCING), "破甲");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.VOID_ARMOR_PIERCING), "虚无破甲");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HULKBUSTER), "钢铁巨人傀儡");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.HULKBUSTER_SPAWN_EGG), "钢铁巨人傀儡刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MISSILE), "导弹");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SILENCE_PHANTOM), "潜声幻翼");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SILENCE_PHANTOM_SPAWN_EGG), "潜声幻翼刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.COAL_SILVERFISH), "煤蠹虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COAL_SILVERFISH_SPAWN_EGG), "煤蠹虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LIRUI_SILVERFISH), "利锐虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("ruili_silverfish")), "利锐虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.DRILL_SILVERFISH), "钻虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("drill_silverfish")), "钻虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.POISONOUS_SILVERFISH), "毒爆虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_silverfish")), "毒爆虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LOAD_SILVERFISH), "载虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("load_silverfish")), "载虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LONG_WHIP_SILVERFISH), "长鞭魔虫");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("long_whip_silverfish")), "长鞭魔虫刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.FLOWER_FAIRY), "花妖");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("flower_fairy")), "花妖刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SUPER_EVOKER), "新-唤魔者");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("super_evoker")), "新-唤魔者刷怪蛋");


        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.CYBORG), "生化人");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("cyborg")), "生化人刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.FRENCH_SPHERE_FLOW), "法球流");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("french_sphere_flow")), "法球流刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.IRON_MAN), "铁人");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man")), "铁人刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.IRON_MAN_TRUE), "铁人（二阶段）");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("iron_man_true")), "铁人（二阶段刷怪蛋）");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.POISONOUS_SLASH), "毒砍");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("poisonous_slash")), "毒砍刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.TAI_LIN), "泰林");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("tai_lin")), "泰林刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.WILD_BOAR), "野猪");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_boar")), "野猪刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.WILD_MAN), "野人");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("wild_man")), "野人刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.SEX_ENTITY), "感性平民");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("sex_entity")), "感性平民刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HUMAN_SHIELD), "人盾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("human_shield")), "人盾刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HUMAN_HAMMER), "人锤");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("human_hammer")), "人锤刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LITTLE_PERSON_SOLDIER), "小人士兵");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("little_person_soldier")), "小人士兵刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LITTLE_PERSON_SOLDIER_ARCHER), "小人弓兵");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("little_person_soldier_archer")), "小人弓兵刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.BLOODY_BLADE), "血刃");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("bloody_blade")), "血刃刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MAGIC_MAN), "魔法人");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("magic_man")), "魔法人刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.HEAVEN_CRIPPLED_FEET), "天残脚");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("heaven_crippled_feet")), "天残脚刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("lobster_entity")), "龙虾刷怪蛋");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get("magma_lobster_entity")), "熔岩龙虾刷怪蛋");



        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.TRUE_INVISIBLE), "超级隐形");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.BLOCK), "格挡");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.DISARM), "缴械");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.INFESTATION), "虫扰");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.HEART_EATER), "噬心");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.PROTEIN), "蛋白质");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.SMALL_BACKPACK), "小背包");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.LARGE_BACKPACK), "PM背包");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.FINE_KNIFE), "精良小刀");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.WARLOCK_BOOK), "术士之书");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.GRAND_SUMMON_BOOK), "唤魔大书");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.GUARDIAN_SEAL), "守护法印");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.FILLING_SEAL), "充盈法印");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_HELMET), "翠钻合金头盔");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_CHESTPLATE), "翠钻合金胸甲");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_LEGGINGS), "翠钻合金护腿");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_BOOTS), "翠钻合金靴子");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.EMERALD_DIAMOND_SWORD), "翠钻合金剑");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ZIJIN_SWORD), "紫金剑");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ZIJIN_HELMET), "紫金头盔");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ZIJIN_CHESTPLATE), "紫金胸甲");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ZIJIN_LEGGINGS), "紫金护腿");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ZIJIN_BOOTS), "紫金靴子");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.shieldKey), "召唤盾牌");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerRetreatStepRun), "后撤步");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerAttack2Run), "左拳大摆拳");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerLeftWhipRun), "左鞭腿");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerTopKneeRun), "顶膝随后上勾拳");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerCollisionRun), "冲锋冲撞");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerRunCollisionRun), "起跑向下砸");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerSmashingTheGroundRun), "跳起砸地");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPlayerScrapingRun), "抓取");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.CARDIOTONIC_INJECTION), "强心注射剂");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyZiJin), "激活紫金套装奖励");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_COPPER_INGOT), "压缩铜锭");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_IRON_INGOT), "压缩铁锭");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_GOLD_INGOT), "压缩金锭");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_DIAMOND), "压缩钻石");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_NETHERITE_INGOT), "压缩下界合金锭");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_REDSTONE), "压缩红石");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.COMPRESSED_LAPIS_LAZULI), "压缩青金石");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.LOBSTER), "龙虾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.MAGMA_LOBSTER), "岩浆龙虾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.OBSIDIAN_LOBSTER), "黑曜石龙虾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.BURST_OBSIDIAN_LOBSTER), "爆开的黑曜石龙虾");

        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ICE_BOW), "寒冰弓");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ICE_ARROW_ITEM), "寒冰箭");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEffects.ICE), "冰冻");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.TRAIN_BULLET), "特质子弹");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.AREA_GRAVITY_DEVICE_ITEM), "区域重力装置");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.PIGLIN_CANNON), "猪灵炮");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModKeyBinding.keyPiglinCannonItemMode), "切换猪灵炮模式");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MAGMA_LOBBER_BIG_FIREBALL), "火球");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.MAGMA_LOBSTER), "熔岩龙虾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModEntities.LOBSTER), "龙虾");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.ELECTRONIC_COMPONENTS), "电子元件");
        translationBuilder.add(ModLangUtils.getTranslationKey(ModItems.WIRE), "电线");
    }
}
