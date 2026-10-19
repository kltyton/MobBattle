package com.kltyton.mob_battle.mixin.item;

import com.kltyton.mob_battle.items.registry.CompressedToolItems;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** 只替换原版满蓄力暴击分支的倍率，保留原命中、附魔、伤害和反馈回调。 */
@Mixin(Player.class)
public abstract class CompressedCopperAxeCriticalMixin {
    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
    private float mobBattle$copperAxeCritical(float original) {
        return ((Player) (Object) this).getWeaponItem().is(CompressedToolItems.COPPER_AXE) ? 2.0F : original;
    }
}
