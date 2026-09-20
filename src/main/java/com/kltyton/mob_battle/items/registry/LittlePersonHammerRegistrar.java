package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.weapon.littlepersonhammer.LittlePersonHammerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Weapon;

/**
 * 小人锤领域注册器。
 *
 * <p>该注册器只写入物品注册表，不接入共享物品门面、物品组、语言或掉落表；主入口
 * 可调用 {@link #init()}，再由自己的集成层把 {@link #LITTLE_PERSON_HAMMER} 接入人锤实体掉落。</p>
 */
public final class LittlePersonHammerRegistrar {
    /** 供主入口和专用 GameTest 使用的稳定公开注册字段。 */
    public static LittlePersonHammerItem LITTLE_PERSON_HAMMER;

    private LittlePersonHammerRegistrar() {
    }

    /** 注册具有原版重锤组件和小人锤专属命中效果的物品。 */
    public static void init() {
        if (LITTLE_PERSON_HAMMER != null) {
            return;
        }

        Identifier id = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_hammer");
        LITTLE_PERSON_HAMMER = RegistrySupport.registerItem(
                "little_person_hammer",
                new LittlePersonHammerItem(new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, id))
                        .rarity(Rarity.EPIC)
                        .durability(500)
                        .component(DataComponents.TOOL, MaceItem.createToolProperties())
                        .repairable(Items.BREEZE_ROD)
                        .attributes(LittlePersonHammerItem.createAttributes())
                        .enchantable(15)
                        .component(DataComponents.WEAPON, new Weapon(1))),
                false,
                false
        );
    }
}
