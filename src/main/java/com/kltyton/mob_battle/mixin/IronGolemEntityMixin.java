package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemEntityMixin extends AbstractGolem implements NeutralMob {
    protected IronGolemEntityMixin(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    public void damage(IronGolem instance, SoundEvent soundEvent, float v, float i) {
        if (!(instance instanceof WarriorVillager)) {
            instance.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }
    }
    @Inject(method = "doPush", at = @At("RETURN"))
    protected void pushAway(Entity entity, CallbackInfo ci) {
        if (this instanceof ModBaseIronGolemEntity && this.getRandom().nextInt(20) == 0 && entity instanceof Enemy) {
            this.setTarget((LivingEntity)entity);
        }
    }
    @Inject(method = "canAttackType", at = @At("RETURN"), cancellable = true)
    public void canTarget(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof ModBaseIronGolemEntity && type == EntityType.CREEPER) cir.setReturnValue(true);
    }
}
