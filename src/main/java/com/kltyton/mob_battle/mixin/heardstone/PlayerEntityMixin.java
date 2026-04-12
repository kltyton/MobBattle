package com.kltyton.mob_battle.mixin.heardstone;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullKingEntity;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.utils.HeadStoneUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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

@Mixin(PlayerEntity.class)
@Implements(@Interface(iface = IPlayerEntityAccessor.class, prefix = "accessor$"))
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    protected abstract void vanishCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean dropInventory(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtil.keepInventory((PlayerEntity) (Object) this)) {
            HeadStoneUtil.consumeHeartStones((PlayerEntity) (Object) this, 2);
            this.vanishCursedItems();
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Redirect(method = "getExperienceToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean getExperienceToDrop(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (HeadStoneUtil.keepInventory((PlayerEntity) (Object) this)) {
            return true;
        }
        return instance.getBoolean(rule);
    }
    @Inject(method = "isUsingSpyglass", at = @At("RETURN"), cancellable = true)
    public void isUsingSpyglass(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMainHandStack().getItem() instanceof VsSnipe vsSnipeItem && vsSnipeItem.isLeftClick) {
            cir.setReturnValue(true);
        }
    }
    //无敌帧
    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean isInvulnerableTo(PlayerEntity instance, ServerWorld world, DamageSource source) {
        Entity sourcer = source.getSource();
        Entity attacker = source.getAttacker();
        if ((sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) || source.isOf(DamageTypes.THORNS) || (source.isOf(DamageTypes.ON_FIRE) && !this.isFireImmune() && !world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE))) {
            instance.timeUntilRegen = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }
    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float modifyExhaustion(float exhaustion) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 检查玩家是否有糖分效果
        if (player.hasStatusEffect(ModEffects.SUGAR_ENTRY)) {
            int amplifier = Objects.requireNonNull(player.getStatusEffect(ModEffects.SUGAR_ENTRY)).getAmplifier();
            float multiplier = 1.0f - (0.2f * (amplifier + 1));
            multiplier = Math.max(0.0f, multiplier);
            return exhaustion * multiplier;
        }
        return exhaustion;
    }
    @Inject(method = "initDataTracker", at = @At("RETURN"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(DataTrackersEvent.IS_GECKO_LIB_USING, false);
    }

    public void accessor$setUseGeckoLib(boolean use) {
        this.dataTracker.set(DataTrackersEvent.IS_GECKO_LIB_USING, use);
    }

    public boolean accessor$isUsingGeckoLib() {
        return this.dataTracker.get(DataTrackersEvent.IS_GECKO_LIB_USING);
    }

    @Inject(method = "writeCustomData", at = @At("RETURN"))
    protected void writeCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("IsGeckoLibUsing", this.dataTracker.get(DataTrackersEvent.IS_GECKO_LIB_USING));
    }
    @Inject(method = "readCustomData", at = @At("RETURN"))
    protected void readCustomData(ReadView view, CallbackInfo ci) {
        this.dataTracker.set(DataTrackersEvent.IS_GECKO_LIB_USING, view.getBoolean("IsGeckoLibUsing", false));
    }
}
