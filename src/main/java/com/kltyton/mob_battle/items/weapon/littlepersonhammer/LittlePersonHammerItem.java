package com.kltyton.mob_battle.items.weapon.littlepersonhammer;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 小人锤的物品行为。
 *
 * <p>所有重锤攻击、下落伤害、击退、耐久和方块交互行为均由 {@link MaceItem}
 * 提供；本类只把基础攻击伤害调整为 20，并在原版成功的下落命中回调中追加眩晕 I。</p>
 */
public final class LittlePersonHammerItem extends MaceItem {
    /** 小人锤追加眩晕的持续时间，单位为 tick。 */
    public static final int SMASH_STUN_DURATION_TICKS = 20;

    /** 小人锤的最终基础攻击伤害。 */
    public static final double BASE_ATTACK_DAMAGE = 20.0D;

    /**
     * 创建小人锤属性，保留原版重锤的攻速，仅将玩家基础攻击值调整为 20。
     */
    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, BASE_ATTACK_DAMAGE - 1.0D,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.4F,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    /** 构造小人锤。 */
    public LittlePersonHammerItem(Properties properties) {
        super(properties);
    }

    /**
     * 原版玩家攻击成功后才会进入此回调；只有满足原版重锤下落攻击判定时才追加眩晕。
     */
    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (attacker instanceof ServerPlayer serverPlayer && MaceItem.canSmashAttack(serverPlayer)) {
            target.addEffect(new MobEffectInstance(
                    ModEffects.STUN_ENTRY,
                    SMASH_STUN_DURATION_TICKS,
                    0
            ), serverPlayer);
        }
    }
}
