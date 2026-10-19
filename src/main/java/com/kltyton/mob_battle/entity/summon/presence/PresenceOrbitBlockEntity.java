package com.kltyton.mob_battle.entity.summon.presence;

import com.kltyton.mob_battle.items.armor.presence.PresenceArmorSkills;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.Optional;

/** 五个弹药槽的可见环绕方块；数量由服务器套装状态控制，不保存重复的实体副本。 */
public final class PresenceOrbitBlockEntity extends Entity implements BlockAppearance {
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER =
            SynchedEntityData.defineId(PresenceOrbitBlockEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Integer> SLOT =
            SynchedEntityData.defineId(PresenceOrbitBlockEntity.class, EntityDataSerializers.INT);

    public PresenceOrbitBlockEntity(EntityType<? extends PresenceOrbitBlockEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void configure(LivingEntity owner, int slot) {
        this.entityData.set(OWNER, Optional.of(EntityReference.of(owner)));
        this.entityData.set(SLOT, slot);
        setPos(owner.position().add(0, 1, 0));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER, Optional.empty());
        builder.define(SLOT, 0);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.entityData.get(OWNER).map(ref -> ref.getEntity(level(), LivingEntity.class)).orElse(null);
        if (owner == null) {
            if (!level().isClientSide()) discard();
            return;
        }
        if (!level().isClientSide() && (!owner.isAlive() || !PresenceArmorSkills.isEquipped(owner))) {
            discard();
            return;
        }
        double angle = level().getGameTime() * 0.065 + entityData.get(SLOT) * Math.PI * 2 / 5;
        setPos(owner.getX() + Math.cos(angle) * 1.7, owner.getY() + 1.0 + Math.sin(angle * 2) * 0.12,
                owner.getZ() + Math.sin(angle) * 1.7);
    }

    @Override public BlockState visualBlockState() { return Blocks.GRAY_CONCRETE.defaultBlockState(); }
    @Override public float visualScale() { return 0.45F; }

    /** 环绕物是服务器弹药槽的表现，没有独立生命值；投掷操作负责消耗对应槽位。 */
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) { return false; }

    // 类型声明 noSave；存档中只保存玩家弹药数量，登录时由套装恢复可见环绕物。
    @Override protected void readAdditionalSaveData(ValueInput input) {}
    @Override protected void addAdditionalSaveData(ValueOutput output) {}
}
