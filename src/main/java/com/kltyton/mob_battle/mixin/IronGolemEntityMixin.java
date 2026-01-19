package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntity implements Angerable {
    protected IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/IronGolemEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    public void damage(IronGolemEntity instance, SoundEvent soundEvent, float v, float i) {
        if (!(instance instanceof WarriorVillager)) {
            instance.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }
    }
    @Inject(method = "pushAway", at = @At("RETURN"))
    protected void pushAway(Entity entity, CallbackInfo ci) {
        if (this instanceof ModBaseIronGolemEntity && this.getRandom().nextInt(20) == 0 && entity instanceof Monster) {
            this.setTarget((LivingEntity)entity);
        }
    }
    @Inject(method = "canTarget", at = @At("RETURN"), cancellable = true)
    public void canTarget(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof ModBaseIronGolemEntity && type == EntityType.CREEPER) cir.setReturnValue(true);
    }
}
