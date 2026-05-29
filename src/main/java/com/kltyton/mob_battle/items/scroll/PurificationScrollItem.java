package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PurificationScrollItem extends Item {

    public PurificationScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                0.8F, 1.2F);

        if (!world.isClientSide) {
            removeStatusEffects(user,
                    MobEffects.MINING_FATIGUE,
                    MobEffects.BLINDNESS,
                    MobEffects.DARKNESS,
                    MobEffects.NAUSEA,
                    MobEffects.SLOWNESS,
                    ModEffects.STUN_ENTRY,
                    ModEffects.ICE_ENTRY
            );
        }
        //统计
        user.awardStat(Stats.ITEM_USED.get(this));

        if (!user.getAbilities().instabuild) itemStack.shrink(1);

        return InteractionResult.SUCCESS;
    }

    @SafeVarargs
    public static void removeStatusEffects(Player user, Holder<MobEffect>... effects) {
        for (Holder<MobEffect> effect : effects) {
            user.removeEffect(effect);
        }
    }
}
