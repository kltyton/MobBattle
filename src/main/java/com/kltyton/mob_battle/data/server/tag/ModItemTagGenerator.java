package com.kltyton.mob_battle.data.server.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
/*       valueLookupBuilder(ItemTags.WOODEN_SLABS)
                .add(Items.SLIME_BALL)
                .add(Items.ROTTEN_FLESH)
                .addOptionalTag(ItemTags.DIRT)
                .add(Items.OAK_PLANKS)
                .forceAddTag(ItemTags.BANNERS)
                .setReplace(true);*/
    }
}
