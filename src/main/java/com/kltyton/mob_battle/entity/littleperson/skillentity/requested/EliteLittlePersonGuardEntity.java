package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class EliteLittlePersonGuardEntity extends RequestedTaskLittlePersonEntity {
    private boolean spawned;

    public EliteLittlePersonGuardEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 2);
        this.healPerSecond = 1.0F;
        this.blockChance = 30;
        this.blockDamageCap = 60.0F;
        this.autoSkillRange = 5.0D;
        setCooldownSeconds(15, 20);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(1500.0D, 50.0D, 0.50D, 40.0D, 0.20D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        if (!this.spawned) {
            this.spawned = true;
            this.setHasSkill(true);
            this.setNoAi(true);
            this.triggerAnim("skill_controller", "spawn");
        }
        LivingEntity owner = this.getSummonOwner();
        if (owner != null && owner.isAlive() && this.tickCount % 20 == 0) {
            owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 2), this);
            owner.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 5 * 20, 0), this);
        }
    }

    @Override
    protected void runAttack() {
        int hit = 0;
        for (LivingEntity target : getForwardBoxTargets(2.0D, 3.0D, 2.0D)) {
            damagePhysical(target, 50.0F);
            if (++hit >= 3) {
                break;
            }
        }
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> areaDamage(3.0D, 60.0F, 0.0F);
            case 3 -> damageTarget(80.0F, 0.0F);
            default -> {
            }
        }
    }

    @Override
    public boolean handleSkillPayload(String skillName) {
        if ("spawn".equals(skillName)) {
            areaDamage(3.0D, 80.0F, 0.0F);
            return true;
        }
        return super.handleSkillPayload(skillName);
    }
}
