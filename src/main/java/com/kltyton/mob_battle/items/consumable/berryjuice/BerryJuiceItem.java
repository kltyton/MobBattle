package com.kltyton.mob_battle.items.consumable.berryjuice;

import net.minecraft.world.item.Item;

/**
 * 甜浆果汁物品类型。
 *
 * <p>饮用数据组件由 {@link BerryJuiceItems} 统一构造；本类型只提供清晰的
 * 领域类型边界，避免把饮品行为混入共享物品门面。</p>
 */
public final class BerryJuiceItem extends Item {

    public BerryJuiceItem(Properties properties) {
        super(properties);
    }
}
