package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.machine_worktable.MachineWorktableBlock;
import com.kltyton.mob_battle.block.doubleblock.scarecrow.ScarecrowBlock;
import com.kltyton.mob_battle.block.doubleblock.target.TargetBlock;
import com.kltyton.mob_battle.block.mushroom.MushroomBlock;
import com.kltyton.mob_battle.block.nest.NestBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static ScarecrowBlock SCARECROW_BLOCK;
    public static TargetBlock TARGET_BLOCK;
    public static MachineWorktableBlock MACHINE_WORKTABLE_BLOCK;
    public static NestBlock NEST_BLOCK;
    public static MushroomBlock MUSHROOM_BLOCK;
    public static Block COMPRESSED_IRON_BLOCK;
    public static Block COMPRESSED_GOLD_BLOCK;
    public static Block COMPRESSED_DIAMOND_BLOCK;
    public static Block COMPRESSED_NETHERITE_BLOCK;
    public static void init() {
        SCARECROW_BLOCK = register(
                "scarecrow",
                ScarecrowBlock::new,
                BlockBehaviour.Properties.of()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .noOcclusion()
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                true
        );
        TARGET_BLOCK = register(
                "target",
                TargetBlock::new,
                BlockBehaviour.Properties.of()
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(3.0F)
                        .noOcclusion()
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                true
        );
        MACHINE_WORKTABLE_BLOCK = register(
                "machine_worktable",
                MachineWorktableBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .strength(3.5F)
                        .noOcclusion()
                        .sound(SoundType.METAL)
                        .requiresCorrectToolForDrops()
                        .pushReaction(PushReaction.DESTROY),
                true
        );
        NEST_BLOCK = register(
                "nest",
                NestBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.6F)
                        .noOcclusion()
                        .sound(SoundType.GRASS)
                        .requiresCorrectToolForDrops(),
                true
        );
        MUSHROOM_BLOCK = register(
                "mushroom",
                MushroomBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(0.6F)
                        .noOcclusion()
                        .sound(SoundType.GRASS),
                true
        );
        COMPRESSED_IRON_BLOCK = register(
                "compressed_iron_block",
                Block::new,
                compressedBlockSettings(MapColor.METAL, NoteBlockInstrument.IRON_XYLOPHONE),
                true
        );
        COMPRESSED_GOLD_BLOCK = register(
                "compressed_gold_block",
                Block::new,
                compressedBlockSettings(MapColor.GOLD, NoteBlockInstrument.BELL),
                true
        );
        COMPRESSED_DIAMOND_BLOCK = register(
                "compressed_diamond_block",
                Block::new,
                compressedBlockSettings(MapColor.DIAMOND, NoteBlockInstrument.BIT),
                true
        );
        COMPRESSED_NETHERITE_BLOCK = register(
                "compressed_netherite_block",
                Block::new,
                compressedBlockSettings(MapColor.COLOR_BLACK, NoteBlockInstrument.BASEDRUM),
                true
        );
    }

    private static BlockBehaviour.Properties compressedBlockSettings(MapColor mapColor, NoteBlockInstrument instrument) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .instrument(instrument)
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    public static <T extends Block> T register(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            BlockBehaviour.Properties settings,
            boolean shouldRegisterItem
    ) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        T block = factory.apply(settings.setId(blockKey));

        if (shouldRegisterItem) {
            ResourceKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        T blockRegistered = Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        BLOCKS.put(name, blockRegistered);

        return blockRegistered;
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
    }
}
