package com.kltyton.mob_battle.tags;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Block> SCULK_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Mob_battle.MOD_ID, "sculk_blocks"));
    public static final TagKey<EntityType<?>> SILENCE_PHANTOM_CANNOT_ATTACK = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID, "silence_phantom_cannot_attack"));
    public static final TagKey<EntityType<?>> ATTACK_HEAL_ENTITY = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID, "attack_heal_entity"));
    public static final TagKey<Item> IRON_GOLD_REPAIRABLE =  TagKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_repairable"));
}
