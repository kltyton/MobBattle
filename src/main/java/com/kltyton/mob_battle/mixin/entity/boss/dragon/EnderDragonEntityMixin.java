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
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
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
    // ==================== 鏂板瀛楁锛堟斁鍦ㄧ被椤堕儴锛?====================
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
    // ==================== 鏂板锛?0%琛€閲忓彫鍞?+ 澶嶆椿 + 姝讳骸鑱斿姩 ====================
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

        // 棣栨鎺夊埌50%浠ヤ笅鍙敜
        if (shadow == null && healthPercent <= 0.5F && shadowRespawnTime == -1L) {
            summonShadow(world, self);
        }

        // 澶嶆椿璁℃椂鍣?
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
                owner.getX() + 12 + world.getRandom().nextDouble() * 8,
                owner.getY() + 10,
                owner.getZ() + 12 + world.getRandom().nextDouble() * 8,
                world.getRandom().nextFloat() * 360, 0
        );
        newShadow.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        newShadow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        newShadow.setHealth(10000);
        ((EnderDragonAccessor) newShadow).setShadow(true);
        world.addFreshEntity(newShadow);
        this.shadow = newShadow;
        // 鐗规晥
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
            // 妫€娴嬪ご閮?韬綋闄勮繎瀹炰綋锛堢被浼煎師鐗堢繀鑶€纰版挒锛屼絾鏇村己锛?
            AABB headBox = this.head.getBoundingBox().inflate(2.0, 1.5, 2.0);
            AABB bodyBox = this.body.getBoundingBox().inflate(3.0, 2.0, 3.0);
            if (world.getGameTime() > skillManager.rushEndTime) return;
            for (Entity entity : world.getEntities(self, headBox.minmax(bodyBox))) {
                if (entity instanceof LivingEntity living && !entity.isSpectator() && living != self) {
                    if (living instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                    if (living.isAlliedTo(self)) continue;  // 璺宠繃闃熷弸
                    // 330鐐圭墿鐞嗕激瀹筹紙鐢╩obAttack鏉ユ簮锛?
                    DamageSource source = self.damageSources().mobAttack(self);
                    living.hurtServer(world, source, 330.0F);

                    // 鍑婚€€缈诲€嶏紙鍘熺増鍐叉挒鍑婚€€ 鈮?2~3锛岃繖閲岀炕鍊?鈮?4~6锛?
                    Vec3 knockDir = living.position().subtract(self.position()).normalize();
                    living.push(knockDir.x * 1.2, 0.8, knockDir.z * 1.2); // y鍚戜笂鎶珮
                    living.hurtMarked = true;

                    // 澶辨槑3绉?
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));

                    // 閾佺牕钀藉湴闊虫晥
                    world.playSound(null, living.getX(), living.getY(), living.getZ(),
                            ModSounds.PLAYER_ATTACK_SOUND_EVENT, SoundSource.HOSTILE, 1.8F, 0.7F + world.getRandom().nextFloat() * 0.4F);

                    // 鍙€夛細绮掑瓙鍐插嚮
                    world.sendParticles(ParticleTypes.EXPLOSION, living.getX(), living.getY() + 1, living.getZ(), 8, 0.6, 0.6, 0.6, 0.1);
                    break;
                }
            }
        }
    }
    @Unique
    private static final float SCALE = 1.5F;   // 灏哄鍊嶇巼锛屽彲鑷淇敼

    // ==================== 1. 琛€閲忔敼涓?50000 ====================
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

    // ==================== 2. 鏁翠綋灏哄鏀惧ぇ 1.5 鍊嶏紙纰版挒绠卞畬缇庢斁澶э級 ====================
    @Redirect(
            method = "<init>",
            at = @At(value = "NEW", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;")
    )
    private EnderDragonPart scalePart(EnderDragon owner, String name, float width, float height) {
        return new EnderDragonPart(owner, name, width * SCALE, height * SCALE);
    }

    // ==================== 3. 瀵圭垎鐐镐激瀹?60% 鍏嶄激锛堝疄闄呭彧鎵垮彈40%锛?====================
    @ModifyVariable(
            method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float reduceExplosionDamage(float amount, ServerLevel world, EnderDragonPart part, DamageSource source) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return amount * 0.4F;
        }
        return amount;
    }

    // ==================== 4. 缈呰唨灏栫鎸佺画绱壊绮掑瓙锛堟瘡 tick 鐢熸垚锛屾洿鏄庢樉锛?====================
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void addWingTipParticles(CallbackInfo ci) {
        EnderDragon dragon = (EnderDragon) (Object) this;
        if (dragon.isDeadOrDying()) return;
        if (dragon.level().isClientSide()) {
            // 鍒嗗埆涓哄乏鍙崇繀鑶€鐢熸垚灏捐抗
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

        // 鑾峰彇褰撳墠浣嶇疆
        double curX = wing.getX();
        double curY = wing.getY() + (wing.getBbHeight() / 2.0); // 浠庣繀鑶€涓儴鍠峰嚭
        double curZ = wing.getZ();

        // 鑾峰彇涓婁竴甯т綅缃紙濡傛灉鏄涓€娆℃墽琛屽垯鍒濆鍖栵級
        double prevX = isLeft ? (lastLeftWingX == 0 ? curX : lastLeftWingX) : (lastRightWingX == 0 ? curX : lastRightWingX);
        double prevY = isLeft ? (lastLeftWingY == 0 ? curY : lastLeftWingY) : (lastRightWingY == 0 ? curY : lastRightWingY);
        double prevZ = isLeft ? (lastLeftWingZ == 0 ? curZ : lastLeftWingZ) : (lastRightWingZ == 0 ? curZ : lastRightWingZ);

        // 鎻掑€肩敓鎴愶細鍦ㄤ笂涓€甯у拰杩欎竴甯т箣闂村～婊＄矑瀛愶紝淇濊瘉楂橀€熺Щ鍔ㄤ笅灏捐抗涓嶆柇瑁?
        int particlesPerTick = 8;
        for (int i = 0; i < particlesPerTick; i++) {
            float f = (float)i / (float)particlesPerTick;
            double x = prevX + (curX - prevX) * f;
            double y = prevY + (curY - prevY) * f;
            double z = prevZ + (curZ - prevZ) * f;

            // 1. 鏍稿績娴撶儫鏁堟灉 (DRAGON_BREATH)
            world.addParticle(
                    net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F),
                    x, y, z,
                    (world.getRandom().nextDouble() - 0.5) * 0.1, // 绋嶅井鎶栧姩
                    -0.02,
                    (world.getRandom().nextDouble() - 0.5) * 0.1
            );

            // 2. 杈圭紭闂儊鏁堟灉 (REVERSE_PORTAL) - 鍙湁閮ㄥ垎绮掑瓙鐢熸垚杩欎釜锛屽鍔犲眰娆℃劅
            if (world.getRandom().nextFloat() > 0.7f) {
                world.addParticle(
                        ParticleTypes.REVERSE_PORTAL,
                        x, y, z,
                        0, 0, 0
                );
            }
        }

        // 鏇存柊鍘嗗彶鍧愭爣
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
    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
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
        // 淇濈暀鍘熺増姝讳骸鍒囩浉閫昏緫锛岄伩鍏嶇洿鎺ユ妸鏁村 Boss death sequence 寮勫潖
        if (self.getPhaseManager().getCurrentPhase() != null && self.isDeadOrDying() && self.getPhaseManager().getCurrentPhase().getPhase() != EnderDragonPhase.DYING) {
            self.setHealth(1.0F);
            self.getPhaseManager().setPhase(EnderDragonPhase.DYING);
        }

        cir.setReturnValue(true);
    }
}

