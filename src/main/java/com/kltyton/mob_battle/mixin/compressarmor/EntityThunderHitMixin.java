package com.kltyton.mob_battle.mixin.compressarmor;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityThunderHitMixin {
    @Inject(method = "thunderHit", at = @At("TAIL"))
    private void mobBattle$chargeCompressedCopper(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        if (!((Object) this instanceof LivingEntity living)) {
            return;
        }
        if (!ArmorUtil.hasFullArmor(living, ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE)) {
            return;
        }

        living.addEffect(new MobEffectInstance(ModEffects.COMPRESSED_COPPER_CHARGED_ENTRY, 16 * 60 * 20, 0, false, false, true));
        living.setAbsorptionAmount(Math.max(living.getAbsorptionAmount(), 80.0F));
    }
}
