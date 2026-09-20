package com.kltyton.mob_battle.mixin.ai.goal;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ai.ZombieBowAttackGoal;
import com.kltyton.mob_battle.entity.ai.ZombieBowData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieBowEntityMixin extends Monster implements RangedAttackMob {
    @Unique
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
            this.mob_battle$ensureDefaultBow();
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
        if (enabled && this.getMainHandItem().isEmpty()) {
            this.mob_battle$ensureDefaultBow();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void mob_battle$saveBowUseMode(ValueOutput output, CallbackInfo ci) {
        output.putBoolean(ZombieBowData.BOW_USE_ENABLED_KEY, this.entityData.get(MOB_BATTLE_BOW_USE_ENABLED));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ZombieBowAttackGoal.shootProjectiles(this, target, power);
    }

    @Unique
    private void mob_battle$ensureDefaultBow() {
        if (this.getMainHandItem().isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Override
    public boolean canUseNonMeleeWeapon(ItemStack item) {
        return this.entityData.get(MOB_BATTLE_BOW_USE_ENABLED) && item.getItem() instanceof BowItem;
    }
}
