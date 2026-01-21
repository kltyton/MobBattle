package com.kltyton.mob_battle.data.client.lang;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;

public class ModLangUtils {
    public static String getItemTranslationKey(Item item) {
        return item.getTranslationKey();
    }
    public static String getBlockTranslationKey(Block block) {
        return block.getTranslationKey();
    }
    public static String getEntityTranslationKey(Entity entity) {
        return entity.getType().getTranslationKey();
    }
    public static String getEntityTranslationKey(EntityType<?> entity) {
        return entity.getTranslationKey();
    }
    public static String getEffectTranslationKey(StatusEffect effect) {
        return effect.getTranslationKey();
    }
}
