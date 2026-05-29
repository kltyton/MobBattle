package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WildManEntity extends BaseSkillLittlePersonEntity implements RangedAttackMob {
    public WildManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 20 * 20;
        COOL_DOWN_TIME_2 = 30 * 20;
        COOL_DOWN_TIME_3 = 10 * 20;
        init();
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 6.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true)); // 添加攻击傀儡目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WarriorVillager.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity, world) -> entity instanceof Enemy && !(entity instanceof LittlePersonEntity)));
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 2000.0)
                .add(Attributes.ATTACK_DAMAGE, 45.00);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.canSkill("attack4")) performSkill("attack4");
            if (this.canSkill("attack3") && this.getTarget() != null && this.getTarget().distanceTo(this) <= 2) {
                performSkill("attack3");
            }
        }
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            shootAt(this, entity.getTarget(), 25);
        }
    }

    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 35);
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 35);
        }
    }

    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.level() instanceof ServerLevel serverWorld) {
            for (int i = 0; i < 3; i++) {
                WildBoarEntity wildBoar = ModEntities.WILD_BOAR.create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                if (wildBoar != null) {
                    Vec3 pos = entity.position();
                    wildBoar.snapTo(
                            pos.x(),
                            pos.y(),
                            pos.z(),
                            entity.getYRot(),
                            entity.getXRot()
                    );
                    wildBoar.setSummonOwner(this);
                    serverWorld.addFreshEntity(wildBoar);
                }
            }
        }
    }
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        performSkill("attack2");
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        return true;
    }

    public static void shootAt(WildManEntity wildManEntity, LivingEntity target, float damage) {
        double targetX = target.getX() - wildManEntity.getX();
        double targetY = target.getEyeY() - wildManEntity.getEyeY(); // 更精确的高度计算
        double targetZ = target.getZ() - wildManEntity.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        // 预测目标移动（提高准确性）
        targetY += target.getDeltaMovement().y() * distance * 0.25; // 重力补偿
        Level world = wildManEntity.level();
        if (world instanceof ServerLevel serverWorld) {
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.SPEAR_BULLET, world, wildManEntity, new ItemStack(Items.ARROW), wildManEntity.getMainHandItem().getItem() == Items.BOW ? wildManEntity.getMainHandItem() : null);
            // 设置箭的伤害
            arrowEntity.setBaseDamage(damage);
            arrowEntity.setOwner(wildManEntity);
            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.shoot(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true, false);
            // 发射箭
            serverWorld.addFreshEntity(arrowEntity);
        }

        // 播放攻击音效
        wildManEntity.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (wildManEntity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
