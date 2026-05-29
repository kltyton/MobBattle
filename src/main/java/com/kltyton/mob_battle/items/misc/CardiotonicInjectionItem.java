package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import java.util.Collections;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CardiotonicInjectionItem extends Item {
    public static final ResourceLocation CARDIOTONIC_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "cardiotonic_modifier");

    public CardiotonicInjectionItem(Properties settings) {
        super(settings);
    }

    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!((IPlayerEntityAccessor)user).isUsingGeckoLib()) {
            ((IPlayerEntityAccessor)user).setUseGeckoLib(true);
            var maxHealth = user.getAttribute(Attributes.MAX_HEALTH);
            var scaleAttr = user.getAttribute(Attributes.SCALE);
            var speedAttr = user.getAttribute(Attributes.MOVEMENT_SPEED);
            var jumpAttr = user.getAttribute(Attributes.JUMP_STRENGTH);
            var block_interaction_range = user.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
            var entity_interaction_range = user.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            var salfAttr = user.getAttribute(Attributes.SAFE_FALL_DISTANCE);
            if (scaleAttr != null) {
                scaleAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                scaleAttr.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
            if (speedAttr != null) {
                speedAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                speedAttr.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        0.16,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (jumpAttr != null) {
                jumpAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                jumpAttr.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        0.75,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (maxHealth != null) {
                maxHealth.removeModifier(CARDIOTONIC_MODIFIER_ID);
                maxHealth.addPermanentModifiers(Collections.singleton(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        21980,
                        AttributeModifier.Operation.ADD_VALUE
                )));
            }
            if (block_interaction_range != null) {
                block_interaction_range.removeModifier(CARDIOTONIC_MODIFIER_ID);
                block_interaction_range.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2.5,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (entity_interaction_range != null) {
                entity_interaction_range.removeModifier(CARDIOTONIC_MODIFIER_ID);
                entity_interaction_range.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2.5,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (salfAttr != null) {
                salfAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                salfAttr.addPermanentModifier(new AttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        6,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
            user.addEffect(new MobEffectInstance(ModEffects.SUPER_REGENERATION_ENTRY, 240, 255, false, false));
            if (!user.isCreative()) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(world, user, hand);
    }
}
