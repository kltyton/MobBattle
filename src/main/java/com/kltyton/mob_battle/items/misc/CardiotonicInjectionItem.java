package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collections;

public class CardiotonicInjectionItem extends Item {
    public static final Identifier CARDIOTONIC_MODIFIER_ID = Identifier.of(Mob_battle.MOD_ID, "cardiotonic_modifier");

    public CardiotonicInjectionItem(Settings settings) {
        super(settings);
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!((IPlayerEntityAccessor)user).isUsingGeckoLib()) {
            ((IPlayerEntityAccessor)user).setUseGeckoLib(true);
            var maxHealth = user.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            var scaleAttr = user.getAttributeInstance(EntityAttributes.SCALE);
            var speedAttr = user.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            var jumpAttr = user.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
            var block_interaction_range = user.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
            var entity_interaction_range = user.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
            var salfAttr = user.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE);
            if (scaleAttr != null) {
                scaleAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                scaleAttr.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
            if (speedAttr != null) {
                speedAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                speedAttr.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        0.16,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (jumpAttr != null) {
                jumpAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                jumpAttr.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        0.75,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (maxHealth != null) {
                maxHealth.removeModifier(CARDIOTONIC_MODIFIER_ID);
                maxHealth.addPersistentModifiers(Collections.singleton(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        21980,
                        EntityAttributeModifier.Operation.ADD_VALUE
                )));
            }
            if (block_interaction_range != null) {
                block_interaction_range.removeModifier(CARDIOTONIC_MODIFIER_ID);
                block_interaction_range.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2.5,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (entity_interaction_range != null) {
                entity_interaction_range.removeModifier(CARDIOTONIC_MODIFIER_ID);
                entity_interaction_range.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        2.5,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
            if (salfAttr != null) {
                salfAttr.removeModifier(CARDIOTONIC_MODIFIER_ID);
                salfAttr.addPersistentModifier(new EntityAttributeModifier(
                        CARDIOTONIC_MODIFIER_ID,
                        6,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
            user.addStatusEffect(new StatusEffectInstance(ModEffects.SUPER_REGENERATION_ENTRY, 240, 255, false, false));
            if (!user.isCreative()) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return super.use(world, user, hand);
    }
}
