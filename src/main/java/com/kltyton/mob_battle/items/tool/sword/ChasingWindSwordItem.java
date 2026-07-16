package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class ChasingWindSwordItem extends Item {
    private static final String MODE_KEY = "ChasingWindSwordMode";
    private static final int COOLDOWN_TICKS = 8 * 20;
    private static final int DURABILITY_COST = 3;

    public enum Mode {
        ICE,
        FIRE
    }

    public ChasingWindSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel world) {
            Mode mode = getMode(stack);
            if (mode == Mode.ICE) {
                ElementalSwordItem.shoot(world, player, ElementalSwordProjectileEntity.WIND_ICE, new ItemStack(Items.ICE), 1.5F);
            } else {
                ElementalSwordItem.shoot(world, player, ElementalSwordProjectileEntity.WIND_MAGMA, new ItemStack(Items.MAGMA_BLOCK), 1.5F);
            }
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(DURABILITY_COST, player, hand);
            }
        }
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    public static Mode getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return "FIRE".equals(tag.getStringOr(MODE_KEY, Mode.ICE.name())) ? Mode.FIRE : Mode.ICE;
    }

    public static Mode toggleMode(ItemStack stack) {
        Mode next = getMode(stack) == Mode.ICE ? Mode.FIRE : Mode.ICE;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putString(MODE_KEY, next.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return next;
    }

    public static Component modeMessage(Mode mode) {
        return Component.literal(mode == Mode.ICE ? "追风剑模式：寒冰" : "追风剑模式：火焰");
    }
}
