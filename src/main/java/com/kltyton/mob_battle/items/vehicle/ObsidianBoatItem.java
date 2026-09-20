package com.kltyton.mob_battle.items.vehicle;

import com.kltyton.mob_battle.entity.vehicle.obsidianboat.ObsidianBoatEntity;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** 使用原版 BoatItem 的命中、碰撞和消耗语义生成黑曜石船。 */
public final class ObsidianBoatItem extends Item {
    private final EntityType<? extends ObsidianBoatEntity> entityType;

    public ObsidianBoatItem(EntityType<? extends ObsidianBoatEntity> entityType, Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        Vec3 view = player.getViewVector(1.0F);
        List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D),
                EntitySelector.CAN_BE_PICKED);
        Vec3 eyes = player.getEyePosition();
        for (Entity entity : entities) {
            AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
            if (box.contains(eyes)) {
                return InteractionResult.PASS;
            }
        }

        if (hit instanceof BlockHitResult blockHit) {
            ObsidianBoatEntity boat = this.entityType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (boat == null) {
                return InteractionResult.FAIL;
            }
            Vec3 location = blockHit.getLocation();
            boat.setInitialPos(location.x, location.y, location.z);
            boat.setYRot(player.getYRot());
            if (level instanceof ServerLevel serverLevel) {
                EntityType.<ObsidianBoatEntity>createDefaultStackConfig(serverLevel, stack, player).accept(boat);
            }
            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                level.addFreshEntity(boat);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, location);
                stack.consume(1, player);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
