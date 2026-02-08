package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SexEntity extends BaseSkillLittlePersonEntity {
    private static final TrackedData<Integer> GRABBED_ENTITY_ID = DataTracker.registerData(SexEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final List<LivingEntity> knockedTargets = new ArrayList<>();
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    protected static final RawAnimation SEX_ATTACK_ANIM_5 = RawAnimation.begin().thenPlay("attack5").thenPlay("attack5_1");
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GRABBED_ENTITY_ID, -1);
    }
    public void setGrabbedEntityId(int id) {
        this.dataTracker.set(GRABBED_ENTITY_ID, id);
    }

    public int getGrabbedEntityId() {
        return this.dataTracker.get(GRABBED_ENTITY_ID);
    }
    @Override
    public PlayState mainController(final AnimationTest<LittlePersonMilitiaEntity> event) {
        if (event.isMoving()) {
            return this.getTarget() == null ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(RUN_ANIM);
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
            .setCustomInstructionKeyframeHandler(s -> {
                if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack2", this.getId()
                    ));
                }
                if ("runAttack3;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack3", this.getId()
                    ));
                }
                if ("runAttack4;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack4", this.getId()
                    ));
                }
                if ("runAttack5;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack5", this.getId()
                    ));
                }
                if ("runAttack6;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack6", this.getId()
                    ));
                }
                if ("runAttack7;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack7", this.getId()
                    ));
                }
                if ("runAttack8;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack8", this.getId()
                    ));
                }
                if ("runAttack9;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack9", this.getId()
                    ));
                }
                if ("runAttack10;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack10", this.getId()
                    ));
                }
                if ("runAttack11;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack11", this.getId()
                    ));
                }
                if ("runDie;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "die", this.getId()
                    ));
                }
                if ("runStop;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "stop", this.getId()
                    ));
                }
                if ("runStopAi;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "stop_ai", this.getId()
                    ));
                }
                if ("runStartAi;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "start_ai", this.getId()
                    ));
                }
            });
    @Override
    public AnimationController<?> getSkillController() {
        return this.sexEntitySkillController;
    }
    public SexEntity(EntityType<? extends BaseSkillLittlePersonEntity> entityType, World world) {
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
        this.heal(15f);
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
        if (!this.getWorld().isClient) {
            if (this.endDamage) {
                for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                    entity.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 85);
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 2, 0));
                }
            }
            int grabbedId = this.getGrabbedEntityId();
            if (grabbedId != -1 && !this.getWorld().isClient) {
                Entity grabbedEntity = this.getWorld().getEntityById(grabbedId);
                // 如果技能结束、目标死亡或目标离得太远，释放
                if (!this.hasSkill() || grabbedEntity == null || !grabbedEntity.isAlive()) {
                    this.setGrabbedEntityId(-1);
                } else {
                    // 计算银鱼面前的位置 (例如面前 1.5 格)
                    double distance = 1.5;
                    double radians = Math.toRadians(this.getYaw());
                    double targetX = this.getX() - Math.sin(radians) * distance;
                    double targetZ = this.getZ() + Math.cos(radians) * distance;
                    double targetY = this.getY();
                    // 强制传送目标并清除动能
                    if (grabbedEntity instanceof LivingEntity living) {
                        living.teleport((ServerWorld) this.getWorld(), targetX, targetY, targetZ, Set.of(), grabbedEntity.getYaw(), grabbedEntity.getPitch(), true);
                        living.setVelocity(0, 0, 0);
                        living.velocityDirty = true;
                    }
                }
            }
            // 检查被击飞的目标
            Iterator<LivingEntity> iterator = this.knockedTargets.iterator();
            while (iterator.hasNext()) {
                LivingEntity le = iterator.next();
                if (le == null || le.isRemoved() || le.isOnGround()) {
                    // 落地或死亡/移除 → 恢复重力
                    EntityAttributeInstance inst = null;
                    if (le != null) {
                        inst = le.getAttributeInstance(EntityAttributes.GRAVITY);
                    }
                    if (inst != null) {
                        inst.removeModifier(Identifier.of(Mob_battle.MOD_ID, "kltyton_double_gravity"));
                    }
                    iterator.remove();
                }
            }
        }
    }
    @Override
    public void onRemove(Entity.RemovalReason reason){
        // 清理所有仍在列表中的目标
        for (LivingEntity le : new ArrayList<>(this.knockedTargets)) {
            if (le != null && !le.isRemoved()) {
                EntityAttributeInstance inst = le.getAttributeInstance(EntityAttributes.GRAVITY);
                if (inst != null) {
                    inst.removeModifier(Identifier.of(Mob_battle.MOD_ID, "kltyton_double_gravity"));
                }
            }
        }
        this.knockedTargets.clear();
        super.onRemove(reason);
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 75.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0);
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 80);
        });
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 90);
        });
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 100);
            if (result) {
                double d = livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
                double e = Math.max(0.0, 1.0 - d);
                livingEntity.setVelocity(livingEntity.getVelocity().add(0.0, 0.6F * e, 0.0));
                EntityAttributeInstance gravityInst = livingEntity.getAttributeInstance(EntityAttributes.GRAVITY);
                if (gravityInst != null && !this.knockedTargets.contains(livingEntity)) {
                    double currentGravity = gravityInst.getValue();
                    EntityAttributeModifier gravityModifier = new EntityAttributeModifier(
                            Identifier.of(Mob_battle.MOD_ID, "kltyton_double_gravity"),
                            currentGravity,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    );
                    gravityInst.addTemporaryModifier(gravityModifier);
                    this.knockedTargets.add(livingEntity);
                }
            }
        }

    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 100);
            if (result) {
                double d = livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
                double e = Math.max(0.0, 1.0 - d);
                livingEntity.setVelocity(livingEntity.getVelocity().add(0.0, 0.4F * e, 0.0));
            }
        }

    }
    @Override
    public void runSkill_8(BaseSkillLittlePersonEntity entity) {
        LivingEntity livingEntity = entity.getTarget();
        if (livingEntity != null) {
            boolean result = livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 100);
            if (result) {
                EntityUtil.getNearbyEntity(entity, LivingEntity.class, 5, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity1 -> {
                    livingEntity1.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 90);
                });
            }
        }

    }
    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        if (this.getWorld().isClient) return;
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 3, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        // 2. 如果找到了目标，设置 ID
        if (target != null) {
            this.setGrabbedEntityId(target.getId());
            performSkill("attack9");
        }
    }
    @Override
    public void runSkill_9(BaseSkillLittlePersonEntity entity) {
        if (this.getWorld().isClient) return;
        Entity target = entity.getWorld().getEntityById(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().indirectMagic(entity, entity), 80);

        }
    }
    @Override
    public void runSkill_10(BaseSkillLittlePersonEntity entity) {
        if (this.getWorld().isClient) return;
        Entity target = entity.getWorld().getEntityById(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().indirectMagic(entity, entity), 150);
            this.setGrabbedEntityId(-1);
            livingEntity.takeKnockback(1.2, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
        }
    }
    @Override
    public void runSkill_7(BaseSkillLittlePersonEntity entity) {
        this.setAiDisabled(false);
        entity.endDamage = true;
    }

}
