package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import java.util.UUID;

public class MacroSamuraiEntity extends RequestedTaskLittlePersonEntity {
    private static final double ATTACK_RANGE_SCALE = 1.5D;
    private static final float ANIMATION_DAMAGE_REDUCTION = 30.0F;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            UUID.randomUUID(),
            Component.translatable("entity.mob_battle.macro_samurai"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public MacroSamuraiEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        this.attackVariants = new String[]{"attack_1", "attack_2", "attack_3", "attack_4", "attack_5"};
        this.healPerSecond = 5.0F;
        this.blockChance = 30;
        this.blockDamageCap = 200.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(18, 20, 25, 50, 60);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(20000.0D, 160.0D, 0.5D, 40.0D, 0.0D);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        return !this.hasSkill() && this.distanceTo(target) <= scaledRange(4.0D);
    }

    @Override
    protected double skillRange(String skillName) {
        return scaledRange("attack6".equals(skillName) ? 8.5D : 4.5D);
    }

    @Override
    protected void runAttack() {
        runAttackVariant(1);
    }

    @Override
    protected void runAttackVariant(int variant) {
        switch (variant) {
            case 2, 3 -> areaDamage(scaledRange(2.5D), 120.0F, 0.0F);
            case 4 -> forwardBoxDamage(scaledRange(2.0D), scaledRange(2.0D), scaledRange(2.0D), 120.0F, 0.0F);
            case 5 -> coneDamage(scaledRange(3.5D), 180.0F, 160.0F, 0.0F);
            default -> damageTarget(160.0F, 0.0F);
        }
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> forwardBoxDamage(scaledRange(2.0D), scaledRange(3.0D), scaledRange(2.0D), 250.0F, 0.0F);
            case 3 -> forwardBoxDamage(scaledRange(2.0D), scaledRange(5.0D), scaledRange(2.0D), 300.0F, 0.0F);
            case 4 -> forwardBoxDamage(scaledRange(2.0D), scaledRange(3.0D), scaledRange(2.0D), phase > 0 ? 250.0F : 180.0F, 0.0F);
            case 5 -> forwardBoxDamage(scaledRange(2.0D), scaledRange(3.0D), scaledRange(2.0D), 250.0F, 0.0F);
            case 6 -> areaDamage(scaledRange(7.5D), 420.0F, 0.0F);
            default -> {
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (this.hasSkill()) {
            amount = Math.max(0.0F, amount - ANIMATION_DAMAGE_REDUCTION);
            if (amount <= 0.0F) {
                return false;
            }
        }
        return super.hurtServer(world, source, amount);
    }

    private static double scaledRange(double range) {
        return range * ATTACK_RANGE_SCALE;
    }
}
