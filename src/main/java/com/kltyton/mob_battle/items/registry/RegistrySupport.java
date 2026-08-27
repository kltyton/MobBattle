package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModMaterial;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * 物品注册共享支持层。
 *
 * <p>持有全模组唯一的 ITEMS / GENERATED_ITEMS / SPAWN_EGG_ITEMS 三个映射实例。
 * {@link com.kltyton.mob_battle.items.ModItems} 的对应 public 字段直接别名到这三个实例,
 * 因此所有外部消费方(ModItemGroups、ModModelGenerator、语言数据生成器)看到的映射内容与
 * 重构前完全一致。</p>
 *
 * <p>加载顺序契约(与重构前 ModItems.init() 保持一致):</p>
 * <ol>
 *   <li>调用方必须先执行 {@code BaseMaterialItems.init()}(11 个基础物品最先注册);</li>
 *   <li>各领域注册器按原始源码语句顺序依次调用(由 ModItems.init() 编排);</li>
 *   <li>所有显式刷怪蛋注册(会触发 ModEntities 类初始化)必须先于动态刷怪蛋遍历;</li>
 *   <li>动态刷怪蛋遍历必须作为整个物品初始化序列的最后一步。</li>
 * </ol>
 *
 * <p>registerItem 系列重载保持原始签名与语义:registerGroup 控制是否写入 ITEMS,
 * isGenerated 控制是否写入 GENERATED_ITEMS;注册器与兼容门面共用本类实现,
 * 保证注册顺序、映射填充时机与注册 ID 逐字不变。</p>
 */
public final class RegistrySupport {

    private RegistrySupport() {
    }

    /** 注册到创造模式物品栏的物品(registerGroup=true 时写入)。 */
    public static final Map<String, Item> ITEMS = new HashMap<>();
    /** 全部刷怪蛋物品,key 为物品注册 ID(与实体注册 ID 一致)。 */
    public static final Map<String, SpawnEggItem> SPAWN_EGG_ITEMS = new HashMap<>();
    /** 需要数据生成模型文件的物品(isGenerated=true 时写入)。 */
    public static final Map<String, Item> GENERATED_ITEMS = new HashMap<>();

    /** 构造带注册 ID 的基础 Item.Properties(与重构前实现逐字一致)。 */
    public static Item.Properties registryBaseItemSettings(String id) {
        Identifier itemId = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, itemId));
    }

    /**
     * 注册压缩套装护甲(与重构前 ModItems.registerCompressedArmor 逐字一致)。
     * 仅压缩材料领域使用,保留包级可见性。
     */
    static Item registerCompressedArmor(String id, ArmorMaterial material, ArmorType type, int durability, double maxHealth, double extraToughness) {
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(type.getSlot());
        ItemAttributeModifiers attributes = material.createAttributes(type)
                .withModifierAdded(
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "health_" + id), maxHealth, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
        if (material == ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE) {
            attributes = attributes.withModifierAdded(
                    Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "range_" + id), 0.1, AttributeModifier.Operation.ADD_VALUE),
                    slot
            );
        }
        if (extraToughness > 0.0) {
            attributes = attributes.withModifierAdded(
                    Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), extraToughness, AttributeModifier.Operation.ADD_VALUE),
                    slot
            );
        }
        return registerItem(
                id,
                new com.kltyton.mob_battle.items.armor.ModBaseArmorItem(
                        registryBaseItemSettings(id)
                        .humanoidArmor(material, type)
                        .attributes(attributes)
                        .durability(durability)
                        .stacksTo(1),
                        material,
                        false
                )
        );
    }

    /** 构造压缩剑属性(与重构前 ModItems.compressedSwordSettings 逐字一致)。 */
    static Item.Properties compressedSwordSettings(String id, ToolMaterial material, float attackDamage, float attackSpeed) {
        ItemAttributeModifiers attributes = ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.SWEEPING_DAMAGE_RATIO,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "sweeping_" + id), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
        return registryBaseItemSettings(id)
                .sword(material, attackDamage, attackSpeed)
                .attributes(attributes)
                .stacksTo(1);
    }

    /** 压缩剑注册(注册到 ITEMS,不生成模型,与重构前语义一致)。 */
    static Item registerCompressedSword(String id, Item item) {
        return registerItem(id, item, true, false);
    }

    /** 注册普通物品(默认写入 ITEMS 与 GENERATED_ITEMS)。 */
    public static Item registerItem(String id) {
        return registerItem(id, registryBaseItemSettings(id));
    }

    /** 注册自定义物品实例(默认写入 ITEMS 与 GENERATED_ITEMS)。 */
    public static <T extends Item> T registerItem(String id, T item) {
        return registerItem(id, item, true);
    }

    /** 按 Item.Properties 注册(默认写入 ITEMS 与 GENERATED_ITEMS)。 */
    public static Item registerItem(String id, Item.Properties settings) {
        return registerItem(id, settings, true);
    }

    /** 按 Item.Properties 注册,isGenerated 控制是否写入 GENERATED_ITEMS。 */
    public static Item registerItem(String id, Item.Properties settings, boolean isGenerated) {
        return registerItem(id, settings, true, isGenerated);
    }

    /** 按 Item.Properties 注册,分别控制 ITEMS 与 GENERATED_ITEMS 写入。 */
    public static Item registerItem(String id, Item.Properties settings, boolean registerGroup, boolean isGenerated) {
        return registerItem(id, new Item(settings), registerGroup, isGenerated);
    }

    /** 注册自定义物品实例,isGenerated 控制是否写入 GENERATED_ITEMS。 */
    public static <T extends Item> T registerItem(String id, T item, boolean isGenerated) {
        return registerItem(id, item, true, isGenerated);
    }

    /**
     * 注册自定义物品实例,分别控制 ITEMS 与 GENERATED_ITEMS 写入。
     * 与重构前 ModItems.registerItem 逐字一致。
     */
    public static <T extends Item> T registerItem(String id, T item, boolean registerGroup, boolean isGenerated) {
        Identifier itemId = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        T registered = Registry.register(
                BuiltInRegistries.ITEM,
                itemId,
                item
        );
        if (registerGroup) ITEMS.put(id, registered);
        if (isGenerated) GENERATED_ITEMS.put(id, registered);
        return registered;
    }

    /**
     * 注册刷怪蛋:写入 ITEMS(registerGroup=false)不写入 GENERATED_ITEMS,
     * 并额外写入 SPAWN_EGG_ITEMS(与重构前实现一致)。
     */
    public static SpawnEggItem registerSpawnEggItem(EntityType<? extends Mob> entityType, String id) {
        SpawnEggItem item = registerItem(id, new SpawnEggItem(registryBaseItemSettings(id).spawnEgg(entityType)), false, false);
        SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }
}
