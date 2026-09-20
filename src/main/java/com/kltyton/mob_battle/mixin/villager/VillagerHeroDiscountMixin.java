package com.kltyton.mob_battle.mixin.villager;

import com.kltyton.mob_battle.entity.villager.trading.NoHeroDiscountVillager;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 屏蔽标记村民的声望与村庄英雄折扣，普通村民行为保持原版。
 */
@Mixin(Villager.class)
public abstract class VillagerHeroDiscountMixin {
    @Redirect(
            method = "updateSpecialPrices",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/villager/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"
            )
    )
    private int mobBattle$excludeReputationDiscount(Villager villager, Player player) {
        return (Object) this instanceof NoHeroDiscountVillager ? 0 : villager.getPlayerReputation(player);
    }

    @Redirect(
            method = "updateSpecialPrices",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private boolean mobBattle$excludeHeroDiscount(Player player, Holder<MobEffect> effect) {
        if ((Object) this instanceof NoHeroDiscountVillager && effect == MobEffects.HERO_OF_THE_VILLAGE) {
            return false;
        }
        return player.hasEffect(effect);
    }
}
