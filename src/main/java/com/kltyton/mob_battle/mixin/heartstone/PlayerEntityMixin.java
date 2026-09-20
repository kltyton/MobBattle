package com.kltyton.mob_battle.mixin.heartstone;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullKingEntity;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.items.heartstone.HeartStoneInventory;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(Player.class)
@Implements(@Interface(iface = IPlayerEntityAccessor.class, prefix = "accessor$"))
public abstract class PlayerEntityMixin extends LivingEntity {
    @Unique
    private ItemStack mobBattle$vsSnipeStateStack = ItemStack.EMPTY;
    @Unique
    private boolean mobBattle$vsSnipeLeftClicking;
    @Unique
    private boolean mobBattle$vsSnipeCharging;
    @Unique
    private boolean mobBattle$vsSnipeLoaded;

    @Shadow
    protected abstract void destroyVanishingCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    /** 换手后立即清除旧狙击枪栈的瞬态状态，避免重新装备时复用旧瞄准阶段。 */
    @Inject(method = "tick", at = @At("HEAD"))
    private void clearVsSnipeStateOnHeldItemChange(CallbackInfo ci) {
        ItemStack currentStack = this.getMainHandItem();
        if (!this.mobBattle$vsSnipeStateStack.isEmpty()
                && (currentStack != this.mobBattle$vsSnipeStateStack
                || !(currentStack.getItem() instanceof VsSnipe))) {
            this.accessor$clearVsSnipeState();
        }
    }

    /** 通过库存入口换手时立即清除状态，不等待下一次玩家 tick。 */
    @Inject(method = "setItemInHand", at = @At("HEAD"))
    private void clearVsSnipeStateBeforeHeldItemChange(InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        if (hand == InteractionHand.MAIN_HAND
                && stack != this.mobBattle$vsSnipeStateStack) {
            this.accessor$clearVsSnipeState();
        }
    }

    @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    public Object dropInventory(GameRules instance, GameRule<Boolean> rule) {
        if (HeartStoneInventory.keepInventory((Player) (Object) this)) {
            HeartStoneInventory.consumeHeartStones((Player) (Object) this, 2);
            this.destroyVanishingCursedItems();
            return true;
        }
        return instance.get(rule);
    }
    @Redirect(method = "getBaseExperienceReward", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    public Object getExperienceToDrop(GameRules instance, GameRule<Boolean> rule) {
        if (HeartStoneInventory.keepInventory((Player) (Object) this)) {
            return true;
        }
        return instance.get(rule);
    }
    @Inject(method = "isScoping", at = @At("RETURN"), cancellable = true)
    public void isUsingSpyglass(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMainHandItem().getItem() instanceof VsSnipe
                && ((IPlayerEntityAccessor) (Object) this).isVsSnipeLeftClicking()) {
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

    public boolean accessor$isVsSnipeLeftClicking() {
        ItemStack currentStack = this.getMainHandItem();
        if (this.mobBattle$vsSnipeLeftClicking
                && (currentStack != this.mobBattle$vsSnipeStateStack
                || !(currentStack.getItem() instanceof VsSnipe))) {
            this.accessor$clearVsSnipeState();
        }
        return this.mobBattle$vsSnipeLeftClicking;
    }

    public void accessor$setVsSnipeLeftClicking(ItemStack stack, boolean active) {
        this.mobBattle$ensureVsSnipeStack(stack);
        this.mobBattle$vsSnipeLeftClicking = active;
    }

    public void accessor$clearVsSnipeState() {
        this.mobBattle$vsSnipeStateStack = ItemStack.EMPTY;
        this.mobBattle$vsSnipeLeftClicking = false;
        this.mobBattle$vsSnipeCharging = false;
        this.mobBattle$vsSnipeLoaded = false;
    }

    public void accessor$resetVsSnipeLoading(ItemStack stack) {
        this.mobBattle$ensureVsSnipeStack(stack);
        this.mobBattle$vsSnipeCharging = false;
        this.mobBattle$vsSnipeLoaded = false;
    }

    public boolean accessor$isVsSnipeCharging(ItemStack stack) {
        return this.mobBattle$isVsSnipeStack(stack) && this.mobBattle$vsSnipeCharging;
    }

    public boolean accessor$isVsSnipeLoaded(ItemStack stack) {
        return this.mobBattle$isVsSnipeStack(stack) && this.mobBattle$vsSnipeLoaded;
    }

    public void accessor$setVsSnipeCharging(ItemStack stack, boolean charging) {
        this.mobBattle$ensureVsSnipeStack(stack);
        this.mobBattle$vsSnipeCharging = charging;
    }

    public void accessor$setVsSnipeLoaded(ItemStack stack, boolean loaded) {
        this.mobBattle$ensureVsSnipeStack(stack);
        this.mobBattle$vsSnipeLoaded = loaded;
    }

    @Unique
    private boolean mobBattle$isVsSnipeStack(ItemStack stack) {
        return stack == this.mobBattle$vsSnipeStateStack;
    }

    @Unique
    private void mobBattle$ensureVsSnipeStack(ItemStack stack) {
        if (!this.mobBattle$isVsSnipeStack(stack)) {
            this.accessor$clearVsSnipeState();
            this.mobBattle$vsSnipeStateStack = stack;
        }
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
