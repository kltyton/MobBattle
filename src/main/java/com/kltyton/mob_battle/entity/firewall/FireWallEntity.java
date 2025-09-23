package com.kltyton.mob_battle.entity.firewall;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class FireWallEntity extends Entity {
    private final LivingEntity owner;
    private int life = 120;
    private double originX, originY, originZ;
    private float yaw; // 玩家朝向角度 (0~360)
    public FireWallEntity(EntityType<?> type, World world, LivingEntity owner) {
        super(type, world);
        this.owner = owner;
        this.noClip = true;
        // 固定生成点
        // 玩家水平朝向（忽略俯仰角）
        this.yaw = owner.getYaw();

        // 把 yaw 转换成弧度
        double rad = Math.toRadians(this.yaw);

        // 基准点：玩家面前 2 格
        this.originX = owner.getX() - Math.sin(rad) * 2.0;
        this.originY = owner.getY();
        this.originZ = owner.getZ() + Math.cos(rad) * 2.0;
    }

    public FireWallEntity(EntityType<?> type, World world) {
        super(type, world);
        this.owner = null;
        this.noClip = true; // 不阻挡
    }
    @Override
    public void tick() {
        super.tick();
        if (owner == null) {
            discard();
            return;
        }
        double rad = Math.toRadians(this.yaw);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        // 火墙长度 5（-2..2），高度 3
        for (double i = -2; i <= 2; i += 0.3) {
            for (double j = 0; j < 3; j += 0.3) {
                // 沿着 yaw 的垂直方向偏移
                double px = originX + cos * i;
                double pz = originZ + sin * i;
                double py = originY + j + 0.1;

                if (this.getWorld().isClient) {
                    this.getWorld().addParticleClient(
                            ParticleTypes.FLAME,
                            px, py, pz,
                            (this.random.nextDouble() - 0.5) * 0.02,
                            0.01,
                            (this.random.nextDouble() - 0.5) * 0.02
                    );
                } else {
                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.FLAME,
                            px, py, pz,
                            2,
                            0.1, 0.1, 0.1,
                            0.01
                    );
                }
            }
        }

        if (!this.getWorld().isClient) {
            if (life-- <= 0) {
                discard();
                return;
            }
            // 碰撞箱（沿 yaw 旋转）
            Box box = new Box(
                    originX - 2 * cos - 0.5, originY, originZ - 2 * sin - 0.5,
                    originX + 2 * cos + 0.5, originY + 3, originZ + 2 * sin + 0.5
            );
            for (Entity e : getWorld().getOtherEntities(owner, box)) {
                if (e instanceof LivingEntity living) {
                    living.damage((ServerWorld)this.getWorld(), owner.getDamageSources().magic(), 30.0F);
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
                }
            }
        }

    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
    @Override
    protected void readCustomData(ReadView view) {
    }
    @Override
    protected void writeCustomData(WriteView view) {
    }
}

