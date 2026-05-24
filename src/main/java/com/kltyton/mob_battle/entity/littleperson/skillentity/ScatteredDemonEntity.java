package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ScatteredDemonEntity extends RequestedLittlePersonEntity {
    private boolean clone;

    public ScatteredDemonEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 5);
        this.healPerSecond = 3.0F;
        this.autoSkillRange = 10.0D;
        setCooldownSeconds(20, 30, 40, 15, 30);
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(3800.0D, 60.0D, 0.5D, 40.0D, 0.0D);
    }

    @Override
    protected boolean canUseSkill(String skillName, LivingEntity target) {
        return !(this.clone && "attack6".equals(skillName)) && super.canUseSkill(skillName, target);
    }

    @Override
    protected double skillRange(String skillName) {
        return "attack4".equals(skillName) ? 10.0D : super.skillRange(skillName);
    }

    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        if (isEnvironmentDamage(source)) {
            return false;
        }
        if (this.random.nextInt(100) < 10) {
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
            this.triggerAnim("attack_controller", "block");
            return true;
        }
        if (amount <= 100.0F && this.random.nextInt(100) < 20) {
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
            this.triggerAnim("skill_controller", "block_1");
            return true;
        }
        return false;
    }

    @Override
    protected void runAttack() {
        for (LivingEntity target : nearestTargets(5.0D, 3)) {
            damagePhysical(target, 60.0F);
        }
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> forwardBoxDamage(2.0D, 2.0D, 2.0D, 88.0F, 0.0F);
            case 3 -> {
                this.skillDamageReductionTicks = 35;
                if (phase == 0) {
                    areaDamage(6.0D, 80.0F, 0.0F);
                } else {
                    areaDamage(10.0D, 100.0F, 0.0F);
                }
            }
            case 4 -> {
                if (phase == 0) {
                    this.setAiDisabled(false);
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 7 * 20, 0, false, false));
                    startMovingHitbox(120, 80.0F);
                } else {
                    this.setAiDisabled(true);
                    this.movingDamageTicks = 0;
                    areaDamage(3.0D, 100.0F, 0.0F);
                }
            }
            case 5 -> areaDamage(3.0D, 80.0F, 0.0F);
            case 6 -> summonClones();
            default -> {
            }
        }
    }

    private void summonClones() {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            ScatteredDemonEntity cloneEntity = ModEntities.SCATTERED_DEMON.create(world, SpawnReason.MOB_SUMMONED);
            if (cloneEntity == null) {
                continue;
            }
            Vec3d pos = this.getPos().add((this.random.nextDouble() - 0.5D) * 2.0D, 0.0D, (this.random.nextDouble() - 0.5D) * 2.0D);
            cloneEntity.refreshPositionAndAngles(pos.x, pos.y, pos.z, this.getYaw(), this.getPitch());
            cloneEntity.clone = true;
            cloneEntity.lifeTicks = 60 * 20;
            cloneEntity.setSummonOwner(this);
            cloneEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(500.0D);
            cloneEntity.setHealth(500.0F);
            world.spawnEntity(cloneEntity);
        }
    }
}
