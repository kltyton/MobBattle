package com.kltyton.mob_battle.mixin.heardstone;

import com.kltyton.mob_battle.utils.HeadStoneUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean dropInventory(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtils.keepInventory((PlayerEntity) (Object) this)) {
            HeadStoneUtils.consumeHeartStones((PlayerEntity) (Object) this, 2);
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Redirect(method = "getExperienceToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean getExperienceToDrop(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtils.keepInventory((PlayerEntity) (Object) this)) {
            return true;
        }
        return instance.getBoolean(rule);
    }
}
