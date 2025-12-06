package com.kltyton.mob_battle.entity.drone.attackdrone;

import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.entity.drone.goal.FlyRangedAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AttackDroneEntity extends DroneEntity {

    public enum CombatMode {
        PASSIVE("被动", "§7"),
        NEUTRAL("中立", "§e"),
        AGGRESSIVE("主动", "§c");

        private final String name;
        private final String color;

        CombatMode(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public CombatMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public String getDisplayName() {
            return color + name;
        }
    }
    private CombatMode combatMode = CombatMode.AGGRESSIVE; // 默认主动模式
    public CombatMode getCombatMode() {
        return combatMode;
    }
    public void setCombatMode(CombatMode mode) {
        if (this.combatMode != mode) {
            this.combatMode = mode;
            if (!this.getWorld().isClient) {
                updateTargetGoals(); // 切换模式时重新注册目标选择器
            }
        }
    }
    public void cycleCombatMode() {
        setCombatMode(combatMode.next());
    }
    private void updateTargetGoals() {
        // 先清空所有与攻击相关的目标选择器
        this.targetSelector.getGoals().removeIf(goal -> goal.getGoal() instanceof TrackOwnerAttackerGoal ||
                goal.getGoal() instanceof AttackWithOwnerGoal ||
                goal.getGoal() instanceof RevengeGoal ||
                goal.getGoal() instanceof ActiveTargetGoal);

        switch (combatMode) {
            case PASSIVE:
                break;
            case NEUTRAL:
                this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
                this.targetSelector.add(2, new AttackWithOwnerGoal(this));
                this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
                break;
            case AGGRESSIVE:
                this.targetSelector.add(3, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, (target, world) -> {
                    if (target == this) return false;
                    if (target == this.getOwner()) return false;
                    if (target instanceof TameableEntity tameableEntity && tameableEntity.getOwner() == this.getOwner()) {
                        return false;
                    }
                    return !this.isTeammate(target);
                }));
                break;
        }
    }
    public AttackDroneEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        // 参数：实体，移动速度倍率，最小攻击间隔(tick)，最大攻击间隔(tick)，最大射程
        this.goalSelector.add(3, new FlyRangedAttackGoal(this, 1.5D, 20, 25, 16.0F));
    }

    private boolean hasInitializedGoals = false;

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && !hasInitializedGoals) {
            updateTargetGoals(); // 第一次 tick 时确保目标已正确设置
            hasInitializedGoals = true;
        }
    }
    // 4. 远程攻击实现 (类似骷髅)
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        BulletEntity bullet = new BulletEntity(this.getWorld(), this,
                new ItemStack(Items.IRON_BLOCK), null);

        // 完全精准预瞄（考虑目标移动速度）
        Vec3d targetVel = target.getVelocity();
        double predictTicks = 20; // 根据子弹速度调整（4.0速度 ≈ 0.2秒到达20格）
        Vec3d predictedPos = target.getPos().add(targetVel.multiply(predictTicks * 0.05));

        double dx = predictedPos.x - this.getX();
        double dy = predictedPos.getY() + target.getHeight() * 0.7 - this.getEyeY(); // 瞄胸口更稳
        double dz = predictedPos.z - this.getZ();

        // 发射点：从眼睛正前方0.5格发出（视觉上最自然）
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        double offsetX = dx / horizontalLength * 0.5;
        double offsetZ = dz / horizontalLength * 0.5;

        bullet.setPosition(
                this.getX() + offsetX,
                this.getEyeY() - 0.3,   // 稍微低一点点对齐枪口动画
                this.getZ() + offsetZ
        );

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = 5.0D; // 推荐调到5.0，更快更准

        bullet.setVelocity(
                dx / distance * speed,
                dy / distance * speed,
                dz / distance * speed
        );
        bullet.setTrueDamage(true);
        bullet.setDamage(300.0D);
        this.getWorld().spawnEntity(bullet);
        this.triggerAnim("attack_controller", "attack");
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString("CombatMode", combatMode.name());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        if (view.contains("CombatMode")) {
            try {
                this.combatMode = CombatMode.valueOf(view.getString("CombatMode", CombatMode.AGGRESSIVE.name()));
            } catch (IllegalArgumentException e) {
                this.combatMode = CombatMode.AGGRESSIVE;
            }
        }
        // 读取完后也要更新一次目标（防止重生后目标丢失）
        if (!this.getWorld().isClient) {
            this.updateTargetGoals();
        }
    }
}
