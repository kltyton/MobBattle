package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.GeoAnimationUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IronManEntity extends BaseSkillLittlePersonEntity {
    private static final RawAnimation IRON_MAN_ATTACK_ANIM_5 = RawAnimation.begin().thenPlay("attack5").thenPlay("attack5_1");
    private final AnimationController<?> ironManSkillController = new AnimationController<>("skill_controller", animTest -> {
        if (GeoAnimationUtil.consumeFinishedTriggeredAnimation(animTest)) {
            ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            if (GeoAnimationUtil.isLastFinishedAnimation(animTest, DIE_ANIM)) {
                this.deathTime = 400;
                ClientPlayNetworking.send(new SkillPayload("die", this.getId()));
            }
        }
        return GeoAnimationUtil.playTriggeredAnimationOrStop(animTest);
    })
            .receiveTriggeredAnimations()
            .triggerableAnim("attack2", ATTACK_ANIM_2)
            .triggerableAnim("attack3", ATTACK_ANIM_3)
            .triggerableAnim("attack4", ATTACK_ANIM_4)
            .triggerableAnim("attack5", IRON_MAN_ATTACK_ANIM_5)
            .triggerableAnim("attack6", ATTACK_ANIM_6)
            .triggerableAnim("attack7", ATTACK_ANIM_7)
            .triggerableAnim("attack8", ATTACK_ANIM_8)
            .triggerableAnim("attack9", ATTACK_ANIM_9)
            .triggerableAnim("attack10", ATTACK_ANIM_10)
            .triggerableAnim("attack11", ATTACK_ANIM_11)
            .triggerableAnim("die", DIE_ANIM)
            .setCustomInstructionKeyframeHandler(s -> dispatchSkillKeyframe(s.keyframeData().getInstructions()));

    public IronManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        COOL_DOWN_TIME_1 = 13 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        COOL_DOWN_TIME_4 = 20 * 20;
        COOL_DOWN_TIME_5 = 15 * 20;
        init();
    }
    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3000.0)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ARMOR_TOUGHNESS, 20);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            if (target != null
                    && !this.hasSkill()
                    && this.canSkill("attack5")
                    && EntityUtil.isValidSummonCombatTarget(this, this.getSummonOwner(), target)
                    && this.distanceTo(target) <= this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                this.performSkill("attack5");
            }
            if (canSkill("attack3")) performSkill("attack3");
            if (this.endDamage) {
                for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                    entity.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 65);
                }
            }
        }
    }
    @Override
    public int blockProbability() {
        return 10;
    }
    @Override
    public float maxBlockDamage() {
        return 200f;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 75);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            Level world = entity.level();
            Vec3 lookDir = entity.getViewVector(1.0F);
            Direction.Axis mainAxis = Math.abs(lookDir.x) > Math.abs(lookDir.z) ?
                    Direction.Axis.X : Direction.Axis.Z;

            for (int i = 0; i < 6; i++) {
                IronManBulletEntity bullet = new IronManBulletEntity(
                        world,
                        entity,
                        entity.getTarget(),
                        mainAxis
                );
                bullet.setPos(
                        entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
                        entity.getEyeY() + (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
                        entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 2.0D
                );
                world.addFreshEntity(bullet);
            }
            entity.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        entity.endDamage = true;
    }

    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        LivingEntity target = entity.getTarget();
        if (target == null || !(entity.level() instanceof ServerLevel world)
                || !EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), target)) {
            return;
        }
        Vec3 direction = target.position().subtract(entity.position()).normalize();
        entity.setNoAi(false);
        entity.setDeltaMovement(direction.x * 1.8D, 0.15D, direction.z * 1.8D);
        entity.hurtMarked = true;
        if (entity.distanceToSqr(target) <= 9.0D) {
            entity.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected void runSkill(int attack, int phase) {
        if (attack == 5 && phase == 1) {
            runSkill5Impact(this);
            return;
        }
        super.runSkill(attack, phase);
    }

    private void runSkill5Impact(BaseSkillLittlePersonEntity entity) {
        if (!(entity.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity target = entity.getTarget();
        boolean hitTarget = target != null
                && EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), target)
                && entity.distanceToSqr(target) <= 20.25D;
        Vec3 center = hitTarget
                ? target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D)
                : entity.position().add(entity.getViewVector(1.0F).normalize().scale(2.5D)).add(0.0D, 1.0D, 0.0D);

        for (LivingEntity living : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 3.5D, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), living)
                    && living.distanceToSqr(center) <= 16.0D) {
                living.invulnerableTime = 0;
                living.hurtServer(world, entity.damageSources().mobAttack(entity), 100.0F);
                living.invulnerableTime = 0;
                Vec3 knockback = living.position().subtract(entity.position());
                Vec3 horizontal = new Vec3(knockback.x, 0.0D, knockback.z);
                if (horizontal.lengthSqr() > 1.0E-4D) {
                    horizontal = horizontal.normalize();
                    living.push(horizontal.x * 0.9D, 0.25D, horizontal.z * 0.9D);
                    living.hurtMarked = true;
                }
            }
        }

        world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 36, 0.9D, 0.6D, 0.9D, 0.12D);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.0F, 1.1F);
    }

    @Override
    public AnimationController<?> getSkillController() {
        return this.ironManSkillController;
    }

    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        if (!(entity.level() instanceof ServerLevel world)) {
            return;
        }
        for (LivingEntity living : EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 3.0D, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (EntityUtil.isValidSummonCombatTarget(entity, entity.getSummonOwner(), living)) {
                living.hurtServer(world, entity.damageSources().mobAttack(entity), 80.0F);
            }
        }
    }

    @Override
    public void die(BaseSkillLittlePersonEntity entity) {
        if (entity.level() instanceof ServerLevel serverWorld) {
            IronManTrueEntity ironManTrue = ModEntities.IRON_MAN_TRUE.create(this.level(), EntitySpawnReason.CONVERSION);
            if (ironManTrue != null) {
                Vec3 pos = entity.position();
                ironManTrue.snapTo(
                        pos.x(),
                        pos.y(),
                        pos.z(),
                        entity.getYRot(),
                        entity.getXRot()
                );
                ironManTrue.setSummonOwner(this);
                serverWorld.addFreshEntity(ironManTrue);
            }
        }
    }
}
