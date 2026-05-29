package com.kltyton.mob_battle.mixin.heardstone;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullKingEntity;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.utils.HeadStoneUtil;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(Player.class)
@Implements(@Interface(iface = IPlayerEntityAccessor.class, prefix = "accessor$"))
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    protected abstract void destroyVanishingCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    public boolean dropInventory(GameRules instance, GameRules.Key<GameRules.BooleanValue> rule) {
        if (HeadStoneUtil.keepInventory((Player) (Object) this)) {
            HeadStoneUtil.consumeHeartStones((Player) (Object) this, 2);
            this.destroyVanishingCursedItems();
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Redirect(method = "getBaseExperienceReward", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    public boolean getExperienceToDrop(GameRules instance, GameRules.Key<GameRules.BooleanValue> rule) {
        if (HeadStoneUtil.keepInventory((Player) (Object) this)) {
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Inject(method = "isScoping", at = @At("RETURN"), cancellable = true)
    public void isUsingSpyglass(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMainHandItem().getItem() instanceof VsSnipe vsSnipeItem && vsSnipeItem.isLeftClick) {
            cir.setReturnValue(true);
        }
    }
    //无敌帧
    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInvulnerableTo(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    public boolean isInvulnerableTo(Player instance, ServerLevel world, DamageSource source) {
        Entity sourcer = source.getDirectEntity();
        Entity attacker = source.getEntity();
        if ((sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) || source.is(DamageTypes.THORNS)) {
            instance.invulnerableTime = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }
    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true)
    private float modifyExhaustion(float exhaustion) {
        Player player = (Player) (Object) this;

        // 检查玩家是否有糖分效果
        if (player.hasEffect(ModEffects.SUGAR_ENTRY)) {
            int amplifier = Objects.requireNonNull(player.getEffect(ModEffects.SUGAR_ENTRY)).getAmplifier();
            float multiplier = 1.0f - (0.2f * (amplifier + 1));
            multiplier = Math.max(0.0f, multiplier);
            return exhaustion * multiplier;
        }
        return exhaustion;
    }
    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    protected void initDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DataTrackersEvent.IS_GECKO_LIB_USING, false);
    }

    public void accessor$setUseGeckoLib(boolean use) {
        this.entityData.set(DataTrackersEvent.IS_GECKO_LIB_USING, use);
    }

    public boolean accessor$isUsingGeckoLib() {
        return this.entityData.get(DataTrackersEvent.IS_GECKO_LIB_USING);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void writeCustomData(ValueOutput view, CallbackInfo ci) {
        view.putBoolean("IsGeckoLibUsing", this.entityData.get(DataTrackersEvent.IS_GECKO_LIB_USING));
    }
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readCustomData(ValueInput view, CallbackInfo ci) {
        this.entityData.set(DataTrackersEvent.IS_GECKO_LIB_USING, view.getBooleanOr("IsGeckoLibUsing", false));
    }
}
