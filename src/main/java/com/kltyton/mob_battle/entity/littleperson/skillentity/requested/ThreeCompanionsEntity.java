package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThreeCompanionsEntity extends RequestedTaskLittlePersonEntity {
    private int normalHits;
    private int attackReductionTicks;

    public ThreeCompanionsEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 2);
        this.healPerSecond = 3.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(0, 10);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(6000.0D, 60.0D, 0.55D, 40.0D, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackReductionTicks > 0) {
            this.attackReductionTicks--;
        }
    }

    @Override
    protected boolean canUseSkill(String skillName, net.minecraft.world.entity.LivingEntity target) {
        return !"attack2".equals(skillName) && super.canUseSkill(skillName, target);
    }

    @Override
    protected void runAttack() {
        this.attackReductionTicks = 20;
        damageTarget(60.0F, 0.0F);
        if (++this.normalHits >= 8) {
            this.normalHits = 0;
            runSkill(2, 0);
        }
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                if (this.level() instanceof ServerLevel world) {
                    world.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1.0D, this.getZ(), 12, 1.0D, 0.6D, 1.0D, 0.0D);
                    world.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.2F, 0.9F);
                }
                areaDamage(4.0D, 90.0F, 0.0F);
            }
            case 3 -> damageTarget(90.0F, 0.0F);
            default -> {
            }
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel world, @NotNull DamageSource source, float amount) {
        if (this.attackReductionTicks > 0) {
            amount *= 0.7F;
        }
        return super.hurtServer(world, source, amount);
    }
}
