package com.kltyton.mob_battle.entity.firewall;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class FireWallEntity extends Entity {

    /* ====== 可随意改的尺寸 ====== */
    private double length = 5.0D;   // 沿“左-右”方向总长度（负方向2 + 正方向2）
    private double height = 3.0D;   // 火墙高度
    private double width  = 1.0D;   // 碰撞箱“厚度”（前后方向）
    private double density = 1.2D;  // 粒子/检测格的密度（越小越密）

    /* ====== 原有字段 ====== */
    private final LivingEntity owner;
    private int life = 120;
    private double originX, originY, originZ;
    private float yaw;
    private float damage = 30.0F;

    /* ---------------------------------------------------------- */
    public float getDamage() { return damage; }
    public void  setDamage(float damage) { this.damage = damage; }

    /* 新增 setter，链式调用更爽 */
    public void setLength(double len)   { this.length   = len;}
    public void setHeight(double h)     { this.height   = h;}
    public void setWidth(double w)      { this.width    = w;}
    public void setDensity(double dens) { this.density  = dens;}
    /* ---------------------------------------------------------- */

    public FireWallEntity(EntityType<?> type, Level world, LivingEntity owner) {
        super(type, world);
        this.owner = owner;
        this.noPhysics = true;
        this.yaw = owner.getYRot();

        double rad = Math.toRadians(this.yaw);
        this.originX = owner.getX() - Math.sin(rad) * 2.0;
        this.originY = owner.getY();
        this.originZ = owner.getZ() + Math.cos(rad) * 2.0;
    }

    /* 兼容旧构造，owner 为空时直接丢弃 */
    public FireWallEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.owner = null;
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (owner == null) { discard(); return; }

        double rad = Math.toRadians(yaw);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        /* ---------- 1. 粒子渲染 ---------- */
        // 以 length 为中心左右对称，以 height 为高度
        for (double i = -length / 2; i <= length / 2; i += density) {
            for (double j = 0; j < height; j += density) {
                double px = originX + cos * i;
                double pz = originZ + sin * i;
                double py = originY + j + 0.1;

                if (this.level().isClientSide) {
                    this.level().addParticle(ParticleTypes.FLAME,
                            px, py, pz,
                            (random.nextDouble() - 0.5) * 0.02,
                            0.01,
                            (random.nextDouble() - 0.5) * 0.02);
                } else {
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLAME,
                            px, py, pz,
                            1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        /* ---------- 2. 生命周期 & 伤害 ---------- */
        if (this.level().isClientSide) return;

        if (--life <= 0) { discard(); return; }

        // 碰撞箱：沿 yaw 方向为 length，垂直方向为 width，高度为 height
        AABB box = new AABB(
                originX - length / 2 * cos - width / 2,
                originY,
                originZ - length / 2 * sin - width / 2,
                originX + length / 2 * cos + width / 2,
                originY + height,
                originZ + length / 2 * sin + width / 2
        );

        for (Entity e : this.level().getEntities(owner, box)) {
            if (e instanceof LivingEntity living) {
                living.hurtServer((ServerLevel) this.level(),
                        owner.damageSources().magic(), damage);
                living.addEffect(
                        new MobEffectInstance(MobEffects.SLOWNESS, 200, 1));
            }
        }
    }

    /* ====== 下面保持原有空实现即可 ====== */
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean hurtServer(ServerLevel world, DamageSource source, float amount) { return false; }
    @Override protected void readAdditionalSaveData(ValueInput view) { }
    @Override protected void addAdditionalSaveData(ValueOutput view) { }
}