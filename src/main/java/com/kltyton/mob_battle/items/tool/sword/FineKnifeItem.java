package com.kltyton.mob_battle.items.tool.sword;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FineKnifeItem extends Item {
    public FineKnifeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();
        if (!world.isClient &&
                entity instanceof AnimalEntity &&
                (entity.getType() == EntityType.PIG || entity.getType() == EntityType.COW || entity.getType() == EntityType.SHEEP)
        ) {
            entity.damage((ServerWorld) world, user.getDamageSources().playerAttack(user), 5.0f);
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
                entity.dropItem(new ItemStack(meat), true, false);
            }

            world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            stack.damage(1, user, hand);
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
