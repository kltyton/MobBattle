package com.kltyton.mob_battle.items.food;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MagmaLobsterItemMod extends Item implements ModFabricItem {
    public MagmaLobsterItemMod(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, world, user);

        if (!world.isClientSide) {
            user.igniteForSeconds(5);
        }

        return result;
    }
    @Override
    public void itemEntityHook(ItemStack stack, ItemEntity itemEntity) {
        tryTransformInWater(itemEntity);
    }
    /**
     * 在 ItemEntity tick 中调用：
     * 如果岩浆龙虾掉进水里，就变成黑曜石龙虾并播放冷却音效
     */
    public static void tryTransformInWater(ItemEntity entity) {
        if (entity == null || !entity.isAlive()) return;

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof MagmaLobsterItemMod)) return;

        Level world = entity.level();
        if (world.isClientSide) return;

        if (!entity.isInWater()) return;

        ItemStack newStack = new ItemStack(ModItems.OBSIDIAN_LOBSTER, stack.getCount());

        entity.setItem(newStack);

        world.playSound(
                null,
                entity.blockPosition(),
                SoundEvents.LAVA_EXTINGUISH,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }
}
