package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.Comparator;
import java.util.List;

public class WoodenWhistleItem extends Item implements ModFabricItem {
    private static final double RANGE = 4.0D;
    private static final int COOLDOWN_TICKS = 20;

    public WoodenWhistleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                calmOwnedWolves(player);
                actionbar(player, "item.mob_battle.wooden_whistle.message.calm");
            } else {
                setOwnedWolvesSitting(player, false);
                actionbar(player, "item.mob_battle.wooden_whistle.message.follow");
            }
            damageWhistle(stack, player, hand);
            player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (!isServer || player.getCooldowns().isOnCooldown(stack)) {
            return;
        }
        if (player.isShiftKeyDown()) {
            enrageOwnedWolves(player);
            actionbar(player, "item.mob_battle.wooden_whistle.message.attack");
        } else {
            setOwnedWolvesSitting(player, true);
            actionbar(player, "item.mob_battle.wooden_whistle.message.sit");
        }
        damageWhistle(stack, player, InteractionHand.MAIN_HAND);
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.mob_battle.wooden_whistle.tooltip.1"));
        tooltip.accept(Component.translatable("item.mob_battle.wooden_whistle.tooltip.2"));
        tooltip.accept(Component.translatable("item.mob_battle.wooden_whistle.tooltip.3"));
    }

    private static List<Wolf> ownedWolves(Player player) {
        return player.level().getEntitiesOfClass(Wolf.class, player.getBoundingBox().inflate(RANGE),
                wolf -> wolf.isAlive() && wolf.isOwnedBy(player));
    }

    private static void setOwnedWolvesSitting(Player player, boolean sitting) {
        for (Wolf wolf : ownedWolves(player)) {
            wolf.setTarget(null);
            wolf.setOrderedToSit(sitting);
            wolf.level().playSound(null, wolf.blockPosition(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 0.6F, sitting ? 0.8F : 1.2F);
        }
    }

    private static void enrageOwnedWolves(Player player) {
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RANGE),
                living -> living.isAlive() && living != player && !(living instanceof Wolf wolf && wolf.isOwnedBy(player)));
        for (Wolf wolf : ownedWolves(player)) {
            wolf.setOrderedToSit(false);
            targets.stream()
                    .filter(target -> target != wolf)
                    .min(Comparator.comparingDouble(wolf::distanceToSqr))
                    .ifPresent(wolf::setTarget);
            wolf.level().playSound(null, wolf.blockPosition(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 0.8F, 0.8F);
        }
    }

    private static void calmOwnedWolves(Player player) {
        for (Wolf wolf : ownedWolves(player)) {
            wolf.setTarget(null);
            wolf.setOrderedToSit(false);
        }
    }

    private static void damageWhistle(ItemStack stack, Player player, InteractionHand hand) {
        if (player.level() instanceof ServerLevel && !player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, hand);
        }
    }

    private static void actionbar(Player player, String key) {
        Component message = Component.translatable(key);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message, true);
        } else {
            player.sendSystemMessage(message);
        }
    }
}
