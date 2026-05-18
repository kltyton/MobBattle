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

    public FireWallEntity(EntityType<?> type, World world, LivingEntity owner) {
        super(type, world);
        this.owner = owner;
        this.noClip = true;
        this.yaw = owner.getYaw();

        double rad = Math.toRadians(this.yaw);
        this.originX = owner.getX() - Math.sin(rad) * 2.0;
        this.originY = owner.getY();
        this.originZ = owner.getZ() + Math.cos(rad) * 2.0;
    }

    /* 兼容旧构造，owner 为空时直接丢弃 */
    public FireWallEntity(EntityType<?> type, World world) {
        super(type, world);
        this.owner = null;
        this.noClip = true;
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

                if (this.getWorld().isClient) {
                    this.getWorld().addParticleClient(ParticleTypes.FLAME,
                            px, py, pz,
                            (random.nextDouble() - 0.5) * 0.02,
                            0.01,
                            (random.nextDouble() - 0.5) * 0.02);
                } else {
                    ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.FLAME,
                            px, py, pz,
                            1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        /* ---------- 2. 生命周期 & 伤害 ---------- */
        if (this.getWorld().isClient) return;

        if (--life <= 0) { discard(); return; }

        // 碰撞箱：沿 yaw 方向为 length，垂直方向为 width，高度为 height
        Box box = new Box(
                originX - length / 2 * cos - width / 2,
                originY,
                originZ - length / 2 * sin - width / 2,
                originX + length / 2 * cos + width / 2,
                originY + height,
                originZ + length / 2 * sin + width / 2
        );

        for (Entity e : this.getWorld().getOtherEntities(owner, box)) {
            if (e instanceof LivingEntity living) {
                living.damage((ServerWorld) this.getWorld(),
                        owner.getDamageSources().magic(), damage);
                living.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
            }
        }
    }

    /* ====== 下面保持原有空实现即可 ====== */
    @Override protected void initDataTracker(DataTracker.Builder builder) { }
    @Override public boolean damage(ServerWorld world, DamageSource source, float amount) { return false; }
    @Override protected void readCustomData(ReadView view) { }
    @Override protected void writeCustomData(WriteView view) { }
}