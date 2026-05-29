package com.kltyton.mob_battle.items.tool.sword;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FineKnifeItem extends Item {
    public FineKnifeItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (user.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }

        Level world = user.level();
        if (!world.isClientSide &&
                entity instanceof Animal &&
                (entity.getType() == EntityType.PIG || entity.getType() == EntityType.COW || entity.getType() == EntityType.SHEEP)
        ) {
            entity.hurtServer((ServerLevel) world, user.damageSources().playerAttack(user), 5.0f);
            Item meat;
            if (entity.getType() == EntityType.PIG) {
                meat = Items.PORKCHOP;
            } else if (entity.getType() == EntityType.COW) {
                meat = Items.BEEF;
            } else if (entity.getType() == EntityType.SHEEP) {
                meat = Items.MUTTON;
            } else {
                meat = Items.AIR;
            }

            for (int i = 0; i < 3; i++) {
                entity.drop(new ItemStack(meat), true, false);
            }

            world.playSound(null, entity.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0f, 1.0f);
            stack.hurtAndBreak(1, user, hand);
            user.getCooldowns().addCooldown(stack, 20);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }
}
