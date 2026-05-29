package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.misc.PoisonousBeachEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

public class PoisonousSilverfishEntity extends CoalSilverfishEntity {
    public PoisonousSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public int getCooldownTime() {
        return 20 * 35;
    }
    @Override
    public boolean canBlock() {
        return false;
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
    }
    @Override
    public boolean canSkill() {
        return false;
    }
    public void performSkill() {
    }
    @Override
    public boolean hasSkill() {
        return true;
    }
    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        // 仅在服务端逻辑中生成，防止客户端重复生成
        if (!this.level().isClientSide) {
            // 1. 创建药水云实体
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            PoisonousBeachEntity beach = ModEntities.POISONOUS_BEACH.create(this.level(), EntitySpawnReason.MOB_SUMMONED);

            // 2. 设置药水云的属性
            cloud.setRadius(1.0F);               // 云雾半径（3格）
            cloud.setRadiusOnUse(0.0f);         // 每次被触发后半径缩小的量
            cloud.setWaitTime(0);               // 生成后等待多久开始生效（半秒）
            cloud.setDuration(600);              // 药水云存在的时间：30秒 * 20 tick = 600
            //cloud.setRadiusGrowth(-cloud.getRadius() / (float)cloud.getDuration()); // 让云雾随时间慢慢消失(半径增长)

            // 3. 设置具体的中毒效果
            MobEffectInstance poisonEffect = new MobEffectInstance(
                    ModEffects.HEART_EATER_ENTRY,
                    200, // 10秒持续时间 * 20 tick = 200
                    4    // 等级 5 (1级对应0，以此类推)
            );
            cloud.addEffect(poisonEffect);
            // 4. 将药水云加入世界
            this.level().addFreshEntity(cloud);
            if (beach != null) {
                beach.setPos(this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(beach);
            }
        }
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(ModEntityAttributes.MAGIC_DAMAGE, 10.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.3);
    }
}
