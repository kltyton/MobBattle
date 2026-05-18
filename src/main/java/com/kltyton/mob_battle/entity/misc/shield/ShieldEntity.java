package com.kltyton.mob_battle.entity.misc.shield;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShieldEntity extends Entity implements GeoEntity {
    private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(
            Identifier.of(Mob_battle.MOD_ID, "shield_reach"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE);
    // 使用 TrackedData 同步时间，确保客户端渲染缩放正确
    private static final TrackedData<Integer> AGE = DataTracker.registerData(ShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final Set<UUID> affectedPlayers = new HashSet<>();
    private UUID ownerUuid; // 记得在spawn时设置owner

    public void setOwner(PlayerEntity owner) {
        this.ownerUuid = owner.getUuid();
    }
    public UUID getOwner() {
        return ownerUuid;
    }

    public boolean isShieldEntityTeammate(Entity other) {
        if (ownerUuid == null) return false;
        Entity owner = getWorld().getEntity(ownerUuid);
        if (owner instanceof PlayerEntity player) {
            return player.isTeammate(other);
        }
        return other.getUuid().equals(ownerUuid);
    }
    public ShieldEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true; // 护盾本身不被物理引擎阻挡
        this.triggerAnim("main_controller", "one");
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(AGE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        int age = this.dataTracker.get(AGE);
        this.dataTracker.set(AGE, age + 1);

        if (!getWorld().isClient) {
            if (age > 220) {
                for (UUID uuid : affectedPlayers) {
                    PlayerEntity p = getWorld().getPlayerByUuid(uuid);
                    if (p != null) removeReachBuff(p);
                }
                this.discard(); // 210刻后消失
                return;
            }
            handleRepulsion(age);
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.dataTracker.set(AGE, view.getInt("age", 0));
    }


    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("age", this.dataTracker.get(AGE));
    }

    private void handleRepulsion(int age) {
        double radius = getScale(age) * 3; // 调整为 2.5 → 最大时正好 ≈5 块宽（-2.5 ~ +2.5）
        Box box = this.getBoundingBox().expand(radius + 1.0); // 多扩展1格，提前捕捉高速投掷物

        Set<UUID> currentPlayersInShield = new HashSet<>();

        for (Entity entity : getWorld().getOtherEntities(this, box)) {
            if (isShieldEntityTeammate(entity)) {
                // 同队：抗性 + 交互距离加成（你的原逻辑保留）
                if (entity instanceof LivingEntity living) {
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 3, false, false, true));
                }
                if (entity instanceof PlayerEntity player) {
                    currentPlayersInShield.add(player.getUuid());
                    applyReachBuff(player);
                }
            } else {
                // 非同队：强力推开/反射
                repulseEntity(entity, radius);
            }
        }

        // 清理离开的玩家加成（你的原逻辑）
        affectedPlayers.removeIf(uuid -> {
            if (!currentPlayersInShield.contains(uuid)) {
                PlayerEntity p = getWorld().getPlayerByUuid(uuid);
                if (p != null) removeReachBuff(p);
                return true;
            }
            return false;
        });
    }

    private void repulseEntity(Entity entity, double radius) {
        if (entity instanceof LivingEntity living && !this.getWorld().isClient()) {
            living.damage((ServerWorld) this.getWorld(),
                    this.getDamageSources().indirectMagic(this, this.getOwner() == null ? this : getWorld().getEntity(ownerUuid)),
                    10f
            );
        }
        Vec3d center = this.getPos();
        Vec3d toEntity = entity.getPos().subtract(center);
        double dist = toEntity.length();

        if (dist >= radius + 0.5) return; // 不在护盾内

        Vec3d normal = toEntity.normalize();
        if (normal.lengthSquared() == 0) normal = new Vec3d(1, 0, 0);

        // 强制传送至护盾表面（防止穿模）
        Vec3d surfacePos = center.add(normal.multiply(radius + 0.05));
        entity.setPosition(surfacePos);

        if (entity instanceof ProjectileEntity projectile && !(isShieldEntityTeammate(projectile))) {
            // === 投掷物专用的镜面反射 ===
            Vec3d incident = projectile.getVelocity();
            double dot = incident.dotProduct(normal);
            Vec3d reflected = incident.subtract(normal.multiply(2 * dot));

            projectile.setVelocity(reflected.multiply(1.1)); // 稍微加速，像原版盾牌格挡
            projectile.velocityModified = true;

            // 可选：格挡音效和粒子反馈
            getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            // 粒子（可根据需要调整）
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.CRIT,
                    entity.getX(), entity.getY(), entity.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
        } else {
            // === 普通实体强力推开 ===
            Vec3d push = normal.multiply(1.2); // 力度更大
            entity.addVelocity(push.x, 0.3 + push.y * 0.5, push.z);
            entity.velocityModified = true;
        }
    }
    private void applyReachBuff(PlayerEntity player) {
        var reachAttr = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);

        if (reachAttr != null && !reachAttr.hasModifier(REACH_MODIFIER.id())) {
            reachAttr.addTemporaryModifier(REACH_MODIFIER);
            affectedPlayers.add(player.getUuid());
        }
    }
    private void removeReachBuff(PlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE).removeModifier(REACH_MODIFIER.id());
    }
    @Override
    public void onRemove(Entity.RemovalReason reason) {
        // 实体消失时清理所有加成
        for (UUID uuid : affectedPlayers) {
            PlayerEntity p = getWorld().getPlayerByUuid(uuid);
            if (p != null) removeReachBuff(p);
        }
        super.onRemove(reason);
    }
    public float getScale(int age) {
        if (age <= 10) return age / 10f; // 变大阶段
        if (age <= 200) return 1.0f;     // 维持阶段
        if (age <= 210) return 1.0f - (age - 200) / 10f; // 变小阶段
        return 0f;
    }
    protected static final RawAnimation ONE = RawAnimation.begin().thenPlayAndHold("one");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", animTest -> PlayState.STOP)
                .triggerableAnim("one", ONE));
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
