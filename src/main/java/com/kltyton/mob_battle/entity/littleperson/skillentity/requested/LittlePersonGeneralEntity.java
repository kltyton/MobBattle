package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.soldier.LittlePersonSoldierArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.soldier.LittlePersonSoldierEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LittlePersonGeneralEntity extends RequestedTaskLittlePersonEntity {
    public LittlePersonGeneralEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        this.attackVariants = new String[]{"attack_1", "attack_2", "attack_3"};
        this.healPerSecond = 3.0F;
        this.blockChance = 30;
        this.blockDamageCap = 60.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(8, 20, 20, 20, 15);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(1500.0D, 55.0D, 0.55D, 40.0D, 0.20D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount % 40 == 0) {
            for (LivingEntity ally : EntityUtil.getNearbyEntity(this, LivingEntity.class, 3.0D, false, EntityUtil.TeamFilter.ONLY_TEAM)) {
                if (ally instanceof LittlePersonSoldierEntity || ally instanceof LittlePersonSoldierArcherEntity) {
                    ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 4 * 20, 1), this);
                    ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 4 * 20, 1), this);
                    ally.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 10 * 20, 8), this);
                }
            }
        }
    }

    @Override
    protected void runAttack() {
        damageTarget(55.0F, 0.0F);
    }

    @Override
    protected void runAttackVariant(int variant) {
        damageTarget(55.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(65.0F, 0.0F);
            case 3 -> {
                for (LivingEntity ally : EntityUtil.getNearbyEntity(this, LivingEntity.class, 8.0D, true, EntityUtil.TeamFilter.ONLY_TEAM)) {
                    if (ally == this || ally instanceof LittlePersonSoldierEntity || ally instanceof LittlePersonSoldierArcherEntity) {
                        ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 9), this);
                        ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 20, 4), this);
                        ally.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 20, 0), this);
                    }
                }
            }
            case 4 -> summonSoldiers();
            case 5 -> {
                LivingEntity target = this.getTarget();
                if (target != null && isValidSummonTarget(target)) {
                    this.teleportTo(target.getX(), target.getY() + 2.0D, target.getZ());
                    damagePhysical(target, 80.0F);
                    addPiercing(target, 5, 1);
                }
            }
            case 6 -> {
                lungeTowardTarget(1.6D, 1.35D);
                startMovingHitbox(18, 80.0F);
                areaDamage(3.0D, 80.0F, 0.0F);
            }
            default -> {
            }
        }
    }

    private void summonSoldiers() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            LittlePersonSoldierEntity soldier = ModEntities.LITTLE_PERSON_SOLDIER.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (soldier != null) {
                spawnAlly(world, soldier, i);
            }
            LittlePersonSoldierArcherEntity archer = ModEntities.LITTLE_PERSON_SOLDIER_ARCHER.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (archer != null) {
                spawnAlly(world, archer, i + 3);
            }
        }
    }

    private void spawnAlly(ServerLevel world, Mob entity, int index) {
        double angle = index * Math.PI / 3.0D;
        Vec3 pos = this.position().add(Math.cos(angle) * 2.0D, 0.0D, Math.sin(angle) * 2.0D);
        entity.snapTo(pos.x, pos.y, pos.z, this.getYRot(), this.getXRot());
        EntityUtil.joinSameTeam(entity, this);
        if (this.getTarget() != null) {
            entity.setTarget(this.getTarget());
        }
        world.addFreshEntity(entity);
    }
}
