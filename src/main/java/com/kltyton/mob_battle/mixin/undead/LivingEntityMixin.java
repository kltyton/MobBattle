package com.kltyton.mob_battle.mixin.undead;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullKingEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.tags.ModTags;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = ILead.class, prefix = "custom$"))
public abstract class LivingEntityMixin extends Entity implements Attackable, ServerWaypoint {
    @Unique
    private static final TrackedData<Boolean> UNIVERSAL_LEAD = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Boolean> INVISIBLE_LEAD = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    public boolean custom$getIsUniversalLeadEnyity() {
        return this.dataTracker.get(UNIVERSAL_LEAD);
    }

    @Unique
    public void custom$setIsUniversalLeadEnyity(boolean value) {
        this.dataTracker.set(UNIVERSAL_LEAD, value);
    }

    @Unique
    public boolean custom$getIsInvisibleUniversalLeadEnyity() {
        return this.dataTracker.get(INVISIBLE_LEAD);
    }

    @Unique
    public void custom$setIsInvisibleUniversalLeadEnyity(boolean value) {
        this.dataTracker.set(INVISIBLE_LEAD, value);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(UNIVERSAL_LEAD, false);
        builder.add(INVISIBLE_LEAD, false);
    }
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Shadow
    public abstract void heal(float amount);

