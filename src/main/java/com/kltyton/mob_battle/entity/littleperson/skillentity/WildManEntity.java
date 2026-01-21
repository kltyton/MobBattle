package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class WildManEntity extends BaseSkillLittlePersonEntity {
    public WildManEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 3);
        COOL_DOWN_TIME_1 = 20 * 20;
        COOL_DOWN_TIME_2 = 30 * 20;
        COOL_DOWN_TIME_3 = 10 * 20;
        init();
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2100.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 45.00);
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (this.canSkill("attack2")) performSkill("attack2");
            if (this.canSkill("attack4")) performSkill("attack4");
        }
    }
    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            shootAt(this, entity.getTarget(), 200);
        }
    }

    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 90);
        }
    }
    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        if (entity.getTarget() != null) {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getDamageSources().mobAttack(entity), 100);
        }
    }

    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 3; i++) {
                WildBoarEntity wildBoar = ModEntities.WILD_BOAR.create(this.getWorld(), SpawnReason.MOB_SUMMONED);
                if (wildBoar != null) {
                    Vec3d pos = entity.getPos();
                    wildBoar.refreshPositionAndAngles(
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            entity.getYaw(),
                            entity.getPitch()
                    );
                    serverWorld.spawnEntity(wildBoar);
                }
            }
        }
    }
    public static void shootAt(WildManEntity wildManEntity, LivingEntity target, float damage) {
        double targetX = target.getX() - wildManEntity.getX();
        double targetY = target.getEyeY() - wildManEntity.getEyeY(); // 更精确的高度计算
        double targetZ = target.getZ() - wildManEntity.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        // 预测目标移动（提高准确性）
        targetY += target.getVelocity().getY() * distance * 0.25; // 重力补偿
        World world = wildManEntity.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(ModEntities.SPEAR_BULLET, world, wildManEntity, new ItemStack(Items.ARROW), wildManEntity.getMainHandStack().getItem() == Items.BOW ? wildManEntity.getMainHandStack() : null);
            // 设置箭的伤害
            arrowEntity.setDamage(damage);
            arrowEntity.setOwner(wildManEntity);
            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.setVelocity(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true, false);
            // 发射箭
            serverWorld.spawnEntity(arrowEntity);
        }

        // 播放攻击音效
        wildManEntity.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (wildManEntity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
