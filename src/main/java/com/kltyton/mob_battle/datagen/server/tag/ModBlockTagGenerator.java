package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // 手写 minecraft:tags/block/mineable/axe.json 的等价生成(scarecrow/target 为木质结构)。
        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SCARECROW_BLOCK, ModBlocks.TARGET_BLOCK);
        // 手写 mob_battle:tags/block/sculk_blocks.json 的等价生成。
        valueLookupBuilder(ModTags.SCULK_BLOCKS)
                .add(Blocks.SCULK, Blocks.SCULK_VEIN, Blocks.SCULK_CATALYST, Blocks.SCULK_SENSOR, Blocks.SCULK_SHRIEKER);
        // 将 NEST_BLOCK 添加到铲子挖掘标签中
        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.NEST_BLOCK);
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
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
