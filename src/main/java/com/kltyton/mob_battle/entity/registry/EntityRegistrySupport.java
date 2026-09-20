package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * 实体类型注册的共享支持层。
 *
 * <p>承接原 {@code ModEntities.createEntityType} 的全部语义：按名称构造
 * ResourceKey 与 EntityType、立即写入 {@code BuiltInRegistries.ENTITY_TYPE}，
 * 并按布尔开关同步填充 {@code ModEntities.SPAWN_EGG_ENTITIES} 与
 * {@code ModEntities.GENERAL_RENDERERS}。注册 ID、Map 填充时机与顺序均与重构前逐字一致。</p>
 *
 * <p><b>加载顺序契约：</b>本类只能由 ModEntities 静态初始化过程中的别名字段触发；
 * 触发时 ModEntities 的三个 Map 已完成实例化，因此可以安全写入。</p>
 */
public final class EntityRegistrySupport {

    private EntityRegistrySupport() {
    }

    /** 立即注册实体类型并填充兼容 Map（与原 ModEntities.createEntityType 逐字一致）。 */
    public static <T extends Entity> EntityType<T> registerEntityType(
            String name, EntityType.Builder<T> builder,
            boolean registerSpawnEgg, boolean generalRenderer) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
                Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        if (registerSpawnEgg) {
            ModEntities.SPAWN_EGG_ENTITIES.put(name, type);
        }
        if (generalRenderer) {
            ModEntities.GENERAL_RENDERERS.put(name, type);
        }
        return type;
    }
}