    @Shadow
    public abstract boolean isDead();

    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void preventTeamTargeting(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isTeammate(target)) {
            cir.setReturnValue(false);
        }
    }
    @Redirect(method = "canHaveStatusEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 2))
    private boolean cancelCanHaveStatusEffect(EntityType<?> instance, TagKey<EntityType<?>> tag) {
        if (instance.isIn(EntityTypeTags.UNDEAD)) return false;
        return instance.isIn(tag);
    }
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
    private boolean cancelBaseTick(LivingEntity instance, ServerWorld world, DamageSource source, float amount) {
        if (instance.hasVehicle() || instance.hasPassengers()) {
            return false;
        } else {
            return instance.damage(world, source, amount);
        }
    }
    //猪灵印记
    @Unique
    private long lastPigSpiritAbsorptionTime = -200L;
    @Inject(method = "damage", at = @At("RETURN"))
    private void pigDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {
            if (livingAttacker.hasStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                if (target instanceof AbstractPiglinEntity piglin) {
                    StatusEffectInstance effect = livingAttacker.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                    int amplifier = effect != null ? effect.getAmplifier() : 0;
                    long currentTime = target.getWorld().getTime();
                    if (currentTime - lastPigSpiritAbsorptionTime >= 200L) {
                        float absorptionAmount = 2.0f * (amplifier + 1);
                        piglin.setAbsorptionAmount(piglin.getAbsorptionAmount() + absorptionAmount);
                        lastPigSpiritAbsorptionTime = currentTime;
                    }
                } else if (target instanceof PlayerEntity player && ArmorUtil.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)){
                    StatusEffectInstance effect = livingAttacker.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                    int amplifier = effect != null ? effect.getAmplifier() : 0;
                    long currentTime = target.getWorld().getTime();
                    if (currentTime - lastPigSpiritAbsorptionTime >= 200L) {
                        float absorptionAmount = 2.0f * (amplifier + 1);
                        player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionAmount);
                        lastPigSpiritAbsorptionTime = currentTime;
                    }
                }
            }
        }
    }
    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 3))
    private boolean cancelDamage(DamageSource instance, TagKey<DamageType> tag) {
        Entity sourcer = instance.getSource();
        Entity attacker = instance.getAttacker();

        if (sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) {
            return true;
        }
        return instance.isIn(tag);
    }
    @Unique
    boolean isMagic = false;
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            index = 2,
            argsOnly = true
    )
    private DamageSource modifyDamageSource(DamageSource value) {
        isMagic = value.isIn(DamageTypeTags.WITCH_RESISTANT_TO);
        return value;
    }
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            index = 3,
            argsOnly = true
    )
    private float modifyDamageArgument(float damage, @Local(argsOnly = true) DamageSource source) {
        LivingEntity target = (LivingEntity) (Object) this;
        // --- 逻辑2：拥有印记的生物受到来自猪灵的额外伤害 ---
        if (target.hasStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
            if (source.getAttacker() instanceof AbstractPiglinEntity) {
                StatusEffectInstance effect = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                int amplifier = effect != null ? effect.getAmplifier() : 0;
                float extraDamage = (float) (amplifier + 1);
                damage += extraDamage;
            } else if (target instanceof PlayerEntity player && ArmorUtil.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)) {
                StatusEffectInstance effect = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                int amplifier = effect != null ? effect.getAmplifier() : 0;
                float extraDamage = (float) (amplifier + 1);
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
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD) && (Object) this instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "damage", at = @At("RETURN"))
    public void damageReturn(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingEntity && attacker.getType().isIn(ModTags.ATTACK_HEAL_ENTITY) && this.isDead()) {
            livingEntity.heal(5);
        }
    }
    //无敌帧
    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean isInvulnerableTo(LivingEntity instance, ServerWorld world, DamageSource source) {
        Entity sourcer = source.getSource();
        Entity attacker = source.getAttacker();
        if ((sourcer instanceof WitherSkullKingEntity && attacker instanceof WitherSkullKingEntity) || source.isOf(DamageTypes.THORNS) || (source.isOf(DamageTypes.ON_FIRE) && !this.isFireImmune())) {
            instance.timeUntilRegen = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }
    @Redirect(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void decrement(ItemStack instance, int amount) {
        if (instance.isOf(ModItems.IRON_GOLD_SWORD) && ((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            instance.damage(5000, player);
        } else {
            instance.decrement(amount);
        }
    }
    @Unique
    public boolean isIronGoldSword = false;
    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void tryUseDeathProtector(DamageSource source, CallbackInfoReturnable<Boolean> cir, DeathProtectionComponent deathProtectionComponent, ItemStack itemStack2, Hand[] var5, int var6, int var7, Hand hand) {
        if (itemStack2.isOf(ModItems.IRON_GOLD_SWORD) && (!ArmorUtil.hasFullArmor((LivingEntity) (Object) this, ModMaterial.IRON_GOLD_INSTANCE) || itemStack2.getDamage() >= itemStack2.getMaxDamage() - 1)) {
            isIronGoldSword = false;
            cir.cancel();
            cir.setReturnValue(false);
        } else {
            isIronGoldSword = true;
        }
    }
    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    public void applyDeathEffects(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (isIronGoldSword) {
            this.heal(50);
            isIronGoldSword = false;
        }
    }
    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    public void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
        if ((Object)this instanceof MobEntity mob && mob.isAiDisabled()) {
            ci.cancel();
        }
    }
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;canActVoluntarily()Z", ordinal = 1))
    public boolean tickMovement(LivingEntity instance) {
        LivingEntity entity = (LivingEntity) (Object) this;
        String entityType = Registries.ENTITY_TYPE.getEntry(entity.getType()).getKey().get().getValue().getNamespace();
        if (Mob_battle.MOD_ID.equals(entityType) && entity instanceof MobEntity mob && mob.isAiDisabled()) {
            return true;
        }
        return instance.canActVoluntarily();
    }
    @Redirect(method = "applyMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    public void applyMovementInput(LivingEntity instance, MovementType type, Vec3d movement) {
        if (Registries.ENTITY_TYPE.getEntry(instance.getType()).getKey().isPresent()) {
            String entityType = Registries.ENTITY_TYPE.getEntry(instance.getType()).getKey().get().getValue().getNamespace();
            if (Mob_battle.MOD_ID.equals(entityType) && instance instanceof MobEntity mob && mob.isAiDisabled()) {
                Vec3d vec3d = new Vec3d(0, movement.y, 0);
                this.move(type, vec3d);
            } else {
                this.move(type, movement);
            }
        }
    }
    @ModifyArg(
            method = "modifyAppliedDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getInflictedDamage(FF)F"),
            index = 1
    )
    private float reduceEnchantmentProtection(float protection) {
        StatusEffectInstance effect = ((LivingEntity) (Object) this).getStatusEffect(ModEffects.ARMOR_PIERCING_ENTRY);
        if (effect != null) {
            int level = effect.getAmplifier() + 1;
            float newProtection = protection - (1.0f * level);
            newProtection = Math.max(0.0f, newProtection);
            return newProtection;
        }
        return protection;
    }
}