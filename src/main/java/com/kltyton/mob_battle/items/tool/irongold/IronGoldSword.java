package com.kltyton.mob_battle.items.tool.irongold;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.ArmorUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

public class IronGoldSword extends BaseSword implements ModFabricItem {
    private static final ResourceLocation BONUS_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_full_attack_bonus");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_sword_armor");
    private static final AttributeModifier BONUS_MODIFIER =
            new AttributeModifier(BONUS_ID, 30, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier ARMOR_MODIFIER =
            new AttributeModifier(ARMOR_ID, 3, AttributeModifier.Operation.ADD_VALUE);
    public IronGoldSword(Properties settings) {
        super(settings);
    }
    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof LivingEntity living) {
            ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            boolean shouldHaveBonus = ArmorUtil.hasFullArmor(living, ModMaterial.IRON_GOLD_INSTANCE);
            ItemAttributeModifiers updated = current.withModifierAdded(
                    Attributes.ARMOR,
                    ARMOR_MODIFIER,
                    EquipmentSlotGroup.MAINHAND
            );
            if (shouldHaveBonus) {
                updated = updated.withModifierAdded(
                        Attributes.ATTACK_DAMAGE,
                        BONUS_MODIFIER,
                        EquipmentSlotGroup.MAINHAND
                );
            } else {
                updated = removeBonusById(updated);
            }
            if (updated != current) {
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, updated);
            }
        }
    }

    private static ItemAttributeModifiers removeBonusById(ItemAttributeModifiers current) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        boolean found = false;
        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
            if (entry.modifier().is(BONUS_ID)) {
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
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, false, true, true), attacker);
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 0, false, true, true), attacker);
    }
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
/*        if (attacker instanceof PlayerEntity player) {
            stack.damage(5000, player);
        }*/
    }
    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
    }

    @Override
    public void onLeftClickStop(Player player, ItemStack stack, boolean isServer) {
    }
}
