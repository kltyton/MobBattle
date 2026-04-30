package com.kltyton.mob_battle.mixin.compressarmor;

import com.kltyton.mob_battle.accessor.ICompressedArmorMarker;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMarkerMixin implements ICompressedArmorMarker {
    @Unique
    private static final int DIAMOND_MARKER_MASK = 1;
    @Unique
    private static final int NETHERITE_MARKER_MASK = 2;
    @Unique
    private static final TrackedData<Integer> COMPRESSED_ARMOR_MARKER_TYPE =
            DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void mobBattle$initCompressedArmorMarkerData(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(COMPRESSED_ARMOR_MARKER_TYPE, 0);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$syncCompressedArmorMarker(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld().isClient()) {
            return;
        }

        int markerType = mobBattle$getMarkerTypeFromStatusEffect(entity);
        if (this.mobBattle$getCompressedArmorMarkerType() != markerType) {
            this.mobBattle$setCompressedArmorMarkerType(markerType);
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
}
