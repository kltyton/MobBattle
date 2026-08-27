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
    /** 服务端是否已发起出生流程（首次服务器 tick 置位，不会重复触发）。 */
    private boolean spawned;
    /** 出生动画完成信号是否已被消费；消费后本实体不再接受 {@code spawn} 指令。 */
    private boolean spawnCompleted;

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

    /**
     * 服务端技能指令分发契约（精英守卫）。
     *
     * <p>额外识别出生动画完成信号 {@code spawn}：该信号已被策略层限定在“服务端已
     * 发起出生流程且完成信号尚未消费”的合法状态，此处消费标记后触发一次范围伤害
     * 并返回 {@code true}；重复的 {@code spawn} 会因 {@link #canAcceptSpawnCommand()}
     * 返回 {@code false} 被策略层拒绝，不会再次进入本方法。其余指令全部委托给父级
     * {@code RequestedLittlePersonEntity} 的既有实现。
     */
    @Override
    public boolean handleSkillPayload(String skillName) {
        if ("spawn".equals(skillName)) {
            this.spawnCompleted = true;
            areaDamage(3.0D, 80.0F, 0.0F);
            return true;
        }
        return super.handleSkillPayload(skillName);
    }

    /**
     * 出生收尾指令的实体侧准入：仅当服务端已发起出生流程（{@code spawned}）、技能
     * 流程仍处于活跃状态（{@code hasSkill()}）且完成信号尚未消费
     * （{@code !spawnCompleted}）时返回 {@code true}。策略层还会再次校验发送方跟踪
     * 与技能活跃状态，因此本方法只编码实体自身可达的合法状态，出生指令不会泛化到
     * 其他实体。
     */
    @Override
    public boolean canAcceptSpawnCommand() {
        return this.spawned && !this.spawnCompleted && this.hasSkill();
    }
}
