package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.ScarecrowBlock;
import com.kltyton.mob_battle.block.doubleblock.TargetBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static ScarecrowBlock SCARECROW_BLOCK;
    public static TargetBlock TARGET_BLOCK;
    public static void init() {
        SCARECROW_BLOCK = register("scarecrow", ScarecrowBlock::new,
                AbstractBlock.Settings.create()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .nonOpaque()
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY),
                true
        );
        TARGET_BLOCK = register("target", TargetBlock::new,
                AbstractBlock.Settings.create()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .nonOpaque()
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY),
                true
        );
    }

    public static <T extends Block> T register(
            String name,
            Function<AbstractBlock.Settings, T> factory,
            AbstractBlock.Settings settings,
            boolean shouldRegisterItem
    ) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        T block = factory.apply(settings.registryKey(blockKey));
        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }
        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Mob_battle.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, name));
    }
}
