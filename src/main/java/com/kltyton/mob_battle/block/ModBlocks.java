package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.scarecrow.ScarecrowBlock;
import com.kltyton.mob_battle.block.doubleblock.target.TargetBlock;
import com.kltyton.mob_battle.block.mushroom.MushroomBlock;
import com.kltyton.mob_battle.block.nest.NestBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static ScarecrowBlock SCARECROW_BLOCK;
    public static TargetBlock TARGET_BLOCK;
    public static NestBlock NEST_BLOCK;
    public static MushroomBlock MUSHROOM_BLOCK;
    public static void init() {
        SCARECROW_BLOCK = register(
                "scarecrow",
                ScarecrowBlock::new,
                AbstractBlock.Settings.create()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .nonOpaque()
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY),
                true
        );
        TARGET_BLOCK = register(
                "target",
                TargetBlock::new,
                AbstractBlock.Settings.create()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .nonOpaque()
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY),
                true
        );
        NEST_BLOCK = register(
                "nest",
                NestBlock::new,
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.PALE_YELLOW)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.6F)
                        .nonOpaque()
                        .sounds(BlockSoundGroup.GRASS)
                        .requiresTool(),
                true
        );
        MUSHROOM_BLOCK = register(
                "mushroom",
                MushroomBlock::new,
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.PALE_YELLOW)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.6F)
                        .nonOpaque()
                        .sounds(BlockSoundGroup.GRASS),
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

        T blockRegistered = Registry.register(Registries.BLOCK, blockKey, block);
        BLOCKS.put(name, blockRegistered);

        return blockRegistered;
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Mob_battle.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, name));
    }
}
