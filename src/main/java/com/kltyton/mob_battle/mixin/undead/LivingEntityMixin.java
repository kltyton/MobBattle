package com.kltyton.mob_battle.mixin.undead;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullKingEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.tags.ModTags;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = ILead.class, prefix = "custom$"))
public abstract class LivingEntityMixin extends Entity implements Attackable, WaypointTransmitter {
    @Unique
    private static final EntityDataAccessor<Boolean> UNIVERSAL_LEAD = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> INVISIBLE_LEAD = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private boolean mobBattle$handlingExcitementBonus;

    @Unique
    public boolean custom$getIsUniversalLeadEnyity() {
        return this.entityData.get(UNIVERSAL_LEAD);
    }

    @Unique
    public void custom$setIsUniversalLeadEnyity(boolean value) {
        this.entityData.set(UNIVERSAL_LEAD, value);
    }

    @Unique
    public boolean custom$getIsInvisibleUniversalLeadEnyity() {
        return this.entityData.get(INVISIBLE_LEAD);
    }

    @Unique
    public void custom$setIsInvisibleUniversalLeadEnyity(boolean value) {
        this.entityData.set(INVISIBLE_LEAD, value);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void initCustomDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(UNIVERSAL_LEAD, false);
        builder.define(INVISIBLE_LEAD, false);
    }
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    @Shadow
    public abstract void heal(float amount);

