package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CyborgEntity extends BaseSkillLittlePersonEntity {
    public CyborgEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 13 * 20;
        COOL_DOWN_TIME_3 = 18 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2600.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean result = super.tryAttack(world, target);
        if (target instanceof LivingEntity livingEntity) {
            copyCyborgEntity(livingEntity);
        }
        return result;
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 65);
            copyCyborgEntity(entity.getTarget());
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.canSkill("attack3")) performSkill("attack3");
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            double targetX = entity.getTarget().getX() - entity.getX();
            double targetY = entity.getTarget().getEyeY() - entity.getEyeY(); // 更精确的高度计算
            double targetZ = entity.getTarget().getZ() - entity.getZ();
            double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
            // 预测目标移动（提高准确性）
            targetY += entity.getTarget().getVelocity().getY() * distance * 0.25; // 重力补偿
            World world = entity.getWorld();
            if (world instanceof ServerWorld serverWorld) {
                // 创建箭实体
                LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.POISON_ARROW, world, entity, new ItemStack(Items.ARROW), entity.getMainHandStack().getItem() == Items.BOW ? entity.getMainHandStack() : null);
                // 设置箭的伤害
                arrowEntity.setDamage(30);
                arrowEntity.setOwner(entity);
                arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));
                arrowEntity.setVelocity(targetX, targetY, targetZ, 1.6F, 0.01F);
                arrowEntity.setTrueDamage(true, true);
                // 发射箭
                serverWorld.spawnEntity(arrowEntity);
            }
            // 播放攻击音效
            entity.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
            copyCyborgEntity(entity.getTarget());
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 130);
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().indirectMagic(entity, entity), 15);
            copyCyborgEntity(entity.getTarget());
        }
    }
    public void copyCyborgEntity(LivingEntity livingEntity) {
        if (livingEntity.isDead()) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                CyborgEntity cyborg = ModEntities.CYBORG.create(this.getWorld(), SpawnReason.CONVERSION);
                if (cyborg != null) {
                    Vec3d pos = livingEntity.getPos();
                    cyborg.refreshPositionAndAngles(
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            livingEntity.getYaw(),
                            livingEntity.getPitch()
                    );
                    double originalMaxHealth = livingEntity.getAttributeValue(EntityAttributes.MAX_HEALTH);
                    double newMaxHealth = originalMaxHealth + 500.0;
                    cyborg.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(newMaxHealth);
                    cyborg.setHealth((float) newMaxHealth);
                    serverWorld.spawnEntity(cyborg);
                }
            }
        }
    }
}
