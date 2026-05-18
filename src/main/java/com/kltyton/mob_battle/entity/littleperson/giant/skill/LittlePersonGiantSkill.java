package com.kltyton.mob_battle.entity.littleperson.giant.skill;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

public class LittlePersonGiantSkill {
    public static void runSkill_2(LittlePersonGiantEntity littlePersonGiantEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGiantEntity, 5);
        ServerWorld sw = (ServerWorld) littlePersonGiantEntity.getWorld();
        for (LivingEntity living : livingEntities) {
            living.damage(sw, living.getDamageSources().magic(), 5);
            living.damage(sw, living.getDamageSources().mobAttack(littlePersonGiantEntity), 70);
        }

    }
    public static void runSkill_3(LittlePersonGiantEntity littlePersonGiantEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGiantEntity, 3);
        ServerWorld sw = (ServerWorld) littlePersonGiantEntity.getWorld();
        for (LivingEntity living : livingEntities) {
            living.damage(sw, living.getDamageSources().mobAttack(littlePersonGiantEntity), 65);
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
        targetY += target.getVelocity().getY() * distance * 0.25; // 重力补偿
        World world = littlePersonGiant.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.STONE_ARROW, world, littlePersonGiant, new ItemStack(Items.ARROW), littlePersonGiant.getMainHandStack().getItem() == Items.BOW ? littlePersonGiant.getMainHandStack() : null);
            // 设置箭的伤害
            arrowEntity.setDamage(damage);
            arrowEntity.setOwner(littlePersonGiant);
            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.setVelocity(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true, false);
            // 发射箭
            serverWorld.spawnEntity(arrowEntity);
        }

        // 播放攻击音效
        littlePersonGiant.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (littlePersonGiant.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonGiantEntity entity, double radius) {
        return entity.getWorld().getEntitiesByClass(
                LivingEntity.class,
                entity.getBoundingBox().expand(radius),
                p -> p.isAlive() && entity.distanceTo(p) <= radius && p != entity && !p.isTeammate(entity)
        );
    }
}