    @Shadow
    public abstract boolean isDeadOrDying();

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void preventTeamTargeting(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isAlliedTo(target) || EntityUtil.shouldBlockOwnedSummonDamage(self, target)) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void mobBattle$applyStatusEffectImmunity(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (effect.getEffect().equals(MobEffects.BLINDNESS) && self.hasEffect(ModEffects.BLINDNESS_IMMUNITY_FACTOR_ENTRY)) {
            cir.setReturnValue(false);
        }
        if (effect.getEffect().equals(MobEffects.DARKNESS) && self.hasEffect(ModEffects.DARKNESS_IMMUNITY_FACTOR_ENTRY)) {
            cir.setReturnValue(false);
        }
    }
    @Redirect(method = "canBeAffected", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 2))
    private boolean cancelCanHaveStatusEffect(EntityType<?> instance, TagKey<EntityType<?>> tag) {
        if (instance.is(EntityTypeTags.UNDEAD)) return false;
        return instance.is(tag);
    }
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
    private boolean cancelBaseTick(LivingEntity instance, ServerLevel world, DamageSource source, float amount) {
        if (instance.isPassenger() || instance.isVehicle()) {
            return false;
        } else {
            return instance.hurtServer(world, source, amount);
        }
    }
    //鐚伒鍗拌
    @Unique
    private long lastPigSpiritAbsorptionTime = -200L;
    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void pigDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        LivingEntity target = (LivingEntity) (Object) this;
        Entity attacker = source.getEntity();

        MobEffectInstance effect = target.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        if (effect == null) return;

        int amplifier = effect.getAmplifier();
        long currentTime = world.getGameTime();

        if (attacker instanceof AbstractPiglin piglin) {
            if (currentTime - this.lastPigSpiritAbsorptionTime >= 200L) {
                piglin.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, Math.max(0, amplifier), false, false));
                this.lastPigSpiritAbsorptionTime = currentTime;
            }
        } else if (attacker instanceof Player player && ArmorUtil.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)) {
            if (currentTime - this.lastPigSpiritAbsorptionTime >= 200L) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, Math.max(0, amplifier), false, false));
                this.lastPigSpiritAbsorptionTime = currentTime;
            }
        }
    }
    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 3))
    private boolean cancelDamage(DamageSource instance, TagKey<DamageType> tag) {
        Entity sourcer = instance.getDirectEntity();
        Entity attacker = instance.getEntity();

        if (sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) {
            return true;
        }
        return instance.is(tag);
    }
    @Unique
    boolean isMagic = false;
    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            index = 2,
            argsOnly = true
    )
    private DamageSource modifyDamageSource(DamageSource value) {
        isMagic = value.is(DamageTypeTags.WITCH_RESISTANT_TO);
        return value;
    }
    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            index = 3,
            argsOnly = true
    )
    private float modifyDamageArgument(float damage, @Local(argsOnly = true) DamageSource source) {
        LivingEntity target = (LivingEntity) (Object) this;
        if (!this.mobBattle$handlingExcitementBonus && mobBattle$isDirectMeleeDamage(source)) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                MobEffectInstance fatigue = livingAttacker.getEffect(ModEffects.FATIGUE_ENTRY);
                if (fatigue != null) {
                    int level = fatigue.getAmplifier() + 1;
                    if (target.getRandom().nextFloat() < level / 100.0F) {
                        return 0.0F;
                    }
                }
            }
        }
        if (target instanceof Creeper
                && (mobBattle$isNonPlayerEntityDamageSource(source.getDirectEntity())
                || mobBattle$isNonPlayerEntityDamageSource(source.getEntity()))) {
            damage += 30.0F;
        }
        // --- 閫昏緫2锛氭嫢鏈夊嵃璁扮殑鐢熺墿鍙楀埌鏉ヨ嚜鐚伒鐨勯澶栦激瀹?---
        if (target.hasEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
            MobEffectInstance effect = target.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
            int amplifier = effect != null ? effect.getAmplifier() : 0;
            float extraDamage = (float) (amplifier + 1);

            if (source.getEntity() instanceof AbstractPiglin) {
                damage += extraDamage;
            } else if (source.getEntity() instanceof Player player
                    && ArmorUtil.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)) {
                damage += extraDamage;
            }
        }
        int level = ArmorUtil.getMagicProtectionLevel((LivingEntity) (Object) this);
        if (isMagic && level > 0) {
            float reductionPercentage = Math.min(level * 0.04f, 0.80f);
            damage *= 1.0f - reductionPercentage;
        }
        return damage;
    }

    @Unique
    private boolean mobBattle$isNonPlayerEntityDamageSource(Entity entity) {
        return entity != null && !(entity instanceof Player) && !(entity instanceof Snowball) && !(entity instanceof ThrownEgg);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void damage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;
        Entity sourceEntity = source.getDirectEntity();
        Entity attacker = source.getEntity();
        if ((sourceEntity != null && EntityUtil.shouldBlockOwnedSummonDamage(sourceEntity, target))
                || (attacker != null && attacker != sourceEntity && EntityUtil.shouldBlockOwnedSummonDamage(attacker, target))) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && (Object) this instanceof Player player && (player.isCreative() || player.isSpectator())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "hurtServer", at = @At("RETURN"))
    public void damageReturn(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingEntity && attacker.getType().is(ModTags.ATTACK_HEAL_ENTITY) && this.isDeadOrDying()) {
            livingEntity.heal(5);
        }
        if (!cir.getReturnValue() || this.mobBattle$handlingExcitementBonus || !mobBattle$isDirectMeleeDamage(source)) {
            return;
        }
        if (attacker instanceof LivingEntity livingAttacker) {
            MobEffectInstance excitement = livingAttacker.getEffect(ModEffects.EXCITEMENT_ENTRY);
            if (excitement != null && livingAttacker.getRandom().nextFloat() < 0.20F) {
                LivingEntity target = (LivingEntity) (Object) this;
                int level = excitement.getAmplifier() + 1;
                this.mobBattle$handlingExcitementBonus = true;
                try {
                    target.hurtServer(world, source, 3.0F * level);
                } finally {
                    this.mobBattle$handlingExcitementBonus = false;
                }
            }
        }
    }
    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    public boolean isInvulnerableTo(LivingEntity instance, ServerLevel world, DamageSource source) {
        Entity sourcer = source.getDirectEntity();
        Entity attacker = source.getEntity();
        if ((sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) || source.is(DamageTypes.THORNS)) {
            instance.invulnerableTime = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }
    @Redirect(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    public void decrement(ItemStack instance, int amount) {
        if (instance.is(ModItems.IRON_GOLD_SWORD) && ((LivingEntity) (Object) this) instanceof Player player) {
            instance.hurtWithoutBreaking(5000, player);
        } else {
            instance.shrink(amount);
        }
    }
    @Unique
    public boolean isIronGoldSword = false;
    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void tryUseDeathProtector(DamageSource source, CallbackInfoReturnable<Boolean> cir, DeathProtection deathProtectionComponent, ItemStack itemStack2, InteractionHand[] var5, int var6, int var7, InteractionHand hand) {
        if (itemStack2.is(ModItems.IRON_GOLD_SWORD) && (!ArmorUtil.hasFullArmor((LivingEntity) (Object) this, ModMaterial.IRON_GOLD_INSTANCE) || itemStack2.getDamageValue() >= itemStack2.getMaxDamage() - 1)) {
            isIronGoldSword = false;
            cir.cancel();
            cir.setReturnValue(false);
        } else {
            isIronGoldSword = true;
        }
    }
    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    public void applyDeathEffects(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (isIronGoldSword) {
            this.heal(50);
            isIronGoldSword = false;
        }
    }
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    public void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
        if ((Object)this instanceof BaseSkillLittlePersonEntity skillEntity && skillEntity.allowsNormalAttackKnockback()) {
            return;
        }
        if ((Object)this instanceof Mob mob && mob.isNoAi()) {
            ci.cancel();
        }
    }
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEffectiveAi()Z", ordinal = 1))
    public boolean tickMovement(LivingEntity instance) {
        LivingEntity entity = (LivingEntity) (Object) this;
        String entityType = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).unwrapKey().get().location().getNamespace();
        if (Mob_battle.MOD_ID.equals(entityType) && entity instanceof Mob mob && mob.isNoAi()) {
            return true;
        }
        return instance.isEffectiveAi();
    }
    @Redirect(method = "handleRelativeFrictionAndCalculateMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    public void applyMovementInput(LivingEntity instance, MoverType type, Vec3 movement) {
        if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(instance.getType()).unwrapKey().isPresent()) {
            String entityType = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(instance.getType()).unwrapKey().get().location().getNamespace();
            if (Mob_battle.MOD_ID.equals(entityType) && instance instanceof Mob mob && mob.isNoAi()) {
                Vec3 vec3d = new Vec3(0, movement.y, 0);
                this.move(type, vec3d);
            } else {
                this.move(type, movement);
            }
        }
    }
    @ModifyArg(
            method = "getDamageAfterMagicAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"),
            index = 1
    )
    private float reduceEnchantmentProtection(float protection) {
        MobEffectInstance effect = ((LivingEntity) (Object) this).getEffect(ModEffects.ARMOR_PIERCING_ENTRY);
        if (effect != null) {
            int level = effect.getAmplifier() + 1;
            float newProtection = protection - (1.0f * level);
            newProtection = Math.max(0.0f, newProtection);
            return newProtection;
        }
        return protection;
    }

    @Unique
    private boolean mobBattle$isDirectMeleeDamage(DamageSource source) {
        Entity attacker = source.getEntity();
        return attacker instanceof LivingEntity
                && source.getDirectEntity() == attacker
                && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));
    }
}
