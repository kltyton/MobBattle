package com.kltyton.mob_battle.entity.littleperson.archer.soldier;

import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonSoldierArcherEntity extends LittlePersonArcherEntity {
    protected static final RawAnimation MOVE_ANIM = RawAnimation.begin().thenPlayAndHold("move");
    public LittlePersonSoldierArcherEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.ATTACK_DAMAGE, 35.0);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        // 先执行原有的伤害逻辑
        boolean isDamaged = super.damage(world, source, amount);

        // 如果伤害成功触发，且技能不在冷却中，且攻击者是生物
        if (isDamaged && !this.getWorld().isClient) {
            if (source.getAttacker() instanceof LivingEntity attacker) {
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
        Vec3d lookDir = this.getRotationVector();
        Vec3d backwardDir = new Vec3d(-lookDir.x, 0, -lookDir.z).normalize().multiply(2.0);

        // 使用 move 确保物理碰撞，不穿墙
        this.move(MovementType.SELF, backwardDir);

        // 增加一点位移初速度，让动作看起来更丝滑
        this.setVelocity(backwardDir.multiply(0.2));
        this.velocityDirty = true;

        // 2. 触发动画
        this.triggerAnim("attack_controller", "skill");

        // 3. 立即向攻击者反击
        // 调用你现有的 shootAt 方法
        this.shootAt(attacker, 1.0F);

        // 4. 可选：播放一个闪避音效
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.5F);
    }
}
