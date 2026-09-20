package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.accessor.IPiglinBruteSpearMode;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ai.PiglinBruteWeaponData;
import com.kltyton.mob_battle.entity.ai.ZombieBowData;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import org.jetbrains.annotations.Nullable;

/**
 * 刷怪蛋注册器，集中处理显式实体蛋、携带实体数据的原版实体蛋与动态实体蛋。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class SpawnEggItemRegistrar {

    private SpawnEggItemRegistrar() {
    }

    /**
     * 注册需要稳定历史顺序的显式刷怪蛋。
     *
     * <p>该阶段会触发 {@link ModEntities} 初始化，必须位于动态刷怪蛋阶段之前。</p>
     */
    public static void initExplicit() {
        ModItems.SKULL_KING_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SKULL_KING, "skull_king_spawn_egg");
        ModItems.SKULL_ARCHER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SKULL_ARCHER, "skull_archer_spawn_egg");
        ModItems.SKULL_WARRIOR_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SKULL_WARRIOR, "skull_warrior_spawn_egg");
        ModItems.SKULL_MAGE_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SKULL_MAGE, "skull_mage_spawn_egg");
        ModItems.VOID_CELL_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.VOID_CELL, "void_cell_spawn_egg");
        ModItems.YOUNG_MIN_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.YOUNG_MIN, "young_min_spawn_egg");
        ModItems.HIDDEN_EYE_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HIDDEN_EYE, "hidden_eye_spawn_egg");
        // 注册高脚鸟刷怪蛋
        ModItems.HIGHBIRD_BABY_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HIGHBIRD_BABY, "highbird_baby_spawn_egg");
        ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HIGHBIRD_TEENAGE, "highbird_teenage_spawn_egg");
        ModItems.HIGHBIRD_ADULTHOOD_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HIGHBIRD_ADULTHOOD, "highbird_adulthood_spawn_egg");
        ModItems.HIGHBIRD_EGG_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HIGHBIRD_EGG, "highbird_egg_spawn_egg");
        ModItems.XUN_SHENG_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.XUN_SHENG, "xun_sheng_spawn_egg");
        ModItems.DEEP_CREATURE_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.DEEP_CREATURE, "deep_creature_spawn_egg");
        ModItems.WITHER_SKELETON_KING_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.WITHER_SKELETON_KING, "wither_skeleton_king_spawn_egg");
        ModItems.MILITIA_ARCHER_VILLAGER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.MILITIA_ARCHER_VILLAGER, "militia_archer_villager_spawn_egg");
        ModItems.MILITIA_WARRIOR_VILLAGER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.MILITIA_WARRIOR_VILLAGER, "militia_warrior_villager_spawn_egg");
        ModItems.ARCHER_VILLAGER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.ARCHER_VILLAGER, "archer_villager_spawn_egg");
        ModItems.WARRIOR_VILLAGER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.WARRIOR_VILLAGER, "warrior_villager_spawn_egg");
        ModItems.BLUE_IRON_GOLEM_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.BLUE_IRON_GOLEM, "blue_iron_golem_spawn_egg");
        ModItems.SUGAR_MAN_SCORPION_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SUGAR_MAN_SCORPION, "sugar_man_scorpion_spawn_egg");
        ModItems.IRON_GOLEM_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.VILLAGER_IRON_GOLEM_ENTITY, "iron_golem_spawn_egg");
        ModItems.LITTLE_PERSON_CIVILIAN_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_CIVILIAN, "little_person_civilian_spawn_egg");
        ModItems.LITTLE_PERSON_MILITIA_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_MILITIA, "little_person_militia_spawn_egg");
        ModItems.LITTLE_PERSON_ARCHER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_ARCHER, "little_person_archer_spawn_egg");
        ModItems.LITTLE_PERSON_GIANT_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_GIANT, "little_person_giant_spawn_egg");
        ModItems.LITTLE_PERSON_GUARD_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_GUARD, "little_person_guard_spawn_egg");
        ModItems.LITTLE_PERSON_KING_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.LITTLE_PERSON_KING, "little_person_king_spawn_egg");
        ModItems.VILLAGER_KING_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.VILLAGER_KING_ENTITY, "villager_king_spawn_egg");
        ModItems.VINDICATOR_GENERAL_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.VINDICATOR_GENERAL, "vindicator_general_spawn_egg");

        ModItems.HULKBUSTER_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.HULKBUSTER, "hulkbuster_spawn_egg");
        ModItems.SILENCE_PHANTOM_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.SILENCE_PHANTOM, "silence_phantom_spawn_egg");
        ModItems.COAL_SILVERFISH_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(ModEntities.COAL_SILVERFISH, "coal_silverfish_spawn_egg");
        ModItems.PIGLIN_BRUTE_SPEAR_USE_SPAWN_EGG = registerPiglinBruteSpearSpawnEgg(
                "piglin_brute_spear_use_spawn_egg",
                true
        );
        ModItems.PIGLIN_BRUTE_SPEAR_MELEE_SPAWN_EGG = registerPiglinBruteSpearSpawnEgg(
                "piglin_brute_spear_melee_spawn_egg",
                false
        );
        ModItems.PIGLIN_BRUTE_BOW_SPAWN_EGG = registerPiglinBruteWeaponSpawnEgg(
                "piglin_brute_bow_spawn_egg",
                PiglinBruteWeaponData.FORCE_BOW_KEY
        );
        ModItems.PIGLIN_BRUTE_CROSSBOW_SPAWN_EGG = registerPiglinBruteWeaponSpawnEgg(
                "piglin_brute_crossbow_spawn_egg",
                PiglinBruteWeaponData.FORCE_CROSSBOW_KEY
        );
        ModItems.BOW_ZOMBIE_SPAWN_EGG = registerBowZombieSpawnEgg();
        ModItems.PIGLIN_BRUTE_SPEAR_MOD_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(
                ModEntities.PIGLIN_BRUTE_SPEAR_MOD,
                "piglin_brute_spear_mod_spawn_egg"
        );
        ModItems.BOW_ZOMBIE_MOD_SPAWN_EGG = RegistrySupport.registerSpawnEggItem(
                ModEntities.BOW_ZOMBIE_MOD,
                "bow_zombie_mod_spawn_egg"
        );
    }

    /** 在全部普通物品完成后注册由实体目录声明的动态刷怪蛋。 */
    public static void initDynamic() {
        com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes.init();
        ModEntities.SPAWN_EGG_ENTITIES.forEach((id, entityType) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) entityType;
            RegistrySupport.registerSpawnEggItem(mobType, id);
        });
    }

    /**
     * 返回三个特殊村民刷怪蛋应复用的原版物品模型。
     *
     * <p>26.1.2 的刷怪蛋颜色由原版 {@code item/<mob>_spawn_egg} 贴图提供；
     * 返回原版模型路径可同时复用其物品栏、手持模型和颜色来源。</p>
     */
    public static @Nullable Identifier getVanillaSpawnEggModel(SpawnEggItem item) {
        return switch (BuiltInRegistries.ITEM.getKey(item).getPath()) {
            case "evoker_villager" -> Identifier.withDefaultNamespace("item/evoker_spawn_egg");
            case "piglin_villager" -> Identifier.withDefaultNamespace("item/piglin_spawn_egg");
            case "wither_skeleton_villager" -> Identifier.withDefaultNamespace("item/wither_skeleton_spawn_egg");
            default -> null;
        };
    }

    private static SpawnEggItem registerPiglinBruteSpearSpawnEgg(String id, boolean useSpearAsItem) {
        CompoundTag entityData = new CompoundTag();
        entityData.putBoolean(IPiglinBruteSpearMode.FORCE_GOLDEN_SPEAR_KEY, true);
        if (useSpearAsItem) {
            entityData.putInt(IPiglinBruteSpearMode.SPEAR_ATTACK_MODE_KEY, IPiglinBruteSpearMode.SPEAR_MODE_USE);
        }
        SpawnEggItem item = RegistrySupport.registerItem(
                id,
                new SpawnEggItem(
                        RegistrySupport.registryBaseItemSettings(id)
                                .spawnEgg(EntityType.PIGLIN_BRUTE)
                                .component(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.PIGLIN_BRUTE, entityData))
                ),
                false,
                false
        );
        RegistrySupport.SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }

    private static SpawnEggItem registerBowZombieSpawnEgg() {
        String id = "bow_zombie_spawn_egg";
        CompoundTag entityData = new CompoundTag();
        entityData.putBoolean(ZombieBowData.FORCE_BOW_KEY, true);
        SpawnEggItem item = RegistrySupport.registerItem(
                id,
                new SpawnEggItem(
                        RegistrySupport.registryBaseItemSettings(id)
                                .spawnEgg(EntityType.ZOMBIE)
                                .component(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.ZOMBIE, entityData))
                ),
                false,
                false
        );
        RegistrySupport.SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }

    private static SpawnEggItem registerPiglinBruteWeaponSpawnEgg(String id, String forceWeaponKey) {
        CompoundTag entityData = new CompoundTag();
        entityData.putBoolean(forceWeaponKey, true);
        SpawnEggItem item = RegistrySupport.registerItem(
                id,
                new SpawnEggItem(
                        RegistrySupport.registryBaseItemSettings(id)
                                .spawnEgg(EntityType.PIGLIN_BRUTE)
                                .component(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.PIGLIN_BRUTE, entityData))
                ),
                false,
                false
        );
        RegistrySupport.SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }

}
