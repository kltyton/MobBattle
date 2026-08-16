package com.kltyton.mob_battle.mixin.item;

import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerAttackCooldownMixin {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void mobBattle$cancelBloodKnifeAttackDuringCooldown(Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BloodKnifeItem
                && StackBoundCooldowns.isCoolingDown(player, stack, BloodKnifeItem.COOLDOWN_ID, BloodKnifeItem.COOLDOWN_TICKS)) {
            ci.cancel();
        }
    }
}
