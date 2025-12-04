package com.kltyton.mob_battle.mixin.undead;

import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullEntityKing;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.utils.IronGoldArmorUtil;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract void heal(float amount);

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
    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 3))
    private boolean cancelDamage(DamageSource instance, TagKey<DamageType> tag) {
        Entity sourcer = instance.getSource();
        Entity attacker = instance.getAttacker();
        if (sourcer instanceof WitherSkullEntityKing && attacker instanceof WitherSkullEntityKing) {
            return true;
        }
        return instance.isIn(tag);
    }
    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean isInvulnerableTo(LivingEntity instance, ServerWorld world, DamageSource source) {
        Entity sourcer = source.getSource();
        Entity attacker = source.getAttacker();
        if (sourcer instanceof WitherSkullEntityKing && attacker instanceof WitherSkullEntityKing) {
            instance.timeUntilRegen = 0;
            return false;
        }
        return instance.isInvulnerableTo(world, source);
    }
    @Redirect(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void decrement(ItemStack instance, int amount) {
        if (instance.isOf(ModItems.IRON_GOLD_SWORD) && ((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            instance.damage(500, player);
        } else {
            instance.decrement(amount);
        }
    }
    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void tryUseDeathProtector(DamageSource source, CallbackInfoReturnable<Boolean> cir, DeathProtectionComponent deathProtectionComponent, ItemStack itemStack2, Hand[] var5, int var6, int var7, Hand hand) {
        if (itemStack2.isOf(ModItems.IRON_GOLD_SWORD) && !IronGoldArmorUtil.hasFullDiamondArmor((LivingEntity) (Object) this)) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/DeathProtectionComponent;applyDeathEffects(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)V", shift = At.Shift.AFTER))
    public void applyDeathEffects(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        this.heal(50);
    }
}
