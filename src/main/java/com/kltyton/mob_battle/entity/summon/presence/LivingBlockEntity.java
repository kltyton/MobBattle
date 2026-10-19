package com.kltyton.mob_battle.entity.summon.presence;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.items.armor.presence.PresenceArmorSkills;
import com.kltyton.mob_battle.items.armor.presence.PresenceBondLedger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import java.util.Comparator;
import java.util.UUID;

/** 使用原版导航、近战和实体碰撞的滚动方块；生命预留与区块加载状态相互独立。 */
public final class LivingBlockEntity extends PathfinderMob implements OwnedSummon, BlockAppearance {
    private static final EntityDataAccessor<BlockState> BLOCK =
            SynchedEntityData.defineId(LivingBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final Identifier PENETRATION = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "living_block_penetration");
    private UUID ownerId;
    private int healthCost = 2;
    private boolean contractReleased;
    private float roll;
    private float previousRoll;
    private int ownerAttackTime;
    private int ownerHurtTime;
    private int selfHurtTime;
    private boolean combatMemoryInitialized;

    public LivingBlockEntity(EntityType<? extends LivingBlockEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    /** 最大生命和伤害在绑定前按持有方块硬度覆写，默认值仅供类型属性注册。 */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.ATTACK_DAMAGE, 30).add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 24).add(Attributes.STEP_HEIGHT, 1.0);
    }

    public void configure(ServerPlayer owner, BlockState block, int cost, double health, double damage) {
        ownerId = owner.getUUID();
        healthCost = cost;
        entityData.set(BLOCK, block);
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        setHealth((float) health);
        EntityQueries.joinSameTeam(this, owner);
        resetCombatMemory(owner);
    }

    public @Nullable UUID ownerId() { return ownerId; }

    /** 切换模式时停止原先主动取得的目标，之后只响应新的护主或自卫事件。 */
    public void resetCombatMemory(ServerPlayer owner) {
        setTarget(null);
        ownerAttackTime = owner.getLastHurtMobTimestamp();
        ownerHurtTime = owner.getLastHurtByMobTimestamp();
        selfHurtTime = getLastHurtByMobTimestamp();
        combatMemoryInitialized = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BLOCK, Blocks.STONE.defaultBlockState());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15, true));
    }

    @Override
    public @Nullable Entity getSummonOwner() {
        return level() instanceof ServerLevel world && ownerId != null ? world.getServer().getPlayerList().getPlayer(ownerId) : null;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return EntityQueries.isValidSummonCombatTarget(this, getSummonOwner(), target) && super.canAttack(target);
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel world && isAlive()) {
            ServerPlayer owner = ownerId == null ? null : world.getServer().getPlayerList().getPlayer(ownerId);
            if (ownerId == null || !PresenceBondLedger.get(world.getServer()).contains(ownerId, getUUID())
                    || owner != null && (!owner.isAlive() || !PresenceArmorSkills.isEquipped(owner) || owner.level() != world)) {
                kill(world);
                return;
            }
            if (owner == null) {
                setTarget(null);
                getNavigation().stop();
            }
        }
        double lastX = getX();
        double lastZ = getZ();
        super.tick();
        previousRoll = roll;
        roll += (float) (Math.hypot(getX() - lastX, getZ() - lastZ) * 180.0 / Math.PI * 2);
        if (!(level() instanceof ServerLevel world) || !isAlive()) return;
        if (ownerId == null || !PresenceBondLedger.get(world.getServer()).contains(ownerId, getUUID())) {
            kill(world);
            return;
        }
        ServerPlayer owner = world.getServer().getPlayerList().getPlayer(ownerId);
        if (owner == null) {
            setTarget(null);
            getNavigation().stop();
            return;
        }
        if (!owner.isAlive() || !PresenceArmorSkills.isEquipped(owner) || owner.level() != world) {
            kill(world);
            return;
        }
        if (tickCount % 10 == 0) {
            if (!combatMemoryInitialized) resetCombatMemory(owner);
            LivingEntity target = getTarget();
            if (ownerAttackTime != owner.getLastHurtMobTimestamp()) target = owner.getLastHurtMob();
            if (ownerHurtTime != owner.getLastHurtByMobTimestamp()) target = owner.getLastHurtByMob();
            if (selfHurtTime != getLastHurtByMobTimestamp()) target = getLastHurtByMob();
            ownerAttackTime = owner.getLastHurtMobTimestamp();
            ownerHurtTime = owner.getLastHurtByMobTimestamp();
            selfHurtTime = getLastHurtByMobTimestamp();
            if (!canAttackOwnerTarget(target)) target = null;
            if (target == null && PresenceBondLedger.get(world.getServer()).preference(ownerId).aggressive()) {
                target = world.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(12), this::canAttack)
                        .stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
            }
            setTarget(target);
            if (target == null && distanceToSqr(owner) > 9) getNavigation().moveTo(owner, 1.1);
        }
    }

    private boolean canAttackOwnerTarget(@Nullable LivingEntity target) {
        return target != null && distanceToSqr(target) <= 24 * 24 && canAttack(target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (healthCost != 8 || !(target instanceof LivingEntity living)) return super.doHurtTarget(world, target);
        var armor = living.getAttribute(Attributes.ARMOR);
        if (armor == null) return super.doHurtTarget(world, target);
        armor.addTransientModifier(new AttributeModifier(PENETRATION, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        try {
            return super.doHurtTarget(world, target);
        } finally {
            armor.removeModifier(PENETRATION);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level() instanceof ServerLevel world && !contractReleased) {
            contractReleased = true;
            world.playSound(null, blockPosition(), visualBlockState().getSoundType().getBreakSound(), SoundSource.NEUTRAL, 1, 1);
            if (ownerId != null) {
                PresenceBondLedger.get(world.getServer()).release(ownerId, getUUID());
                ServerPlayer owner = world.getServer().getPlayerList().getPlayer(ownerId);
                if (owner != null) PresenceArmorSkills.updateReservedHealth(owner);
            }
        }
    }

    @Override public BlockState visualBlockState() { return entityData.get(BLOCK); }
    @Override public float visualScale() { return 0.9F; }
    @Override public float visualRoll(float partialTick) { return previousRoll + (roll - previousRoll) * partialTick; }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (ownerId != null) output.putString("PresenceOwner", ownerId.toString());
        output.putInt("PresenceHealthCost", healthCost);
        output.store("PresenceBlock", BlockState.CODEC, visualBlockState());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        String owner = input.getStringOr("PresenceOwner", "");
        ownerId = owner.isEmpty() ? null : UUID.fromString(owner);
        healthCost = input.getIntOr("PresenceHealthCost", 2);
        entityData.set(BLOCK, input.read("PresenceBlock", BlockState.CODEC).orElse(Blocks.STONE.defaultBlockState()));
    }
}
