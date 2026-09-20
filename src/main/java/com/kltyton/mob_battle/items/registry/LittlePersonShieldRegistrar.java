package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.shield.LittlePersonShieldItem;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 小人盾牌领域注册器。
 *
 * <p>不依赖共享物品门面；主入口可在内容初始化阶段调用 {@link #init()}，
 * 再按需要把 {@link #LITTLE_PERSON_SHIELD} 接入旧字段或物品组。</p>
 */
public final class LittlePersonShieldRegistrar {
    /** 稳定公开注册字段，供主入口和专用测试使用。 */
    public static LittlePersonShieldItem LITTLE_PERSON_SHIELD;

    private LittlePersonShieldRegistrar() {
    }

    /** 注册小人盾牌并安装服务端自动格挡策略。 */
    public static void init() {
        Identifier id = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_shield");
        LITTLE_PERSON_SHIELD = RegistrySupport.registerItem(
                "little_person_shield",
                new LittlePersonShieldItem(new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, id))
                        .durability(3000)
                        .equippableUnswappable(EquipmentSlot.OFFHAND)
                        .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                                0.25F,
                                1.0F,
                                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                Optional.of(SoundEvents.SHIELD_BLOCK),
                                Optional.of(SoundEvents.SHIELD_BREAK)
                        ))
                        .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
                        .attributes(ItemAttributeModifiers.builder()
                                .add(Attributes.ATTACK_DAMAGE,
                                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 19.0D,
                                                AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND)
                                .add(Attributes.ATTACK_SPEED,
                                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0D,
                                                AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND)
                                .build())
                ),
                false
        );
        LittlePersonShieldItem.initAutomaticBlocking();
    }
}
