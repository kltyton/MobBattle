package com.kltyton.mob_battle.entity.villager.militia;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.ai.goal.GeneralProtectionVillagerGoal;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

// 远程村民
public class MilitiaArcherVillager extends SnowGolem implements NeutralMob {
    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(MilitiaArcherVillager.class, EntityDataSerializers.BLOCK_POS);
    public BlockPos getHomePos() {
        return this.entityData.get(HOME_POS);
    }
    public void setHomePos(BlockPos pos) {
        this.entityData.set(HOME_POS, pos);
    }
    @Override
    public void saveWithoutId(ValueOutput view) {
        super.saveWithoutId(view);
        BlockPos homePos = this.getHomePos();
        if (homePos != null) {
            view.store("HomePos", BlockPos.CODEC, homePos);
        }
    }

    @Override
    public void load(ValueInput view) {
        super.load(view);
        setHomePos(view.read("HomePos", BlockPos.CODEC).orElse(new BlockPos(0, -9999, 0)));
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOME_POS, new BlockPos(0, -9999, 0));
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            this.heal(1f);
            if (getHomePos().equals(new BlockPos(0, 9999, 0))) {
                Villager villager = EntityType.VILLAGER.create(this.level(), EntitySpawnReason.CONVERSION);
                if (villager != null) {
                    // 1. 获取实体当前位置的群系注册项
                    Holder<Biome> biomeEntry = this.level().getBiome(this.blockPosition());
                    // 2. 根据群系获取对应的村民类型 (例如：沙漠、雪地、平原等)
                    ResourceKey<VillagerType> type = VillagerType.byBiome(biomeEntry);
                    // 3. 设置村民的职业数据，保留默认职业（或设定为无业），但更新外观类型
                    Holder<VillagerType> typeEntry = BuiltInRegistries.VILLAGER_TYPE.getOrThrow(type);
                    // RegistryEntry<VillagerType> typeEntry = this.getWorld().getRegistryManager().getOrThrow(RegistryKeys.VILLAGER_TYPE).getOrThrow(type);(另一种写法)
                    villager.setVillagerData(villager.getVillagerData().withType(typeEntry));
                    villager.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    this.level().addFreshEntity(villager);
                    EntityUtil.joinSameTeam(villager, this);
                    this.discard();
                }
            }
        }
    }
    @Nullable
    private UUID angryAt;
    private int angerTime;
    public static final UniformInt ANGER_TIME_RANGE;
    private static final double ALERT_RANGE = 64.0;
    public MilitiaArcherVillager(EntityType<? extends SnowGolem> entityType, Level world) {
        super(entityType, world);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 添加游泳AI
        this.targetSelector.addGoal(1, new GeneralProtectionVillagerGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
    @Override
    public boolean readyForShearing() {
        return false;
    }
    @Override
    public boolean hasPumpkin() {
        return false;
    }
    @Override
    public void setPumpkin(boolean hasPumpkin) {
        super.setPumpkin(false);
    }
    @Override
    public InteractionResult mobInteract (Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }
    public static boolean checkSnuffleSpawnRules(EntityType<MilitiaArcherVillager> snuffle, LevelAccessor world, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        return world.getCurrentDifficultyAt(pos).getDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!ModSkillEntityType.canSkill(this)) return;
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - (double)1.1F;
        double targetZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;
        Level world = this.level();

        if (world instanceof ServerLevel serverWorld) {
            // 创建箭实体
            Arrow arrowEntity = new Arrow(world, this, new ItemStack(Items.ARROW), this.getMainHandItem().getItem() == Items.BOW ? this.getMainHandItem() : null);
            ((ITrueDamageProjectile) arrowEntity).setTrueDamage(true, false);
            ItemStack itemStack = this.getWeaponItem();
            float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            f = EnchantmentHelper.modifyDamage(serverWorld, itemStack, target, this.damageSources().arrow(arrowEntity, this), f);
            f += itemStack.getItem().getAttackDamageBonus(target, f, this.damageSources().arrow(arrowEntity, this));
            // 设置箭的伤害
            arrowEntity.setBaseDamage(f);
            arrowEntity.setOwner(this);
            arrowEntity.setNoGravity(true);
            double adjustedY = targetY + distance * 0.5; // 调整箭的发射高度
            arrowEntity.shoot(targetX, adjustedY - arrowEntity.getY(), targetZ, 1.6F, 0.1F); // 调整速度和轨迹
            // 发射箭
            serverWorld.addFreshEntity(arrowEntity);
        }

        // 播放攻击音效
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
/*        if (bl && !world.isClient) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity) {
                this.alertOthers((LivingEntity)attacker);
            }
        }*/
        return bl;
    }
    public static AttributeSupplier.Builder createVillagerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ATTACK_DAMAGE, 15);
    }
    private void alertOthers(LivingEntity attacker) {
        // 获取64格范围内所有铁傀儡
        List<SnowGolem> golems = this.level().getEntitiesOfClass(
                SnowGolem.class,
                this.getBoundingBox().inflate(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (SnowGolem golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof AbstractGolem) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            this.setPersistentAngerTarget(attacker.getUUID());
            this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));

            // 立即更新目标选择
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.angryAt;
    }
    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }
    static {
        ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    }
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world); // 允许基础游泳
    }
}

