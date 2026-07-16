package com.kltyton.mob_battle.mixin.projectile;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityExplosionMixin {
    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    private void mobBattle$ignoreOwnedCustomFireballExplosion(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        if (explosion.getDirectSourceEntity() instanceof CustomFireballEntity fireball
                && fireball.getOwner() == (Object) this) {
            cir.setReturnValue(true);
        }
    }
}
