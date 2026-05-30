package com.kltyton.mob_battle.entity.littleperson.giant.skill;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class LittlePersonGiantSkill {
    public static void runSkill_2(LittlePersonGiantEntity littlePersonGiantEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGiantEntity, 5);
        ServerLevel sw = (ServerLevel) littlePersonGiantEntity.level();
        for (LivingEntity living : livingEntities) {
            living.hurtServer(sw, living.damageSources().magic(), 5);
            living.hurtServer(sw, living.damageSources().mobAttack(littlePersonGiantEntity), 70);
        }

    }
    public static void runSkill_3(LittlePersonGiantEntity littlePersonGiantEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGiantEntity, 3);
        ServerLevel sw = (ServerLevel) littlePersonGiantEntity.level();
        for (LivingEntity living : livingEntities) {
            living.hurtServer(sw, living.damageSources().mobAttack(littlePersonGiantEntity), 65);
        }
    }
    public static void runSkill_4(LittlePersonGiantEntity littlePersonGiantEntity) {
        LivingEntity target = littlePersonGiantEntity.getTarget();
        if (target != null) {
            shootAt(littlePersonGiantEntity, target, 70);
        }
    }
    public static void shootAt(LittlePersonGiantEntity littlePersonGiant, LivingEntity target, float damage) {
        double targetX = target.getX() - littlePersonGiant.getX();
        double targetY = target.getEyeY() - littlePersonGiant.getEyeY(); // 更精确的高度计算
        double targetZ = target.getZ() - littlePersonGiant.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        // 预测目标移动（提高准确性）
        targetY += target.getDeltaMovement().y() * distance * 0.25; // 重力补偿
        Level world = littlePersonGiant.level();
        if (world instanceof ServerLevel serverWorld) {
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.STONE_ARROW, world, littlePersonGiant, new ItemStack(Items.ARROW), littlePersonGiant.getMainHandItem().getItem() == Items.BOW ? littlePersonGiant.getMainHandItem() : null);
            // 设置箭的伤害
            arrowEntity.setBaseDamage(damage);
            arrowEntity.setOwner(littlePersonGiant);
            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.shoot(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true, false);
            // 发射箭
            serverWorld.addFreshEntity(arrowEntity);
        }

        // 播放攻击音效
        littlePersonGiant.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (littlePersonGiant.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonGiantEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> p.isAlive() && entity.distanceTo(p) <= radius && p != entity && !p.isAlliedTo(entity)
        );
    }
}
