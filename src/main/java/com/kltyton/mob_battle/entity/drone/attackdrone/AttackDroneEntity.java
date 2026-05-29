package com.kltyton.mob_battle.entity.drone.attackdrone;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.entity.drone.goal.FlyRangedAttackGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

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
            if (!this.level().isClientSide) {
                updateTargetGoals(); // 切换模式时重新注册目标选择器
            }
        }
    }
    public void cycleCombatMode() {
        setCombatMode(combatMode.next());
    }
    private void updateTargetGoals() {
        // 先清空所有与攻击相关的目标选择器
        this.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof OwnerHurtByTargetGoal ||
                goal.getGoal() instanceof OwnerHurtTargetGoal ||
                goal.getGoal() instanceof HurtByTargetGoal ||
                goal.getGoal() instanceof NearestAttackableTargetGoal);

        switch (combatMode) {
            case PASSIVE:
                break;
            case NEUTRAL:
                this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
                this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
                this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
                break;
            case AGGRESSIVE:
                this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (target, world) -> {
                    if (target == this) return false;
                    if (target == this.getOwner()) return false;
                    if (target instanceof TamableAnimal tameableEntity && tameableEntity.getOwner() == this.getOwner()) {
                        return false;
                    }
                    return !this.isAlliedTo(target);
                }));
                break;
        }
    }
    public AttackDroneEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 参数：实体，移动速度倍率，最小攻击间隔(tick)，最大攻击间隔(tick)，最大射程
        this.goalSelector.addGoal(3, new FlyRangedAttackGoal(this, 1.5D, 20, 25, 16.0F));
    }

    private boolean hasInitializedGoals = false;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !hasInitializedGoals) {
            updateTargetGoals(); // 第一次 tick 时确保目标已正确设置
            hasInitializedGoals = true;
        }
    }
    // 4. 远程攻击实现 (类似骷髅)
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!ModSkillEntityType.canSkill(this)) return;

        BulletEntity bullet = new BulletEntity(this.level(), this,
                new ItemStack(Items.IRON_BLOCK), null);

        // 完全精准预瞄（考虑目标移动速度）
        Vec3 targetVel = target.getDeltaMovement();
        double predictTicks = 20; // 根据子弹速度调整（4.0速度 ≈ 0.2秒到达20格）
        Vec3 predictedPos = target.position().add(targetVel.scale(predictTicks * 0.05));

        double dx = predictedPos.x - this.getX();
        double dy = predictedPos.y() + target.getBbHeight() * 0.7 - this.getEyeY(); // 瞄胸口更稳
        double dz = predictedPos.z - this.getZ();

        // 发射点：从眼睛正前方0.5格发出（视觉上最自然）
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        double offsetX = dx / horizontalLength * 0.5;
        double offsetZ = dz / horizontalLength * 0.5;

        bullet.setPos(
                this.getX() + offsetX,
                this.getEyeY() - 0.3,   // 稍微低一点点对齐枪口动画
                this.getZ() + offsetZ
        );

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = 5.0D; // 推荐调到5.0，更快更准

        bullet.setDeltaMovement(
                dx / distance * speed,
                dy / distance * speed,
                dz / distance * speed
        );
        bullet.setTrueDamage(true, false);
        bullet.setBaseDamage(300.0D);
        this.level().addFreshEntity(bullet);
        this.triggerAnim("attack_controller", "attack");
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putString("CombatMode", combatMode.name());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        if (view.contains("CombatMode")) {
            try {
                this.combatMode = CombatMode.valueOf(view.getStringOr("CombatMode", CombatMode.AGGRESSIVE.name()));
            } catch (IllegalArgumentException e) {
                this.combatMode = CombatMode.AGGRESSIVE;
            }
        }
        // 读取完后也要更新一次目标（防止重生后目标丢失）
        if (!this.level().isClientSide) {
            this.updateTargetGoals();
        }
    }
}
