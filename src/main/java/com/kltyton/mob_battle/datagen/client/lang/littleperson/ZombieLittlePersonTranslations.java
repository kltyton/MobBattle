package com.kltyton.mob_battle.datagen.client.lang.littleperson;

import com.kltyton.mob_battle.datagen.client.lang.LanguageProviderSupport;
import com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;

/** 感染族的实体与刷怪蛋共用名称表，防止独立翻译遗漏或错配。 */
public final class ZombieLittlePersonTranslations {
    private static final String[][] NAMES = {
            {"little_person_zombie", "小人僵尸", "Little Person Zombie"},
            {"little_person_claw_zombie", "小人爪牙僵尸", "Clawed Little Person Zombie"},
            {"little_person_shield_zombie", "小人盾牌僵尸", "Shielded Little Person Zombie"},
            {"little_person_sprayer_zombie", "小人喷射僵尸", "Spitting Little Person Zombie"},
            {"little_person_headless_zombie", "无头小人僵尸", "Headless Little Person Zombie"},
            {"little_person_sprayer_zombie_head", "喷射僵尸头", "Spitting Zombie Head"},
            {"zombie_little_person_archer", "小人僵尸民兵弓箭手", "Zombie Little Person Archer"},
            {"zombie_little_person_militia", "小人僵尸民兵战士", "Zombie Little Person Militia"},
            {"zombie_little_person_guard", "小人僵尸护卫", "Zombie Little Person Guard"},
            {"zombie_elite_little_person_guard", "小人僵尸大护卫", "Zombie Elite Little Person Guard"},
            {"zombie_little_person_king", "小人僵尸皇族", "Zombie Little Person King"},
            {"zombie_little_person_giant", "小人巨人僵尸", "Zombie Little Person Giant"}
    };

    private ZombieLittlePersonTranslations() { }

    public static void add(TranslationBuilder translations, boolean chinese) {
        var types = LittlePersonZombieEntityTypes.types();
        for (String[] entry : NAMES) {
            String name = entry[chinese ? 1 : 2];
            translations.add(types.get(entry[0]), name);
            translations.add(LanguageProviderSupport.getTranslationKey(ModItems.SPAWN_EGG_ITEMS.get(entry[0])),
                    name + (chinese ? "刷怪蛋" : " Spawn Egg"));
        }
    }
}
