package com.kltyton.mob_battle.items.food;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MagmaLobsterItemMod extends Item implements ModFabricItem {
    public MagmaLobsterItemMod(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            user.setOnFireFor(5);
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

        ItemStack stack = entity.getStack();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof MagmaLobsterItemMod)) return;

        World world = entity.getWorld();
        if (world.isClient) return;

        if (!entity.isTouchingWater()) return;

        ItemStack newStack = new ItemStack(ModItems.OBSIDIAN_LOBSTER, stack.getCount());

        entity.setStack(newStack);

        world.playSound(
                null,
                entity.getBlockPos(),
                SoundEvents.BLOCK_LAVA_EXTINGUISH,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
        );
    }
}
