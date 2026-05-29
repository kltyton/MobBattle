package com.kltyton.mob_battle.tags;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> SCULK_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "sculk_blocks"));
    public static final TagKey<EntityType<?>> SILENCE_PHANTOM_CANNOT_ATTACK = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "silence_phantom_cannot_attack"));
    public static final TagKey<EntityType<?>> ATTACK_HEAL_ENTITY = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "attack_heal_entity"));
    public static final TagKey<Item> IRON_GOLD_REPAIRABLE =  TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_repairable"));
}
