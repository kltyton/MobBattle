package com.kltyton.mob_battle.items.weapon.biochemical;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 生化刃的服务端命中行为。
 *
 * <p>物品使用 {@code WEAPON} 组件进入原版成功命中回调，但不设置耐久组件；
 * 这样可以同时满足最大堆叠 40 与每次成功攻击消耗一件的协议。</p>
 */
public final class BiochemicalBladeItem extends Item {
    /** 生化刃的额外魔法伤害。 */
    public static final float EXTRA_MAGIC_DAMAGE = 0.5F;

    public BiochemicalBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker.level() instanceof ServerLevel)
                || !EntityQueries.isValidCombatTarget(attacker, target)) {
            return;
        }

        // 原版物理命中刚刚设置了无敌帧；清零后追加独立魔法命中。
        target.invulnerableTime = 0;
        target.hurtServer((ServerLevel) attacker.level(),
                attacker.damageSources().indirectMagic(attacker, attacker), EXTRA_MAGIC_DAMAGE);

        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }
}
