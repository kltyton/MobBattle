package com.kltyton.mob_battle.items.tool.sword.zijin;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ZiJinSword extends BaseSword implements ModFabricItem {
    public ZiJinSword(Properties settings) {
        super(settings);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            CombatEffectUtil.addPigSpiritMark(target, player, 1);
        }
    }

    @Override
    public void onSuccessfulCriticalHit(Player player, Entity target, ItemStack stack) {
        if (target instanceof LivingEntity sweptTarget) CombatEffectUtil.addPigSpiritMark(sweptTarget, player, 7);
    }
    @Override
    public void onSuccessfulSweepHit(Player player, Entity target, ItemStack stack) {
        if (target instanceof LivingEntity sweptTarget) CombatEffectUtil.addPigSpiritMark(sweptTarget, player, 4);
    }
}
