package com.kltyton.mob_battle.items.tool.irongold;

import com.kltyton.mob_battle.items.FabricItem;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.EnchantmentUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class IronGoldSword extends BaseSword implements FabricItem {
    public IronGoldSword(Settings settings) {
        super(settings.sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 0f,-2.2f).maxCount(1).component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent.TOTEM_OF_UNDYING));
    }
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (EnchantmentUtil.getEnchantmentLevel(world, stack, Enchantments.SWEEPING_EDGE) < 1) {
            EnchantmentUtil.addEnchantment(world, stack, Enchantments.SWEEPING_EDGE, 1);
        }
    }
    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1, false, true, true), attacker);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0, false, true, true), attacker);
    }
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (attacker instanceof PlayerEntity player) {
            stack.damage(5000, player);
        }
    }
    @Override
    public void onLeftClickStart(PlayerEntity player, ItemStack stack, boolean isServer) {
    }

    @Override
    public void onLeftClickStop(PlayerEntity player, ItemStack stack, boolean isServer) {
    }
}
