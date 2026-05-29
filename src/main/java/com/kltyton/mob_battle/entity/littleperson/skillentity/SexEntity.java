package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.*;

public class SexEntity extends BaseSkillLittlePersonEntity {
    private static final EntityDataAccessor<Integer> GRABBED_ENTITY_ID = SynchedEntityData.defineId(SexEntity.class, EntityDataSerializers.INT);
    private final List<LivingEntity> knockedTargets = new ArrayList<>();
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    protected static final RawAnimation SEX_ATTACK_ANIM_5 = RawAnimation.begin().thenPlay("attack5").thenPlay("attack8");

    private final ServerBossEvent bossBar = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
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
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (this.hasCustomName()) {
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (this.bossBar != null) {
            this.bossBar.setProgress(health / this.getMaxHealth());
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRABBED_ENTITY_ID, -1);
    }
    public void setGrabbedEntityId(int id) {
        this.entityData.set(GRABBED_ENTITY_ID, id);
    }

    public int getGrabbedEntityId() {
        return this.entityData.get(GRABBED_ENTITY_ID);
    }
    @Override
    public PlayState mainController(final AnimationTest<LittlePersonMilitiaEntity> event) {
        if (event.isMoving()) {
            return this.isAggressive() ? event.setAndContinue(RUN_ANIM) : event.setAndContinue(WALK_ANIM);
        } else return event.setAndContinue(IDLE_ANIM);
    }
    public AnimationController<?> sexEntitySkillController = new AnimationController<>( "skill_controller", animTest -> {
        if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
            if (this.hasSkill()) ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            if (animTest.isCurrentAnimation(DIE_ANIM)) {
                this.deathTime = 400;
                ClientPlayNetworking.send(new SkillPayload(
                        "die", this.getId()
                ));
            }
        }
        return PlayState.STOP;
    })
            .triggerableAnim("attack2", ATTACK_ANIM_2)
            .triggerableAnim("attack3", ATTACK_ANIM_3)
            .triggerableAnim("attack4", ATTACK_ANIM_4)
            .triggerableAnim("attack5", SEX_ATTACK_ANIM_5)
            .triggerableAnim("attack6", ATTACK_ANIM_6)
            .triggerableAnim("attack7", ATTACK_ANIM_7)
            .triggerableAnim("attack8", ATTACK_ANIM_8)
            .triggerableAnim("attack9", ATTACK_ANIM_9)
            .triggerableAnim("attack10", ATTACK_ANIM_10)
            .triggerableAnim("attack11", ATTACK_ANIM_11)
            .triggerableAnim("die", DIE_ANIM)
            .setCustomInstructionKeyframeHandler(s -> dispatchSkillKeyframe(s.keyframeData().getInstructions()));
    @Override
    public AnimationController<?> getSkillController() {
        return this.sexEntitySkillController;
    }
    public SexEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, Level world) {
        super(entityType, world, 6);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 10 * 20;
        COOL_DOWN_TIME_3 = 8 * 20;
        COOL_DOWN_TIME_4 = 12 * 20;
        COOL_DOWN_TIME_5 = 18 * 20;
        COOL_DOWN_TIME_6 = 15 * 20;
        init();
    }
    @Override
    public void heal() {
        this.heal(5f);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime == 1 && !this.level().isClientSide()) {
            this.setNoAi(true);
            this.triggerAnim("skill_controller", "die");
        }
        if (this.deathTime >= 400 && !this.level().isClientSide() && !this.isRemoved()) {
            die(this);
            this.level().broadcastEntityEvent(this, net.minecraft.world.entity.EntityEvent.POOF);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
    @Override
    public int blockProbability() {
        return 20;
    }
    @Override
    public float maxBlockDamage() {
        return 300f;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.endDamage) {
                for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                    entity.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 85);
                }
            }
            int grabbedId = this.getGrabbedEntityId();
            if (grabbedId != -1 && !this.level().isClientSide) {
                Entity grabbedEntity = this.level().getEntity(grabbedId);
                // 如果技能结束、目标死亡或目标离得太远，释放
                if (!this.hasSkill() || grabbedEntity == null || !grabbedEntity.isAlive()) {
                    this.setGrabbedEntityId(-1);
                } else {
                    // 计算自己面前的安全位置，避免把目标强行塞进墙里。
                    double distance = 1.5;
                    double radians = Math.toRadians(this.getYRot());
                    double targetX = this.getX() - Math.sin(radians) * distance;
                    double targetZ = this.getZ() + Math.cos(radians) * distance;
                    double targetY = this.getY();
                    // 强制传送目标并清除动能
                    if (grabbedEntity instanceof LivingEntity living) {
                        AABB movedBox = living.getBoundingBox().move(targetX - living.getX(), targetY - living.getY(), targetZ - living.getZ());
                        if (this.level().noCollision(living, movedBox)) {
                            living.teleportTo((ServerLevel) this.level(), targetX, targetY, targetZ, Set.of(), grabbedEntity.getYRot(), grabbedEntity.getXRot(), true);
                        }
                        living.setDeltaMovement(0, 0, 0);
                        living.hasImpulse = true;
                    }
                }
            }
            // 检查被击飞的目标
            Iterator<LivingEntity> iterator = this.knockedTargets.iterator();
            while (iterator.hasNext()) {
                LivingEntity le = iterator.next();
                if (le == null || le.isRemoved() || le.onGround()) {
                    // 落地或死亡/移除 → 恢复重力
                    AttributeInstance inst = null;
                    if (le != null) {
                        inst = le.getAttribute(Attributes.GRAVITY);
                    }
                    if (inst != null) {
                        inst.removeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "kltyton_double_gravity"));
                    }
                    iterator.remove();
                }
            }
        }
    }
    @Override
    public void onRemoval(Entity.RemovalReason reason){
        // 清理所有仍在列表中的目标
        for (LivingEntity le : new ArrayList<>(this.knockedTargets)) {
            if (le != null && !le.isRemoved()) {
                AttributeInstance inst = le.getAttribute(Attributes.GRAVITY);
                if (inst != null) {
                    inst.removeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "kltyton_double_gravity"));
                }
            }
        }
        this.knockedTargets.clear();
        super.onRemoval(reason);
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 12000.0)
                .add(Attributes.ATTACK_DAMAGE, 75.0)
                .add(Attributes.MOVEMENT_SPEED, 0.55)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 120);
        });
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 145);
        });
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 180);
            if (result) {
                double d = livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double e = Math.max(0.0, 1.0 - d);
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0.0, 0.6F * e, 0.0));
                AttributeInstance gravityInst = livingEntity.getAttribute(Attributes.GRAVITY);
                if (gravityInst != null && !this.knockedTargets.contains(livingEntity)) {
                    double currentGravity = gravityInst.getValue();
                    AttributeModifier gravityModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "kltyton_double_gravity"),
                            currentGravity,
                            AttributeModifier.Operation.ADD_VALUE
                    );
                    gravityInst.addTransientModifier(gravityModifier);
                    this.knockedTargets.add(livingEntity);
                }
            }
        }

    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 150);
            if (result) {
                double d = livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double e = Math.max(0.0, 1.0 - d);
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0.0, 0.4F * e, 0.0));
            }
        }

    }
    @Override
    public void runSkill_8(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 150);
            if (result) {
                EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity1 -> {
                    livingEntity1.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 120);
                });
            }
        }

    }
    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        if (this.level().isClientSide) return;
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 5, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        // 2. 如果找到了目标，设置 ID
        if (target != null) {
            if (target instanceof LivingEntity livingEntity) {
                double dx = entity.getX() - livingEntity.getX();
                double dz = entity.getZ() - livingEntity.getZ();
                float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
                livingEntity.setYRot(yaw);
                livingEntity.setYHeadRot(yaw);
                livingEntity.setYBodyRot(yaw);
            }
            this.setGrabbedEntityId(target.getId());
            performSkill("attack9");
        }
    }
    @Override
    public void runSkill_9(BaseSkillLittlePersonEntity entity) {
        if (this.level().isClientSide) return;
        Entity target = entity.level().getEntity(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 100);
        }
    }
    @Override
    public void runSkill_10(BaseSkillLittlePersonEntity entity) {
        if (this.level().isClientSide) return;
        Entity target = entity.level().getEntity(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            this.setGrabbedEntityId(-1);
            livingEntity.knockback(2.4, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().indirectMagic(entity, entity), 150);
        }
    }


    @Override
    public void runSkill_7(BaseSkillLittlePersonEntity entity) {
        this.setNoAi(false);
        entity.endDamage = true;
    }

}
