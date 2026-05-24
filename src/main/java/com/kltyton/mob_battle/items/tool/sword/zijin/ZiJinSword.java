package com.kltyton.mob_battle.items.tool.sword.zijin;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ZiJinSword extends BaseSword implements ModFabricItem {
    public ZiJinSword(Settings settings) {
        super(settings);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            CombatEffectUtil.addPigSpiritMark(target, player, 1);
        }
    }

    @Override
    public void onSuccessfulCriticalHit(PlayerEntity player, Entity target, ItemStack stack) {
        if (target instanceof LivingEntity sweptTarget) CombatEffectUtil.addPigSpiritMark(sweptTarget, player, 7);
    }
    @Override
    public void onSuccessfulSweepHit(PlayerEntity player, Entity target, ItemStack stack) {
        if (target instanceof LivingEntity sweptTarget) CombatEffectUtil.addPigSpiritMark(sweptTarget, player, 4);
    }
}
