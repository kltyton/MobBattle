package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class IronManEntity extends BaseSkillLittlePersonEntity {
    public IronManEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 13 * 20;
        COOL_DOWN_TIME_2 = 20 * 20;
        COOL_DOWN_TIME_3 = 25 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2500.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0)
                .add(EntityAttributes.ARMOR, 10)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.endDamage) {
            for (LivingEntity entity : EntityUtil.getNearbyEntity(this, LivingEntity.class, Object.class, 2, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
                entity.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 65);
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
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 75);
        }
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            World world = entity.getWorld();
            Vec3d lookDir = entity.getRotationVec(1.0F);
            Direction.Axis mainAxis = Math.abs(lookDir.x) > Math.abs(lookDir.z) ?
                    Direction.Axis.X : Direction.Axis.Z;

            IronManBulletEntity bullet = new IronManBulletEntity(
                    world,
                    entity,
                    entity.getTarget(),
                    mainAxis
            );
            bullet.setPosition(entity.getX(), entity.getEyeY(), entity.getZ());
            world.spawnEntity(bullet);
            entity.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        entity.endDamage = true;
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            IronManTrueEntity ironManTrue = ModEntities.IRON_MAN_TRUE.create(this.getWorld(), SpawnReason.CONVERSION);
            if (ironManTrue != null) {
                Vec3d pos = entity.getPos();
                ironManTrue.refreshPositionAndAngles(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        entity.getYaw(),
                        entity.getPitch()
                );
                serverWorld.spawnEntity(ironManTrue);
            }
        }
    }
}
