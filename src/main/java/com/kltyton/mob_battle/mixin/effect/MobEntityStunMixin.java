package com.kltyton.mob_battle.mixin.effect;

import com.kltyton.mob_battle.accessor.IStunAiState;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.StunEffect;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityStunMixin implements IStunAiState {
    @Unique
    private static final String STUN_AI_STATE_KEY = "MobBattleStunAiState";

    @Unique
    private static final String STUN_ORIGINAL_AI_DISABLED_KEY = "MobBattleStunOriginalAiDisabled";

    @Unique
    private static final String STUN_EXTERNAL_AI_DISABLED_KEY = "MobBattleStunExternalAiDisabled";

    @Unique
    private boolean mobBattle$hasStunAiState;

    @Unique
    private boolean mobBattle$stunOriginalAiDisabled;

    @Unique
    private boolean mobBattle$stunExternalAiDisabled;

    @Inject(method = "setAiDisabled", at = @At("HEAD"))
    private void mobBattle$trackExternalAiDisable(boolean aiDisabled, CallbackInfo ci) {
        if (this.mobBattle$hasStunAiState && !StunEffect.isStunAiChange()) {
            this.mobBattle$stunExternalAiDisabled = aiDisabled;
            if (!aiDisabled) {
                this.mobBattle$stunOriginalAiDisabled = false;
            }
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void mobBattle$writeStunAiState(WriteView view, CallbackInfo ci) {
        if (!this.mobBattle$hasStunAiState) {
            return;
        }

        view.putBoolean(STUN_AI_STATE_KEY, true);
        view.putBoolean(STUN_ORIGINAL_AI_DISABLED_KEY, this.mobBattle$stunOriginalAiDisabled);
        view.putBoolean(STUN_EXTERNAL_AI_DISABLED_KEY, this.mobBattle$stunExternalAiDisabled);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void mobBattle$readStunAiState(ReadView view, CallbackInfo ci) {
        this.mobBattle$hasStunAiState = view.getBoolean(STUN_AI_STATE_KEY, false);
        this.mobBattle$stunOriginalAiDisabled = this.mobBattle$hasStunAiState
                && view.getBoolean(STUN_ORIGINAL_AI_DISABLED_KEY, false);
        this.mobBattle$stunExternalAiDisabled = this.mobBattle$hasStunAiState
                && view.getBoolean(STUN_EXTERNAL_AI_DISABLED_KEY, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$restoreStaleStunAiState(CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        if (!mob.getWorld().isClient() && this.mobBattle$hasStunAiState && !mob.hasStatusEffect(ModEffects.STUN_ENTRY)) {
            StunEffect.restoreAiState(mob);
        }
    }

    @Override
    public boolean mobBattle$hasStunAiState() {
        return this.mobBattle$hasStunAiState;
    }

    @Override
    public void mobBattle$beginStunAiState(boolean originalAiDisabled) {
        this.mobBattle$hasStunAiState = true;
        this.mobBattle$stunOriginalAiDisabled = originalAiDisabled;
        this.mobBattle$stunExternalAiDisabled = false;
    }

    @Override
    public boolean mobBattle$getOriginalAiDisabled() {
        return this.mobBattle$stunOriginalAiDisabled;
    }

    @Override
    public boolean mobBattle$hasExternalAiDisable() {
        return this.mobBattle$stunExternalAiDisabled;
    }

    @Override
    public void mobBattle$setExternalAiDisable(boolean externalAiDisable) {
        this.mobBattle$stunExternalAiDisabled = externalAiDisable;
    }

    @Override
    public void mobBattle$clearStunAiState() {
        this.mobBattle$hasStunAiState = false;
        this.mobBattle$stunOriginalAiDisabled = false;
        this.mobBattle$stunExternalAiDisabled = false;
    }
}
