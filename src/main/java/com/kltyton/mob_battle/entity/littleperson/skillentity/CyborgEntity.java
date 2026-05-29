package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CyborgEntity extends BaseSkillLittlePersonEntity {
    public CyborgEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 5 * 20;
        COOL_DOWN_TIME_2 = 13 * 20;
        COOL_DOWN_TIME_3 = 18 * 20;
        init();
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3600.0)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.20);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        boolean result = super.doHurtTarget(world, target);
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
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 65);
            copyCyborgEntity(entity.getTarget());
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.canSkill("attack3")) performSkill("attack3");
    }
    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            double targetX = entity.getTarget().getX() - entity.getX();
            double targetY = entity.getTarget().getEyeY() - entity.getEyeY(); // 更精确的高度计算
            double targetZ = entity.getTarget().getZ() - entity.getZ();
            double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
            // 预测目标移动（提高准确性）
            targetY += entity.getTarget().getDeltaMovement().y() * distance * 0.25; // 重力补偿
            Level world = entity.level();
            if (world instanceof ServerLevel serverWorld) {
                // 创建箭实体
                LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.POISON_ARROW, world, entity, new ItemStack(Items.ARROW), entity.getMainHandItem().getItem() == Items.BOW ? entity.getMainHandItem() : null);
                // 设置箭的伤害
                arrowEntity.setBaseDamage(30);
                arrowEntity.setOwner(entity);
                arrowEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
                arrowEntity.shoot(targetX, targetY, targetZ, 1.6F, 0.01F);
                arrowEntity.setTrueDamage(true, true);
                // 发射箭
                serverWorld.addFreshEntity(arrowEntity);
            }
            // 播放攻击音效
            entity.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
            copyCyborgEntity(entity.getTarget());
        }
    }
    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().mobAttack(entity), 130);
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.damageSources().indirectMagic(entity, entity), 15);
            copyCyborgEntity(entity.getTarget());
        }
    }
    public void copyCyborgEntity(LivingEntity livingEntity) {
        if (livingEntity.isDeadOrDying()) {
            if (this.level() instanceof ServerLevel serverWorld) {
                CyborgEntity cyborg = ModEntities.CYBORG.create(this.level(), EntitySpawnReason.CONVERSION);
                if (cyborg != null) {
                    Vec3 pos = livingEntity.position();
                    cyborg.snapTo(
                            pos.x(),
                            pos.y(),
                            pos.z(),
                            livingEntity.getYRot(),
                            livingEntity.getXRot()
                    );
                    double originalMaxHealth = livingEntity.getAttributeValue(Attributes.MAX_HEALTH);
                    double newMaxHealth = originalMaxHealth + 10;
                    cyborg.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newMaxHealth);
                    cyborg.setHealth((float) newMaxHealth);
                    cyborg.setSummonOwner(this);
                    serverWorld.addFreshEntity(cyborg);
                }
            }
        }
    }
}
