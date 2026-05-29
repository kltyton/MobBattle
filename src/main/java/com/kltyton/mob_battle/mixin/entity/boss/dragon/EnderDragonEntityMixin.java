package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonShadowEntity;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonSkillManager;
import com.kltyton.mob_battle.sounds.ModSounds;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(EnderDragon.class)
@Implements(@Interface(iface = EnderDragonAccessor.class, prefix = "custom$"))
public abstract class EnderDragonEntityMixin extends Mob {
    // ==================== 新增字段（放在类顶部） ====================
    @Unique
    private static final EntityDataAccessor<Boolean> IS_SHADOW = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private EnderDragonShadowEntity shadow;
    @Unique
    private long shadowRespawnTime = -1L;
    @Unique
    public boolean custom$isShadow() {
        return this.getEntityData().get(IS_SHADOW);
    }
    @Unique
    public void custom$setShadow(boolean shadow) {
        this.getEntityData().set(IS_SHADOW, shadow);
    }
    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    protected void initDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(IS_SHADOW, false);
    }
    @Shadow
    @Final
    public EnderDragonPart body;

    @Shadow
    @Final
    public EnderDragonPart head;

    @Shadow
    @Final
    private EnderDragonPhaseManager phaseManager;

    @Shadow
    protected abstract void reallyHurt(ServerLevel world, DamageSource source, float amount);

    protected EnderDragonEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private EnderDragonSkillManager skillManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSkillManager(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        this.skillManager = new EnderDragonSkillManager(self);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void tickSkills(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        if (self.level() instanceof ServerLevel serverWorld) {
            skillManager.tick(serverWorld);
        }
    }
    // ==================== 新增：50%血量召唤 + 复活 + 死亡联动 ====================
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void manageShadowDragon(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        if (!(self.level() instanceof ServerLevel world)) return;
        if (self instanceof EnderDragonShadowEntity) return;
        if (self.isDeadOrDying()) {
            if (shadow != null) {
                shadow.kill(world);
                shadow = null;
            }
            return;
        }

        float healthPercent = self.getHealth() / self.getMaxHealth();

        // 首次掉到50%以下召唤
        if (shadow == null && healthPercent <= 0.5F && shadowRespawnTime == -1L) {
            summonShadow(world, self);
        }

        // 复活计时器
        if (shadowRespawnTime > 0 && world.getGameTime() >= shadowRespawnTime && shadow == null) {
            summonShadow(world, self);
            shadowRespawnTime = -1L;
        }
    }
    @Unique
    private void summonShadow(ServerLevel world, EnderDragon owner) {
        EnderDragonShadowEntity newShadow = new EnderDragonShadowEntity(EntityType.ENDER_DRAGON, world);
        newShadow.setOwner(owner);
        newShadow.snapTo(
                owner.getX() + 12 + world.random.nextDouble() * 8,
                owner.getY() + 10,
                owner.getZ() + 12 + world.random.nextDouble() * 8,
                world.random.nextFloat() * 360, 0
        );
        newShadow.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        newShadow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        newShadow.setHealth(10000);
        ((EnderDragonAccessor) newShadow).setShadow(true);
        world.addFreshEntity(newShadow);
        this.shadow = newShadow;
        // 特效
        world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, newShadow.getX(), newShadow.getY() + 5, newShadow.getZ(), 1, 0, 0, 0, 0);
        world.playSound(null, newShadow.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 6.0F, 0.8F);
    }
    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            shift = At.Shift.AFTER))
    private void checkRushCollision(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        if (!(self.level() instanceof ServerLevel world)) return;

        if (skillManager != null && skillManager.isChargingRush()) {
            // 检测头部/身体附近实体（类似原版翅膀碰撞，但更强）
            AABB headBox = this.head.getBoundingBox().inflate(2.0, 1.5, 2.0);
            AABB bodyBox = this.body.getBoundingBox().inflate(3.0, 2.0, 3.0);
            if (world.getGameTime() > skillManager.rushEndTime) return;
            for (Entity entity : world.getEntities(self, headBox.minmax(bodyBox))) {
                if (entity instanceof LivingEntity living && !entity.isSpectator() && living != self) {
                    if (living instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                    if (living.isAlliedTo(self)) continue;  // 跳过队友
                    // 330点物理伤害（用mobAttack来源）
                    DamageSource source = self.damageSources().mobAttack(self);
                    living.hurtServer(world, source, 330.0F);

                    // 击退翻倍（原版冲撞击退 ≈ 2~3，这里翻倍 ≈ 4~6）
                    Vec3 knockDir = living.position().subtract(self.position()).normalize();
                    living.push(knockDir.x * 1.2, 0.8, knockDir.z * 1.2); // y向上抬高
                    living.hurtMarked = true;

                    // 失明3秒
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));

                    // 铁砧落地音效
                    world.playSound(null, living.getX(), living.getY(), living.getZ(),
                            ModSounds.PLAYER_ATTACK_SOUND_EVENT, SoundSource.HOSTILE, 1.8F, 0.7F + world.random.nextFloat() * 0.4F);

                    // 可选：粒子冲击
                    world.sendParticles(ParticleTypes.EXPLOSION, living.getX(), living.getY() + 1, living.getZ(), 8, 0.6, 0.6, 0.6, 0.1);
                    break;
                }
            }
        }
    }
    @Unique
    private static final float SCALE = 1.5F;   // 尺寸倍率，可自行修改

    // ==================== 1. 血量改为 50000 ====================
    @ModifyArg(
            method = "createAttributes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
                    ordinal = 0
            )
    )
    private static double modifyMaxHealth(double original) {
        return 50000.0;
    }

    // ==================== 2. 整体尺寸放大 1.5 倍（碰撞箱完美放大） ====================
    @Redirect(
            method = "<init>",
            at = @At(value = "NEW", target = "Lnet/minecraft/world/entity/boss/EnderDragonPart;")
    )
    private EnderDragonPart scalePart(EnderDragon owner, String name, float width, float height) {
        return new EnderDragonPart(owner, name, width * SCALE, height * SCALE);
    }

    // ==================== 3. 对爆炸伤害 60% 免伤（实际只承受40%） ====================
    @ModifyVariable(
            method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float reduceExplosionDamage(float amount, ServerLevel world, EnderDragonPart part, DamageSource source) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return amount * 0.4F;
        }
        return amount;
    }

    // ==================== 4. 翅膀尖端持续紫色粒子（每 tick 生成，更明显） ====================
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void addWingTipParticles(CallbackInfo ci) {
        EnderDragon dragon = (EnderDragon) (Object) this;
        if (dragon.isDeadOrDying()) return;
        if (dragon.level().isClientSide) {
            // 分别为左右翅膀生成尾迹
            EnderDragonPart[] parts = dragon.getSubEntities();
            if (parts.length > 7) {
                spawnJetParticle(parts[7], true);
                spawnJetParticle(parts[6], false);
            }
        }
    }
    @Unique
    private double lastLeftWingX, lastLeftWingY, lastLeftWingZ;
    @Unique
    private double lastRightWingX, lastRightWingY, lastRightWingZ;
    @Unique
    private void spawnJetParticle(EnderDragonPart wing, boolean isLeft) {
        Level world = wing.level();

        // 获取当前位置
        double curX = wing.getX();
        double curY = wing.getY() + (wing.getBbHeight() / 2.0); // 从翅膀中部喷出
        double curZ = wing.getZ();

        // 获取上一帧位置（如果是第一次执行则初始化）
        double prevX = isLeft ? (lastLeftWingX == 0 ? curX : lastLeftWingX) : (lastRightWingX == 0 ? curX : lastRightWingX);
        double prevY = isLeft ? (lastLeftWingY == 0 ? curY : lastLeftWingY) : (lastRightWingY == 0 ? curY : lastRightWingY);
        double prevZ = isLeft ? (lastLeftWingZ == 0 ? curZ : lastLeftWingZ) : (lastRightWingZ == 0 ? curZ : lastRightWingZ);

        // 插值生成：在上一帧和这一帧之间填满粒子，保证高速移动下尾迹不断裂
        int particlesPerTick = 8;
        for (int i = 0; i < particlesPerTick; i++) {
            float f = (float)i / (float)particlesPerTick;
            double x = prevX + (curX - prevX) * f;
            double y = prevY + (curY - prevY) * f;
            double z = prevZ + (curZ - prevZ) * f;

            // 1. 核心浓烟效果 (DRAGON_BREATH)
            world.addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    x, y, z,
                    (world.random.nextDouble() - 0.5) * 0.1, // 稍微抖动
                    -0.02,
                    (world.random.nextDouble() - 0.5) * 0.1
            );

            // 2. 边缘闪烁效果 (REVERSE_PORTAL) - 只有部分粒子生成这个，增加层次感
            if (world.random.nextFloat() > 0.7f) {
                world.addParticle(
                        ParticleTypes.REVERSE_PORTAL,
                        x, y, z,
                        0, 0, 0
                );
            }
        }

        // 更新历史坐标
        if (isLeft) {
            lastLeftWingX = curX; lastLeftWingY = curY; lastLeftWingZ = curZ;
        } else {
            lastRightWingX = curX; lastRightWingY = curY; lastRightWingZ = curZ;
        }
    }

    @Inject(method = "knockBack", at = @At("HEAD"), cancellable = true)
    private void cancelLaunchLivingEntities(ServerLevel world, List<Entity> entities, CallbackInfo ci) {
        ci.cancel();
        double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                if (livingEntity.isAlliedTo(this)) continue;
                double f = entity.getX() - d;
                double g = entity.getZ() - e;
                double h = Math.max(f * f + g * g, 0.1);
                entity.push(f / h * 4.0, 0.2F, g / h * 4.0);
                if (this.phaseManager.getCurrentPhase() != null && !this.phaseManager.getCurrentPhase().isSitting() && livingEntity.getLastHurtByMobTimestamp() < entity.tickCount - 2) {
                    DamageSource damageSource = this.damageSources().mobAttack(this);
                    entity.hurtServer(world, damageSource, 5.0F);
                    EnchantmentHelper.doPostAttackEffects(world, entity, damageSource);
                }
            }
        }
    }
    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private void cancelDamageLivingEntities(ServerLevel world, List<Entity> entities, CallbackInfo ci) {
        ci.cancel();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                if (entity.isAlliedTo(this)) continue;
                DamageSource damageSource = this.damageSources().mobAttack(this);
                entity.hurtServer(world, damageSource, 10.0F);
                EnchantmentHelper.doPostAttackEffects(world, entity, damageSource);
            }
        }
    }
    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void writeCustomData(ValueOutput view, CallbackInfo ci) {
        view.putBoolean("IsShadow", this.custom$isShadow());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readCustomData(ValueInput view, CallbackInfo ci) {
        this.custom$setShadow(view.getBooleanOr("IsShadow", false));
    }
    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void removeAllDragonInvulnerability(
            ServerLevel world,
            EnderDragonPart part,
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (source.getDirectEntity() instanceof EnderDragon || source.getEntity() instanceof EnderDragon) {
            cir.setReturnValue(false);
            return;
        }
        EnderDragon self = (EnderDragon)(Object)this;
        if (amount <= 0.0F) {
            cir.setReturnValue(false);
            return;
        }
        this.reallyHurt(world, source, amount);
        // 保留原版死亡切相逻辑，避免直接把整套 Boss death sequence 弄坏
        if (self.getPhaseManager().getCurrentPhase() != null && self.isDeadOrDying() && self.getPhaseManager().getCurrentPhase().getPhase() != EnderDragonPhase.DYING) {
            self.setHealth(1.0F);
            self.getPhaseManager().setPhase(EnderDragonPhase.DYING);
        }

        cir.setReturnValue(true);
    }
}
