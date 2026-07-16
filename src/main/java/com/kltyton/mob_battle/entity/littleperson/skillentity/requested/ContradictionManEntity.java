package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class ContradictionManEntity extends RequestedTaskLittlePersonEntity {
    public ContradictionManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        this.healPerSecond = 1.0F;
        this.blockChance = 30;
        this.blockDamageCap = 50.0F;
        this.autoSkillRange = 20.0D;
        setCooldownSeconds(8, 15, 5, 10);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(1800.0D, 30.0D, 0.55D, 40.0D, 0.0D);
    }

    @Override
    protected double skillRange(String skillName) {
        return "attack4".equals(skillName) ? 30.0D : 12.0D;
    }

    @Override
    protected void runAttack() {
        damageTarget(30.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                LivingEntity target = this.getTarget();
                damageTarget(25.0F, 0.0F);
                if (target != null && isValidSummonTarget(target)) {
                    target.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 20, 0), this);
                }
            }
            case 3 -> {
                dashForward(phase == 0 ? 0.8D : -0.6D);
                forwardBoxDamage(6.0D, 2.25D, 2.0D, 30.0F, 0.0F);
            }
            case 4 -> shootSpearAtTarget(35.0F);
            case 5 -> {
                for (LivingEntity ally : alliedLittlePersons(3.0D, true)) {
                    ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 1), this);
                }
            }
            default -> {
            }
        }
    }
}
