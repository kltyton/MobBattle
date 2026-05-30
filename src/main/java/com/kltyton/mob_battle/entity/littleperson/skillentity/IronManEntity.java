package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IronManEntity extends BaseSkillLittlePersonEntity {
    public IronManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 13 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
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

            IronManBulletEntity bullet = new IronManBulletEntity(
                    world,
                    entity,
                    entity.getTarget(),
                    mainAxis
            );
            bullet.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
            world.addFreshEntity(bullet);
            entity.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        entity.endDamage = true;
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
