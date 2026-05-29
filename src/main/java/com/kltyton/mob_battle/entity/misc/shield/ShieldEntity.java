package com.kltyton.mob_battle.entity.misc.shield;

import com.kltyton.mob_battle.Mob_battle;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShieldEntity extends Entity implements GeoEntity {
    private static final AttributeModifier REACH_MODIFIER = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "shield_reach"), 3.0, AttributeModifier.Operation.ADD_VALUE);
    // 使用 TrackedData 同步时间，确保客户端渲染缩放正确
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(ShieldEntity.class, EntityDataSerializers.INT);
    private final Set<UUID> affectedPlayers = new HashSet<>();
    private UUID ownerUuid; // 记得在spawn时设置owner

    public void setOwner(Player owner) {
        this.ownerUuid = owner.getUUID();
    }
    public UUID getOwner() {
        return ownerUuid;
    }

    public boolean isShieldEntityTeammate(Entity other) {
        if (ownerUuid == null) return false;
        Entity owner = level().getEntity(ownerUuid);
        if (owner instanceof Player player) {
            return player.isAlliedTo(other);
        }
        return other.getUUID().equals(ownerUuid);
    }
    public ShieldEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true; // 护盾本身不被物理引擎阻挡
        this.triggerAnim("main_controller", "one");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(AGE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        int age = this.entityData.get(AGE);
        this.entityData.set(AGE, age + 1);

        if (!level().isClientSide) {
            if (age > 220) {
                for (UUID uuid : affectedPlayers) {
                    Player p = level().getPlayerByUUID(uuid);
                    if (p != null) removeReachBuff(p);
                }
                this.discard(); // 210刻后消失
                return;
            }
            handleRepulsion(age);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        this.entityData.set(AGE, view.getIntOr("age", 0));
    }


    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        view.putInt("age", this.entityData.get(AGE));
    }

    private void handleRepulsion(int age) {
        double radius = getScale(age) * 3; // 调整为 2.5 → 最大时正好 ≈5 块宽（-2.5 ~ +2.5）
        AABB box = this.getBoundingBox().inflate(radius + 1.0); // 多扩展1格，提前捕捉高速投掷物

        Set<UUID> currentPlayersInShield = new HashSet<>();

        for (Entity entity : level().getEntities(this, box)) {
            if (isShieldEntityTeammate(entity)) {
                // 同队：抗性 + 交互距离加成（你的原逻辑保留）
                if (entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 10, 3, false, false, true));
                }
                if (entity instanceof Player player) {
                    currentPlayersInShield.add(player.getUUID());
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
                Player p = level().getPlayerByUUID(uuid);
                if (p != null) removeReachBuff(p);
                return true;
            }
            return false;
        });
    }

    private void repulseEntity(Entity entity, double radius) {
        if (entity instanceof LivingEntity living && !this.level().isClientSide()) {
            living.hurtServer((ServerLevel) this.level(),
                    this.damageSources().indirectMagic(this, this.getOwner() == null ? this : level().getEntity(ownerUuid)),
                    10f
            );
        }
        Vec3 center = this.position();
        Vec3 toEntity = entity.position().subtract(center);
        double dist = toEntity.length();

        if (dist >= radius + 0.5) return; // 不在护盾内

        Vec3 normal = toEntity.normalize();
        if (normal.lengthSqr() == 0) normal = new Vec3(1, 0, 0);

        // 强制传送至护盾表面（防止穿模）
        Vec3 surfacePos = center.add(normal.scale(radius + 0.05));
        entity.setPos(surfacePos);

        if (entity instanceof Projectile projectile && !(isShieldEntityTeammate(projectile))) {
            // === 投掷物专用的镜面反射 ===
            Vec3 incident = projectile.getDeltaMovement();
            double dot = incident.dot(normal);
            Vec3 reflected = incident.subtract(normal.scale(2 * dot));

            projectile.setDeltaMovement(reflected.scale(1.1)); // 稍微加速，像原版盾牌格挡
            projectile.hurtMarked = true;

            // 可选：格挡音效和粒子反馈
            level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
            // 粒子（可根据需要调整）
            ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT,
                    entity.getX(), entity.getY(), entity.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
        } else {
            // === 普通实体强力推开 ===
            Vec3 push = normal.scale(1.2); // 力度更大
            entity.push(push.x, 0.3 + push.y * 0.5, push.z);
            entity.hurtMarked = true;
        }
    }
    private void applyReachBuff(Player player) {
        var reachAttr = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);

        if (reachAttr != null && !reachAttr.hasModifier(REACH_MODIFIER.id())) {
            reachAttr.addTransientModifier(REACH_MODIFIER);
            affectedPlayers.add(player.getUUID());
        }
    }
    private void removeReachBuff(Player player) {
        player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).removeModifier(REACH_MODIFIER.id());
    }
    @Override
    public void onRemoval(Entity.RemovalReason reason) {
        // 实体消失时清理所有加成
        for (UUID uuid : affectedPlayers) {
            Player p = level().getPlayerByUUID(uuid);
            if (p != null) removeReachBuff(p);
        }
        super.onRemoval(reason);
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
