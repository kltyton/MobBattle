package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // 将 NEST_BLOCK 添加到铲子挖掘标签中
        valueLookupBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.NEST_BLOCK);
        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(
                        ModBlocks.MACHINE_WORKTABLE_BLOCK,
                        ModBlocks.COMPRESSED_IRON_BLOCK,
                        ModBlocks.COMPRESSED_GOLD_BLOCK,
                        ModBlocks.COMPRESSED_DIAMOND_BLOCK,
                        ModBlocks.COMPRESSED_NETHERITE_BLOCK
                );
        valueLookupBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        ModBlocks.COMPRESSED_IRON_BLOCK,
                        ModBlocks.COMPRESSED_GOLD_BLOCK,
                        ModBlocks.COMPRESSED_DIAMOND_BLOCK,
                        ModBlocks.COMPRESSED_NETHERITE_BLOCK
                );
        // 等级限制（如铁铲以上才能挖)
        // valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.NEST_BLOCK);
    }
}
