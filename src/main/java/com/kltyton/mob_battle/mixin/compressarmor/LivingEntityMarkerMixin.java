package com.kltyton.mob_battle.mixin.compressarmor;

import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
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
    private static final TrackedData<Integer> COMPRESSED_ARMOR_MARKER_TYPE =
            DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Unique
    private static final TrackedData<Integer> PIG_SPIRIT_MARK_AMPLIFIER =
            DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void mobBattle$initCompressedArmorMarkerData(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(COMPRESSED_ARMOR_MARKER_TYPE, 0);
        builder.add(PIG_SPIRIT_MARK_AMPLIFIER, -1);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$syncMarkerData(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.getWorld().isClient()) {
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
        return entity.getDataTracker().get(COMPRESSED_ARMOR_MARKER_TYPE);
    }

    @Override
    public void mobBattle$setCompressedArmorMarkerType(int markerType) {
        LivingEntity entity = (LivingEntity) (Object) this;
        entity.getDataTracker().set(COMPRESSED_ARMOR_MARKER_TYPE, markerType);
    }

    @Override
    public int mobBattle$getPigSpiritMarkAmplifier() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getDataTracker().get(PIG_SPIRIT_MARK_AMPLIFIER);
    }

    @Override
    public void mobBattle$setPigSpiritMarkAmplifier(int amplifier) {
        LivingEntity entity = (LivingEntity) (Object) this;
        entity.getDataTracker().set(PIG_SPIRIT_MARK_AMPLIFIER, amplifier);
    }

    @Unique
    private static int mobBattle$getMarkerTypeFromStatusEffect(LivingEntity entity) {
        int markerType = 0;

        if (entity.hasStatusEffect(ModEffects.DIAMOND_MARK_ENTRY)) {
            markerType |= DIAMOND_MARKER_MASK;
        }

        if (entity.hasStatusEffect(ModEffects.NETHERITE_MARK_ENTRY)) {
            markerType |= NETHERITE_MARKER_MASK;
        }

        return markerType;
    }

    @Unique
    private static int mobBattle$getPigSpiritMarkAmplifierFromStatusEffect(LivingEntity entity) {
        StatusEffectInstance pigSpiritMark = entity.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        return pigSpiritMark == null ? -1 : pigSpiritMark.getAmplifier();
    }
}