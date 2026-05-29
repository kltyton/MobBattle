package com.kltyton.mob_battle.entity.littleperson.skillentity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LivingGhostEntity extends RequestedLittlePersonEntity {
    private final ServerBossEvent bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public LivingGhostEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 7);
        this.healPerSecond = 5.0F;
        this.blockChance = 15;
        this.blockDamageCap = -1.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(18, 16, 25, 18, 18, 20, 15);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(20000.0D, 125.0D, 0.6D, 40.0D, 0.0D);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        updateBossBar();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        updateBossBar();
    }

    private void updateBossBar() {
        this.bossBar.setProgress(Math.max(0.0F, this.getHealth() / this.getMaxHealth()));
        this.bossBar.setName(this.getDisplayName().copy().append(" | " + (int) this.getHealth() + "/" + (int) this.getMaxHealth()));
    }

    @Override
    protected void runAttack() {
        damageTarget(125.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> coneDamage(4.0D, 180.0F, 150.0F, 0.0F);
            case 3 -> areaDamage(4.0D, 150.0F, 20.0F);
            case 4 -> areaDamage(6.0D, 180.0F, 0.0F);
            case 5 -> {
                if (phase == 0) {
                    pullNearbyTargets(7.0D, 2.0D);
                    areaDamage(7.0D, 120.0F, 0.0F);
                } else {
                    areaDamage(7.0D, 180.0F, 0.0F);
                }
            }
            case 6 -> {
                if (phase == 0) {
                    areaDamage(2.5D, 120.0F, 0.0F);
                } else {
                    forwardBoxDamage(2.0D, 2.0D, 2.0D, 180.0F, 0.0F);
                }
            }
            case 7, 8 -> forwardBoxDamage(3.0D, 3.0D, 3.0D, 150.0F, 0.0F);
            default -> {
            }
        }
    }
}
