package com.kltyton.mob_battle.mixin.compressarmor;

import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMarkerMixin implements IEffectMarker {
    @Unique
    private static final int DIAMOND_MARKER_MASK = 1;

    @Unique
    private static final int NETHERITE_MARKER_MASK = 2;

    @Unique
    private static final EntityDataAccessor<Integer> COMPRESSED_ARMOR_MARKER_TYPE =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Integer> PIG_SPIRIT_MARK_AMPLIFIER =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void mobBattle$initCompressedArmorMarkerData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(COMPRESSED_ARMOR_MARKER_TYPE, 0);
        builder.define(PIG_SPIRIT_MARK_AMPLIFIER, -1);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$syncMarkerData(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.level().isClientSide()) {
            return;
        }

        int markerType = mobBattle$getMarkerTypeFromStatusEffect(entity);
        if (this.mobBattle$getCompressedArmorMarkerType() != markerType) {
            this.mobBattle$setCompressedArmorMarkerType(markerType);
        }

        int pigSpiritMarkAmplifier = mobBattle$getPigSpiritMarkAmplifierFromStatusEffect(entity);
        if (this.mobBattle$getPigSpiritMarkAmplifier() != pigSpiritMarkAmplifier) {
            this.mobBattle$setPigSpiritMarkAmplifier(pigSpiritMarkAmplifier);
        }
    }

    @Override
    public int mobBattle$getCompressedArmorMarkerType() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getEntityData().get(COMPRESSED_ARMOR_MARKER_TYPE);
    }

    @Override
    public void mobBattle$setCompressedArmorMarkerType(int markerType) {
        LivingEntity entity = (LivingEntity) (Object) this;
        entity.getEntityData().set(COMPRESSED_ARMOR_MARKER_TYPE, markerType);
    }

    @Override
    public int mobBattle$getPigSpiritMarkAmplifier() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getEntityData().get(PIG_SPIRIT_MARK_AMPLIFIER);
    }

    @Override
    public void mobBattle$setPigSpiritMarkAmplifier(int amplifier) {
        LivingEntity entity = (LivingEntity) (Object) this;
        entity.getEntityData().set(PIG_SPIRIT_MARK_AMPLIFIER, amplifier);
    }

    @Unique
    private static int mobBattle$getMarkerTypeFromStatusEffect(LivingEntity entity) {
        int markerType = 0;

        if (entity.hasEffect(ModEffects.DIAMOND_MARK_ENTRY)) {
            markerType |= DIAMOND_MARKER_MASK;
        }

        if (entity.hasEffect(ModEffects.NETHERITE_MARK_ENTRY)) {
            markerType |= NETHERITE_MARKER_MASK;
        }

        return markerType;
    }

    @Unique
    private static int mobBattle$getPigSpiritMarkAmplifierFromStatusEffect(LivingEntity entity) {
        MobEffectInstance pigSpiritMark = entity.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        return pigSpiritMark == null ? -1 : pigSpiritMark.getAmplifier();
    }
}