package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IronGolemEntity.class)
public class IronGolemEntityMixin {
    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/IronGolemEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    public void damage(IronGolemEntity instance, SoundEvent soundEvent, float v, float i) {
        if (!(instance instanceof WarriorVillager)) {
            instance.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }
    }
}
