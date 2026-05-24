package com.kltyton.mob_battle.entity.littleperson.skillentity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LivingGhostEntity extends RequestedLittlePersonEntity {
    private final ServerBossBar bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public LivingGhostEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 7);
        this.healPerSecond = 5.0F;
        this.blockChance = 15;
        this.blockDamageCap = -1.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(18, 16, 25, 18, 18, 20, 15);
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(20000.0D, 125.0D, 0.6D, 40.0D, 0.0D);
    }

    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        updateBossBar();
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        updateBossBar();
    }

    private void updateBossBar() {
        this.bossBar.setPercent(Math.max(0.0F, this.getHealth() / this.getMaxHealth()));
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
