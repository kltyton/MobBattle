package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.incubation.IncubationEggItem;
import com.kltyton.mob_battle.items.scroll.GuardianSealItem;
import com.kltyton.mob_battle.items.scroll.SummonVexBookItem;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.FineKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.IronManMissileLauncherItem;
import com.kltyton.mob_battle.items.tool.sword.PoisonKnifeItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 召唤、封印与近战技能道具注册器。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class CombatItemRegistrar {

    private CombatItemRegistrar() {
    }

    /** 按兼容顺序注册召唤、封印与战斗技能道具。 */
    public static void init() {
        ModItems.INCUBATION_EGG = Registry.register(BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "incubation_egg"),
                new IncubationEggItem(
                        ModEntities.HIGHBIRD_EGG,
                        new Item.Properties()
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "incubation_egg")
                                ))
                )
        );

        ModItems.WARLOCK_BOOK = RegistrySupport.registerItem("warlock_book", new SummonVexBookItem(
                RegistrySupport.registryBaseItemSettings("warlock_book")
                        .useCooldown(15)
                        .durability(150)
                        .stacksTo(1),
                3, 0, 0)
        );

        ModItems.GRAND_SUMMON_BOOK = RegistrySupport.registerItem("grand_summon_book", new SummonVexBookItem(
                RegistrySupport.registryBaseItemSettings("grand_summon_book")
                        .rarity(Rarity.RARE)
                        .useCooldown(20)
                        .durability(150)
                        .stacksTo(1),
                10, 5, 10)
        );

        ModItems.GUARDIAN_SEAL = RegistrySupport.registerItem("guardian_seal", new GuardianSealItem(
                RegistrySupport.registryBaseItemSettings("guardian_seal")
                        .useCooldown(2000)
                        .stacksTo(1),
                false)
        );

        ModItems.FILLING_SEAL = RegistrySupport.registerItem("filling_seal", new GuardianSealItem(
                RegistrySupport.registryBaseItemSettings("filling_seal")
                        .stacksTo(1),
                true)
        );

        ModItems.FINE_KNIFE = RegistrySupport.registerItem("fine_knife", new FineKnifeItem(
                RegistrySupport.registryBaseItemSettings("fine_knife")
                        .durability(200)
                        .sword(ToolMaterial.IRON, 0, 0)
                        .stacksTo(1)),
                false
        );

        ModItems.POISON_KNIFE = RegistrySupport.registerItem("poison_knife", new PoisonKnifeItem(
                        RegistrySupport.registryBaseItemSettings("poison_knife")
                                .durability(2000)
                                .sword(ToolMaterial.IRON, 48.0F, -2.4F)
                                .stacksTo(1)),
                true,
                false
        );

        ModItems.BLOOD_KNIFE = RegistrySupport.registerItem("blood_knife", new BloodKnifeItem(
                        RegistrySupport.registryBaseItemSettings("blood_knife")
                                .durability(3000)
                                .sword(ToolMaterial.IRON, 48.0F, -2.4F)
                                .stacksTo(1)),
                true,
                false
        );

        ModItems.IRON_MAN_MISSILE_LAUNCHER = RegistrySupport.registerItem("iron_man_missile_launcher",
                new IronManMissileLauncherItem(
                        RegistrySupport.registryBaseItemSettings("iron_man_missile_launcher")
                                .durability(10)
                                .sword(ToolMaterial.IRON, 4.0F, -2.4F)
                                .attributes(ItemAttributeModifiers.builder()
                                        .add(
                                                Attributes.ATTACK_DAMAGE,
                                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 4.0F + ToolMaterial.IRON.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .add(
                                                Attributes.ATTACK_SPEED,
                                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4F, AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .add(
                                                Attributes.ENTITY_INTERACTION_RANGE,
                                                new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_man_missile_launcher_entity_range"), 20.0D, AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .build())
                                .stacksTo(1)),
                true,
                false
        );
    }

}
