package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonShadowEntity;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonSkillManager;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnderDragonEntity.class)
@Implements(@Interface(iface = EnderDragonAccessor.class, prefix = "custom$"))
public abstract class EnderDragonEntityMixin extends MobEntity {
    // ==================== 新增字段（放在类顶部） ====================
    @Unique
    private static final TrackedData<Boolean> IS_SHADOW = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private EnderDragonShadowEntity shadow;
    @Unique
    private long shadowRespawnTime = -1L;
    @Unique
    public boolean custom$isShadow() {
        return this.getDataTracker().get(IS_SHADOW);
    }
    @Unique
    public void custom$setShadow(boolean shadow) {
        this.getDataTracker().set(IS_SHADOW, shadow);
    }
    @Inject(method = "initDataTracker", at = @At("RETURN"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IS_SHADOW, false);
    }
    @Shadow
    @Final
    public EnderDragonPart body;

    @Shadow
    @Final
    public EnderDragonPart head;

    @Shadow
    @Final
    private PhaseManager phaseManager;

    @Shadow
    protected abstract void parentDamage(ServerWorld world, DamageSource source, float amount);

    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private EnderDragonSkillManager skillManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSkillManager(CallbackInfo ci) {
        EnderDragonEntity self = (EnderDragonEntity) (Object) this;
        this.skillManager = new EnderDragonSkillManager(self);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickSkills(CallbackInfo ci) {
        EnderDragonEntity self = (EnderDragonEntity) (Object) this;
        if (self.getWorld() instanceof ServerWorld serverWorld) {
            skillManager.tick(serverWorld);
        }
    }
    // ==================== 新增：50%血量召唤 + 复活 + 死亡联动 ====================
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void manageShadowDragon(CallbackInfo ci) {
        EnderDragonEntity self = (EnderDragonEntity) (Object) this;
        if (!(self.getWorld() instanceof ServerWorld world)) return;
        if (self instanceof EnderDragonShadowEntity) return;
        if (self.isDead()) {
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
        if (shadowRespawnTime > 0 && world.getTime() >= shadowRespawnTime && shadow == null) {
            summonShadow(world, self);
            shadowRespawnTime = -1L;
        }
    }
    @Unique
    private void summonShadow(ServerWorld world, EnderDragonEntity owner) {
        EnderDragonShadowEntity newShadow = new EnderDragonShadowEntity(EntityType.ENDER_DRAGON, world);
        newShadow.setOwner(owner);
        newShadow.refreshPositionAndAngles(
                owner.getX() + 12 + world.random.nextDouble() * 8,
                owner.getY() + 10,
                owner.getZ() + 12 + world.random.nextDouble() * 8,
                world.random.nextFloat() * 360, 0
        );
        newShadow.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        newShadow.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10000);
        newShadow.setHealth(10000);
        ((EnderDragonAccessor) newShadow).setShadow(true);
        world.spawnEntity(newShadow);
        this.shadow = newShadow;
        // 特效
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, newShadow.getX(), newShadow.getY() + 5, newShadow.getZ(), 1, 0, 0, 0, 0);
        world.playSound(null, newShadow.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 6.0F, 0.8F);
    }
    @Inject(method = "tickMovement", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
            shift = At.Shift.AFTER))
    private void checkRushCollision(CallbackInfo ci) {
        EnderDragonEntity self = (EnderDragonEntity) (Object) this;
        if (!(self.getWorld() instanceof ServerWorld world)) return;

        if (skillManager != null && skillManager.isChargingRush()) {
            // 检测头部/身体附近实体（类似原版翅膀碰撞，但更强）
            Box headBox = this.head.getBoundingBox().expand(2.0, 1.5, 2.0);
            Box bodyBox = this.body.getBoundingBox().expand(3.0, 2.0, 3.0);
            if (world.getTime() > skillManager.rushEndTime) return;
            for (Entity entity : world.getOtherEntities(self, headBox.union(bodyBox))) {
                if (entity instanceof LivingEntity living && !entity.isSpectator() && living != self) {
                    if (living.isTeammate(self)) continue;  // 跳过队友
                    // 330点物理伤害（用mobAttack来源）
                    DamageSource source = self.getDamageSources().mobAttack(self);
                    living.damage(world, source, 330.0F);

                    // 击退翻倍（原版冲撞击退 ≈ 2~3，这里翻倍 ≈ 4~6）
                    Vec3d knockDir = living.getPos().subtract(self.getPos()).normalize();
                    living.addVelocity(knockDir.x * 1.2, 0.8, knockDir.z * 1.2); // y向上抬高
                    living.velocityModified = true;

                    // 失明3秒
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));

                    // 铁砧落地音效
                    world.playSound(null, living.getX(), living.getY(), living.getZ(),
                            ModSounds.PLAYER_ATTACK_SOUND_EVENT, SoundCategory.HOSTILE, 1.8F, 0.7F + world.random.nextFloat() * 0.4F);

                    // 可选：粒子冲击
                    world.spawnParticles(ParticleTypes.EXPLOSION, living.getX(), living.getY() + 1, living.getZ(), 8, 0.6, 0.6, 0.6, 0.1);
                    break;
                }
            }
        }
    }
    @Unique
    private static final float SCALE = 1.5F;   // 尺寸倍率，可自行修改

    // ==================== 1. 血量改为 50000 ====================
    @ModifyArg(
            method = "createEnderDragonAttributes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;add(Lnet/minecraft/registry/entry/RegistryEntry;D)Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;",
                    ordinal = 0
            )
    )
    private static double modifyMaxHealth(double original) {
        return 50000.0;
    }

    // ==================== 2. 整体尺寸放大 1.5 倍（碰撞箱完美放大） ====================
    @Redirect(
            method = "<init>",
            at = @At(value = "NEW", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonPart;")
    )
    private EnderDragonPart scalePart(EnderDragonEntity owner, String name, float width, float height) {
        return new EnderDragonPart(owner, name, width * SCALE, height * SCALE);
    }

    // ==================== 3. 对爆炸伤害 60% 免伤（实际只承受40%） ====================
    @ModifyVariable(
            method = "damagePart",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float reduceExplosionDamage(float amount, ServerWorld world, EnderDragonPart part, DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return amount * 0.4F;
        }
        return amount;
    }

    // ==================== 4. 翅膀尖端持续紫色粒子（每 tick 生成，更明显） ====================
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void addWingTipParticles(CallbackInfo ci) {
        EnderDragonEntity dragon = (EnderDragonEntity) (Object) this;
        if (dragon.isDead()) return;
        if (dragon.getWorld().isClient) {
            // 分别为左右翅膀生成尾迹
            spawnJetParticle(dragon.leftWing, true);
            spawnJetParticle(dragon.rightWing, false);
        }
    }
    @Unique
    private double lastLeftWingX, lastLeftWingY, lastLeftWingZ;
    @Unique
    private double lastRightWingX, lastRightWingY, lastRightWingZ;
    @Unique
    private void spawnJetParticle(EnderDragonPart wing, boolean isLeft) {
        World world = wing.getWorld();

        // 获取当前位置
        double curX = wing.getX();
        double curY = wing.getY() + (wing.getHeight() / 2.0); // 从翅膀中部喷出
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
            world.addParticleClient(
                    ParticleTypes.DRAGON_BREATH,
                    x, y, z,
                    (world.random.nextDouble() - 0.5) * 0.1, // 稍微抖动
                    -0.02,
                    (world.random.nextDouble() - 0.5) * 0.1
            );

            // 2. 边缘闪烁效果 (REVERSE_PORTAL) - 只有部分粒子生成这个，增加层次感
            if (world.random.nextFloat() > 0.7f) {
                world.addParticleClient(
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

    @Inject(method = "launchLivingEntities", at = @At("HEAD"), cancellable = true)
    private void cancelLaunchLivingEntities(ServerWorld world, List<Entity> entities, CallbackInfo ci) {
        ci.cancel();
        double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isTeammate(this)) continue;
                double f = entity.getX() - d;
                double g = entity.getZ() - e;
                double h = Math.max(f * f + g * g, 0.1);
                entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
                if (this.phaseManager.getCurrent() != null && !this.phaseManager.getCurrent().isSittingOrHovering() && livingEntity.getLastAttackedTime() < entity.age - 2) {
                    DamageSource damageSource = this.getDamageSources().mobAttack(this);
                    entity.damage(world, damageSource, 5.0F);
                    EnchantmentHelper.onTargetDamaged(world, entity, damageSource);
                }
            }
        }
    }
    @Inject(method = "damageLivingEntities", at = @At("HEAD"), cancellable = true)
    private void cancelDamageLivingEntities(ServerWorld world, List<Entity> entities, CallbackInfo ci) {
        ci.cancel();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                if (entity.isTeammate(this)) continue;
                DamageSource damageSource = this.getDamageSources().mobAttack(this);
                entity.damage(world, damageSource, 10.0F);
                EnchantmentHelper.onTargetDamaged(world, entity, damageSource);
            }
        }
    }
    @Inject(method = "writeCustomData", at = @At("RETURN"))
    protected void writeCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("IsShadow", this.custom$isShadow());
    }

    @Inject(method = "readCustomData", at = @At("RETURN"))
    protected void readCustomData(ReadView view, CallbackInfo ci) {
        this.custom$setShadow(view.getBoolean("IsShadow", false));
    }
    @Inject(method = "damagePart", at = @At("HEAD"), cancellable = true)
    private void removeAllDragonInvulnerability(
            ServerWorld world,
            EnderDragonPart part,
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        EnderDragonEntity self = (EnderDragonEntity)(Object)this;
        if (amount <= 0.0F) {
            cir.setReturnValue(false);
            return;
        }
        this.parentDamage(world, source, amount);
        // 保留原版死亡切相逻辑，避免直接把整套 Boss death sequence 弄坏
        if (self.getPhaseManager().getCurrent() != null && self.isDead() && self.getPhaseManager().getCurrent().getType() != PhaseType.DYING) {
            self.setHealth(1.0F);
            self.getPhaseManager().setPhase(PhaseType.DYING);
        }

        cir.setReturnValue(true);
    }
}
