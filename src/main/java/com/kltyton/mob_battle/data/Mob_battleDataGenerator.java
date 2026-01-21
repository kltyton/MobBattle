package com.kltyton.mob_battle.data;

import com.kltyton.mob_battle.data.client.lang.ModChineseLangProvider;
import com.kltyton.mob_battle.data.client.lang.ModEnglishLangProvider;
import com.kltyton.mob_battle.data.client.model.ModModelGenerator;
import com.kltyton.mob_battle.data.server.loot.ModLootTableGenerator;
import com.kltyton.mob_battle.data.server.recipe.ModRecipeGenerator;
import com.kltyton.mob_battle.data.server.tag.ModBlockTagGenerator;
import com.kltyton.mob_battle.data.server.tag.ModItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class Mob_battleDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModBlockTagGenerator::new);
        pack.addProvider(ModItemTagGenerator::new);
        pack.addProvider(ModLootTableGenerator::new);
        pack.addProvider(ModEnglishLangProvider::new);
        pack.addProvider(ModChineseLangProvider::new);
        pack.addProvider(ModModelGenerator::new);
        pack.addProvider(ModRecipeGenerator::new);
    }

}
