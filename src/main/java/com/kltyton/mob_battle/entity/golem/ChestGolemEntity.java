package com.kltyton.mob_battle.entity.golem;

import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChestGolemEntity extends IronGolem implements Container, MenuProvider, OwnedSummon {
    private static final int INVENTORY_SIZE = 27;
    private static final double FOLLOW_OWNER_DISTANCE_SQ = 8.0D * 8.0D;
    private static final double FOLLOW_OWNER_SPEED = 1.1D;
    private static final EntityDataAccessor<Boolean> HAS_VINES =
            SynchedEntityData.defineId(ChestGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER =
            SynchedEntityData.defineId(ChestGolemEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public ChestGolemEntity(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
        this.setPlayerCreated(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_VINES, true);
        builder.define(OWNER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (target, world) -> target instanceof Enemy
                        && EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            updateOwnerBehavior();
        }
    }

    private void updateOwnerBehavior() {
        Entity ownerEntity = getSummonOwner();
        if (!(ownerEntity instanceof LivingEntity owner) || !owner.isAlive()) {
            return;
        }
        LivingEntity ownerTarget = owner instanceof Mob mob ? mob.getTarget() : owner.getLastHurtMob();
        if (!trySetOwnerTarget(ownerTarget)) {
            trySetOwnerTarget(owner.getLastHurtByMob());
        }
        if (this.getTarget() == null && this.distanceToSqr(owner) > FOLLOW_OWNER_DISTANCE_SQ) {
            this.getNavigation().moveTo(owner, FOLLOW_OWNER_SPEED);
        }
    }

    private boolean trySetOwnerTarget(@Nullable LivingEntity target) {
        if (target != null && EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target)) {
            this.setTarget(target);
            return true;
        }
        return false;
    }

    public boolean hasVines() {
        return this.entityData.get(HAS_VINES);
    }

    public void setHasVines(boolean hasVines) {
        this.entityData.set(HAS_VINES, hasVines);
    }

    public void setSummonOwner(@Nullable LivingEntity owner) {
        this.entityData.set(OWNER, Optional.ofNullable(owner).map(EntityReference::of));
        if (owner != null) {
            EntityUtil.joinSameTeam(this, owner);
        }
    }

    @Override
    public @Nullable Entity getSummonOwner() {
        return this.entityData.get(OWNER)
                .map(ref -> ref.getEntity(this.level(), LivingEntity.class))
                .orElse(null);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target) && super.canAttack(target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        level.broadcastEntityEvent(this, (byte) 4);
        boolean hurt = target.hurtServer(level, this.damageSources().mobAttack(this), 4.0F);
        if (hurt) {
            this.playSound(net.minecraft.sounds.SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        }
        return hurt;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && hasVines()) {
            if (!this.level().isClientSide()) {
                setHasVines(false);
                stack.hurtAndBreak(1, player, hand);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ItemTags.PLANKS) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide()) {
                this.heal(15.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(net.minecraft.sounds.SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        if (getSummonOwner() == null) {
            setSummonOwner(player);
        }
        if (!this.level().isClientSide()) {
            player.openMenu(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getType().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, count);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("HasVines", hasVines());
        EntityReference.store(this.entityData.get(OWNER).orElse(null), output, "Owner");
        ContainerHelper.saveAllItems(output, this.items, false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setHasVines(input.getBooleanOr("HasVines", true));
        this.entityData.set(OWNER, Optional.ofNullable(EntityReference.readWithOldOwnerConversion(input, "Owner", this.level())));
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            Containers.dropContents(this.level(), this, this);
        }
        super.die(source);
    }
}
