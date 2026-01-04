package com.kltyton.mob_battle.tags;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Block> SCULK_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Mob_battle.MOD_ID, "sculk_blocks"));
}
