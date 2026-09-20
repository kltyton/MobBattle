package com.kltyton.mob_battle.entity.vehicle.obsidianboat;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 保留黑曜石船的耐火、熔岩浮力、耐久和水中沉降特性，同时遵循原版船的交互契约。
 *
 * <p>原版船每 tick 自动减少 1 点损坏值；玩家乘坐时本类再减少 1 点，使总修复速度
 * 精确为每 tick 2 点。实体继续使用原版同步损坏字段，但把摧毁阈值提高到 130，且把
 * 损坏值写入存档。受损后进入水中时持续施加向下速度，形成铁傀儡式沉底。</p>
 */
public final class ObsidianBoatEntity extends Boat {
    public static final float DAMAGE_THRESHOLD = 130.0F;
    private static final double LAVA_SURFACE_OFFSET = 0.1D;
    private static final double MAX_BUOYANCY_CORRECTION = 0.08D;
    private static final double WATER_SINK_SPEED = -0.08D;

    public ObsidianBoatEntity(EntityType<? extends ObsidianBoatEntity> type, Level level) {
        super(type, level, () -> ModItems.OBSIDIAN_BOAT);
    }

    @Override
    public void tick() {
        boolean damagedAtTickStart = this.getDamage() > 0.0F;
        super.tick();

        if (!this.level().isClientSide()
                && this.getFirstPassenger() instanceof Player
                && this.getDamage() > 0.0F) {
            this.setDamage(Math.max(0.0F, this.getDamage() - 1.0F));
        }

        double lavaSurface = findFluidSurface(FluidTags.LAVA);
        if (lavaSurface != -Double.MAX_VALUE) {
            Vec3 movement = this.getDeltaMovement();
            double targetY = lavaSurface - LAVA_SURFACE_OFFSET;
            double correction = Math.clamp((targetY - this.getY()) * 0.2D,
                    -MAX_BUOYANCY_CORRECTION, MAX_BUOYANCY_CORRECTION);
            this.setDeltaMovement(movement.x * 0.9D, movement.y + correction, movement.z * 0.9D);
        } else if (damagedAtTickStart && containsFluid(FluidTags.WATER)) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x * 0.9D,
                    Math.min(movement.y, WATER_SINK_SPEED), movement.z * 0.9D);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return containsFluid(FluidTags.LAVA) ? 0.0D : super.getDefaultGravity();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return super.interact(player, hand, location);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableToBase(source)) {
            return false;
        }

        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + damage * 10.0F);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        if ((creative || this.getDamage() < DAMAGE_THRESHOLD) && !this.shouldSourceDestroy(source)) {
            if (creative) {
                this.discard();
            }
        } else {
            this.destroy(level, source);
        }
        return true;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Damage", this.getDamage());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDamage(Math.max(0.0F, input.getFloatOr("Damage", 0.0F)));
    }

    private boolean containsFluid(TagKey<Fluid> fluidTag) {
        return findFluidSurface(fluidTag) != -Double.MAX_VALUE;
    }

    private double findFluidSurface(TagKey<Fluid> fluidTag) {
        AABB box = this.getBoundingBox();
        int minX = (int)Math.floor(box.minX);
        int maxX = (int)Math.ceil(box.maxX);
        int minY = (int)Math.floor(box.minY) - 1;
        int maxY = (int)Math.ceil(box.maxY);
        int minZ = (int)Math.floor(box.minZ);
        int maxZ = (int)Math.ceil(box.maxZ);
        double surface = -Double.MAX_VALUE;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    pos.set(x, y, z);
                    var fluid = this.level().getFluidState(pos);
                    if (fluid.is(fluidTag)) {
                        surface = Math.max(surface, y + fluid.getHeight(this.level(), pos));
                    }
                }
            }
        }
        return surface;
    }
}
