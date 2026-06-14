package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ai.ZombieBowData;
import com.kltyton.mob_battle.entity.ai.ZombieBowAttackGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieBowEntityMixin extends Monster implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> MOB_BATTLE_BOW_USE_ENABLED =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);

    protected ZombieBowEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void mob_battle$defineBowUseData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MOB_BATTLE_BOW_USE_ENABLED, false);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mob_battle$equipModBowZombie(EntityType<? extends Zombie> entityType, Level level, CallbackInfo ci) {
        if (entityType == ModEntities.BOW_ZOMBIE_MOD) {
            this.entityData.set(MOB_BATTLE_BOW_USE_ENABLED, true);
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void mob_battle$keepModBowZombieEquipment(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (this.getType() == ModEntities.BOW_ZOMBIE_MOD) {
            this.entityData.set(MOB_BATTLE_BOW_USE_ENABLED, true);
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Inject(method = "addBehaviourGoals", at = @At("TAIL"))
    private void mob_battle$addBowAttackGoal(CallbackInfo ci) {
        int attackInterval = this.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
        this.goalSelector.addGoal(2, new ZombieBowAttackGoal<>(this, 1.0D, attackInterval, 15.0F));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void mob_battle$equipSpawnEggBow(ValueInput input, CallbackInfo ci) {
        boolean enabled = this.getType() == ModEntities.BOW_ZOMBIE_MOD
                || input.getBooleanOr(ZombieBowData.FORCE_BOW_KEY, false)
                || input.getBooleanOr(ZombieBowData.BOW_USE_ENABLED_KEY, false);
        this.entityData.set(MOB_BATTLE_BOW_USE_ENABLED, enabled);
        if (enabled) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void mob_battle$saveBowUseMode(ValueOutput output, CallbackInfo ci) {
        output.putBoolean(ZombieBowData.BOW_USE_ENABLED_KEY, this.entityData.get(MOB_BATTLE_BOW_USE_ENABLED));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ItemStack bow = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        ItemStack projectile = this.getProjectile(bow);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, projectile, power, bow);
        double x = target.getX() - this.getX();
        double y = target.getY(0.3333333333333333D) - arrow.getY();
        double z = target.getZ() - this.getZ();
        double horizontalDistance = Math.sqrt(x * x + z * z);

        if (this.level() instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileUsingShoot(
                    arrow,
                    serverLevel,
                    projectile,
                    x,
                    y + horizontalDistance * 0.2D,
                    z,
                    1.6F,
                    14 - serverLevel.getDifficulty().getId() * 4
            );
        }

        this.playSound(
                SoundEvents.SKELETON_SHOOT,
                1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

    @Override
    public boolean canUseNonMeleeWeapon(ItemStack item) {
        return this.entityData.get(MOB_BATTLE_BOW_USE_ENABLED) && item.is(Items.BOW);
    }
}
