package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.weapon.biochemical.BiochemicalBladeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Weapon;

/** 生化刃领域注册器；不修改共享物品门面。 */
public final class BiochemicalBladeRegistrar {
    /** 稳定公开注册字段，供主入口别名和专用测试使用。 */
    public static BiochemicalBladeItem BIOCHEMICAL_BLADE;

    private BiochemicalBladeRegistrar() {
    }

    /** 注册最大堆叠 40、最终攻击伤害 10、最终攻击速度 4 的生化刃。 */
    public static void init() {
        Identifier id = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "biochemical_blade");
        BIOCHEMICAL_BLADE = RegistrySupport.registerItem(
                "biochemical_blade",
                new BiochemicalBladeItem(new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, id))
                        .stacksTo(40)
                        .component(DataComponents.WEAPON, new Weapon(1))
                        .attributes(ItemAttributeModifiers.builder()
                                .add(Attributes.ATTACK_DAMAGE,
                                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 9.0D,
                                                AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND)
                                .add(Attributes.ATTACK_SPEED,
                                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 0.0D,
                                                AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND)
                                .build())
                ),
                false
        );
    }
}
