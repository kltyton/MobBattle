package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PurificationScrollItem extends Item {

    public PurificationScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS,
                0.8F, 1.2F);

        if (!world.isClient) {
            removeStatusEffects(user,
                    StatusEffects.MINING_FATIGUE,
                    StatusEffects.BLINDNESS,
                    StatusEffects.DARKNESS,
                    StatusEffects.NAUSEA,
                    StatusEffects.SLOWNESS,
                    ModEffects.STUN_ENTRY,
                    ModEffects.ICE_ENTRY
            );
        }
        //统计
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        if (!user.getAbilities().creativeMode) itemStack.decrement(1);

        return ActionResult.SUCCESS;
    }

    @SafeVarargs
    public static void removeStatusEffects(PlayerEntity user, RegistryEntry<StatusEffect>... effects) {
        for (RegistryEntry<StatusEffect> effect : effects) {
            user.removeStatusEffect(effect);
        }
    }
}
