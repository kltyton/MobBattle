package com.kltyton.mob_battle.mixin.heardstone;

import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.utils.HeadStoneUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean dropInventory(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtil.keepInventory((PlayerEntity) (Object) this)) {
            HeadStoneUtil.consumeHeartStones((PlayerEntity) (Object) this, 2);
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Redirect(method = "getExperienceToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean getExperienceToDrop(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtil.keepInventory((PlayerEntity) (Object) this)) {
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Inject(method = "isUsingSpyglass", at = @At("RETURN"), cancellable = true)
    public void isUsingSpyglass(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMainHandStack().getItem() instanceof VsSnipe vsSnipeItem && vsSnipeItem.isLeftClick) {
            cir.setReturnValue(true);
        }
    }
}
