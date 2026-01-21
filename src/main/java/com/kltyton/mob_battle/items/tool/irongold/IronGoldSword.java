package com.kltyton.mob_battle.items.tool.irongold;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.FabricItem;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.ArmorUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IronGoldSword extends BaseSword implements FabricItem {
    private static final Identifier BONUS_ID = Identifier.of(Mob_battle.MOD_ID, "iron_gold_full_attack_bonus");
    private static final EntityAttributeModifier BONUS_MODIFIER =
            new EntityAttributeModifier(BONUS_ID, 30, EntityAttributeModifier.Operation.ADD_VALUE);
    public IronGoldSword(Settings settings) {
        super(settings);
    }
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof LivingEntity living) {
            AttributeModifiersComponent current = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            boolean shouldHaveBonus = ArmorUtil.hasFullDiamondArmor(living);
            AttributeModifiersComponent updated;
            if (shouldHaveBonus) {
                updated = current.with(
                        EntityAttributes.ATTACK_DAMAGE,
                        BONUS_MODIFIER,
                        AttributeModifierSlot.MAINHAND
                );
            } else {
                updated = removeBonusById(current);
            }
            if (updated != current) {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, updated);
            }
        }
    }

    private static AttributeModifiersComponent removeBonusById(AttributeModifiersComponent current) {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
        boolean found = false;
        for (AttributeModifiersComponent.Entry entry : current.modifiers()) {
            if (entry.modifier().idMatches(BONUS_ID)) {
                found = true;
                continue;
            }
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }
        return found ? builder.build() : current;  // 如果没找到，直接返回原对象
    }
/*    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (EnchantmentUtil.getEnchantmentLevel(world, stack, Enchantments.SWEEPING_EDGE) < 1) {
            EnchantmentUtil.addEnchantment(world, stack, Enchantments.SWEEPING_EDGE, 1);
        }
    }*/
/*    @Override
    public Text getName(ItemStack stack) {
        Text originalName = super.getName(stack);
        if (stack.getDamage() >= stack.getMaxDamage() - 1) {
            MutableText damagedName = originalName.copy();
            damagedName.append(Text.literal("（已损坏）").styled(style -> style.withColor(0xFF5555)));
            return damagedName;
        }

        // 未损坏时返回原名
        return originalName;
    }*/
    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1, false, true, true), attacker);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0, false, true, true), attacker);
    }
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
/*        if (attacker instanceof PlayerEntity player) {
            stack.damage(5000, player);
        }*/
    }
    @Override
    public void onLeftClickStart(PlayerEntity player, ItemStack stack, boolean isServer) {
    }

    @Override
    public void onLeftClickStop(PlayerEntity player, ItemStack stack, boolean isServer) {
    }
}
