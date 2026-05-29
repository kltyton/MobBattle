package com.kltyton.mob_battle.entity.villager.militia;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.ai.goal.GeneralProtectionVillagerGoal;
import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.List;

// 杩戞垬鏉戞皯
public class MilitiaWarriorVillager extends IronGolem implements ModBaseIronGolemEntity {
    public static AttributeSupplier.Builder createVillagerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ATTACK_DAMAGE, 10);
    }
    // 娣诲姞缇や綋浠囨仺鐨勬娴嬭寖鍥达紙64鏍硷級
    private static final double ALERT_RANGE = 64.0;

    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(MilitiaWarriorVillager.class, EntityDataSerializers.BLOCK_POS);
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
                    // 1. 鑾峰彇瀹炰綋褰撳墠浣嶇疆鐨勭兢绯绘敞鍐岄」
                    Holder<Biome> biomeEntry = this.level().getBiome(this.blockPosition());
                    // 2. 鏍规嵁缇ょ郴鑾峰彇瀵瑰簲鐨勬潙姘戠被鍨?(渚嬪锛氭矙婕犮€侀洩鍦般€佸钩鍘熺瓑)
                    ResourceKey<VillagerType> type = VillagerType.byBiome(biomeEntry);
                    // 3. 璁剧疆鏉戞皯鐨勮亴涓氭暟鎹紝淇濈暀榛樿鑱屼笟锛堟垨璁惧畾涓烘棤涓氾級锛屼絾鏇存柊澶栬绫诲瀷
                    Holder<VillagerType> typeEntry = BuiltInRegistries.VILLAGER_TYPE.getOrThrow(type);
                    villager.setVillagerData(villager.getVillagerData().withType(typeEntry));

                    villager.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    this.level().addFreshEntity(villager);

                    EntityUtil.joinSameTeam(villager, this);
                    this.discard();
                }
            }
        }
    }
    public MilitiaWarriorVillager(EntityType<? extends IronGolem> entityType, Level world) {
        super(entityType, world);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        ItemStack itemStack = this.getWeaponItem();
        world.broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        DamageSource damageSource = this.damageSources().mobAttack(this);
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;

        boolean bl = target.hurtServer(world, damageSource, g);
        if (bl) {
            double d = target instanceof LivingEntity livingEntity
                    ? livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
                    : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setDeltaMovement(target.getDeltaMovement().add(0.1 * e, 0.0, 0.1 * e));
            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
        }
        return bl;
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
        if (bl && !world.isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                this.alertOthers(livingAttacker);
            }
        }
        return bl;
    }
    private void alertOthers(LivingEntity attacker) {
        List<IronGolem> golems = this.level().getEntitiesOfClass(
                IronGolem.class,
                this.getBoundingBox().inflate(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (IronGolem golem : golems) {
            if (attacker instanceof AbstractGolem) {
                continue;
            }

            golem.setPersistentAngerTarget(attacker.getUUID());
            golem.startPersistentAngerTimer();

            // 绔嬪嵆鏇存柊鐩爣閫夋嫨
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public static boolean checkWarriorSpawnRules(EntityType<MilitiaWarriorVillager> warriorVillagerEntityType, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return world.getCurrentDifficultyAt(pos).getDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 娣诲姞娓告吵AI
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
        this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(1, new GeneralProtectionVillagerGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity, world) -> entity instanceof Enemy));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
    }

}
