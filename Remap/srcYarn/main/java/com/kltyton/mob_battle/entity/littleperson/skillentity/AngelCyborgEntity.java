package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AngelCyborgEntity extends RequestedLittlePersonEntity {
    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    );

    public AngelCyborgEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 8);
        this.healPerSecond = 5.0F;
        this.autoSkillRange = 18.0D;
        setCooldownSeconds(12, 20, 10, 15, 20, 30, 18, 30);
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(12000.0D, 100.0D, 0.5D, 40.0D, 0.0D);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
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
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        updateBossBar();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        updateBossBar();
    }

    private void updateBossBar() {
        if (this.bossBar == null) {
            return;
        }

        this.bossBar.setPercent(Math.clamp(this.getHealth() / this.getMaxHealth(), 0.0F, 1.0F));
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    protected double skillRange(String skillName) {
        return "attack8".equals(skillName) ? 18.0D : 8.0D;
    }

    @Override
    protected void runAttack() {
        damageTarget(100.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> runAttack2();

            case 3 -> {
                if (phase == 0) {
                    runAttack3();
                } else if (phase == 1) {
                    runAttack3_1();
                }
            }

            case 4 -> runAttack4();

            case 5 -> runAttack5();

            case 6 -> runAttack6();

            case 7 -> runAttack7();

            case 8 -> runAttack8();

            case 9 -> {
                if (phase == 0) {
                    runAttack9();
                } else if (phase == 1) {
                    runAttack9_1();
                }
            }

            default -> {
            }
        }
    }

    /**
     * attack2:
     * 范围伤害 120
     */
    private void runAttack2() {
        areaDamage(4.0D, 120.0F, 0.0F);
    }

    /**
     * attack3 / runAttack3;
     * 给予目标中毒、反胃、凋零、饥饿、黑暗、穿甲、魔法穿甲 III 级 10 秒，
     * 并造成 50 魔法伤害。
     */
    private void runAttack3() {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }

        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 10 * 20, 2), this);
        target.addStatusEffect(new StatusEffectInstance(ModEffects.VOID_ARMOR_PIERCING_ENTRY, 10 * 20, 2), this);

        damageMagic(target, 50.0F);
    }

    /**
     * attack3 / runAttack3_1;
     * 把目标拉到自己面前，并造成 100 物理伤害。
     */
    private void runAttack3_1() {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }

        pullTargetToFront(target, 1.4D);
        damagePhysical(target, 100.0F);
    }

    /**
     * attack4:
     * 对面前敌人造成 50 法术范围伤害。
     */
    private void runAttack4() {
        coneDamage(4.0D, 100.0F, 0.0F, 50.0F);
    }

    /**
     * attack5:
     * 100 点范围伤害。
     */
    private void runAttack5() {
        areaDamage(4.0D, 100.0F, 0.0F);
    }

    /**
     * attack6:
     * 150 点范围伤害。
     */
    private void runAttack6() {
        areaDamage(5.0D, 150.0F, 0.0F);
    }

    /**
     * attack7:
     * 抓住控制敌人并造成 200 伤害。
     */
    private void runAttack7() {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }

        pullTargetToFront(target, 1.4D);
        target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 40, 0), this);
        damagePhysical(target, 200.0F);
    }

    /**
     * attack8:
     * 发射一个生化人同款子弹，造成 100 物理伤害。
     */
    private void runAttack8() {
        shootCyborgArrow();
    }

    /**
     * attack9 / runAttack9;
     * 向上跳起。
     */
    private void runAttack9() {
        this.setVelocity(this.getVelocity().x, 0.8D, this.getVelocity().z);
        this.velocityModified = true;
    }

    /**
     * attack9 / runAttack9_1;
     * 向下砸下，造成 300 范围伤害。
     */
    private void runAttack9_1() {
        areaDamage(6.0D, 300.0F, 0.0F);
    }

    private void shootCyborgArrow() {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target) || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }

        LittleArrowEntity arrow = new LittleArrowEntity(
                ModEntities.POISON_ARROW,
                world,
                this,
                new ItemStack(Items.ARROW),
                null
        );

        arrow.setDamage(100.0D);
        arrow.setTrueDamage(true, false);

        Vec3d velocity = target.getEyePos().subtract(this.getEyePos());
        arrow.setVelocity(velocity.x, velocity.y, velocity.z, 1.8F, 0.01F);

        world.spawnEntity(arrow);

        TaskSchedulerUtil.runLater(5 * 20, () -> {
            if (!arrow.isRemoved()) {
                arrow.discard();
            }
        });

        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.8F);
    }
}