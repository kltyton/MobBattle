package com.kltyton.mob_battle.entity.littleperson.archer.soldier;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonSoldierArcherEntity extends LittlePersonArcherEntity {
    protected static final RawAnimation MOVE_ANIM = RawAnimation.begin().thenPlayAndHold("move");
    public LittlePersonSoldierArcherEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 35.0);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        // 先执行原有的伤害逻辑
        boolean isDamaged = super.hurtServer(world, source, amount);

        // 如果伤害成功触发，且技能不在冷却中，且攻击者是生物
        if (isDamaged && !this.level().isClientSide) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                performEscapeAndCounter(attacker);
            }
        }
        return isDamaged;
    }
    @Override
    public PlayState mainController(final AnimationTest<LittlePersonArcherEntity> event) {
        return event.isMoving() ? event.setAndContinue(MOVE_ANIM) : event.setAndContinue(IDLE_ANIM);
    }

    private void performEscapeAndCounter(LivingEntity attacker) {
        // 1. 向后移动逻辑
        // 获取水平朝向向量（忽略 Y 轴，防止飞天或钻地）
        Vec3 lookDir = this.getLookAngle();
        Vec3 backwardDir = new Vec3(-lookDir.x, 0, -lookDir.z).normalize().scale(2.0);

        // 使用 move 确保物理碰撞，不穿墙
        this.move(MoverType.SELF, backwardDir);

        // 增加一点位移初速度，让动作看起来更丝滑
        this.setDeltaMovement(backwardDir.scale(0.2));
        this.hasImpulse = true;

        // 2. 触发动画
        this.triggerAnim("attack_controller", "skill");

        // 3. 立即向攻击者反击
        // 调用你现有的 shootAt 方法
        this.performRangedAttack(attacker, 1.0F);

        // 4. 可选：播放一个闪避音效
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.5F);
    }
}
