package com.kltyton.mob_battle.items.misc;
import java.util.Objects;

import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class IncubationEggItem extends SpawnEggItem {
    public IncubationEggItem(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, settings);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            if (world.getBlockEntity(blockPos) instanceof Spawner spawner) {
                EntityType<?> entityType = this.getEntityType(world.getRegistryManager(), itemStack);
                spawner.setEntityType(entityType, world.getRandom());
                world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.decrement(1);
            } else {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.offset(direction);
                }

                EntityType<?> entityType = this.getEntityType(world.getRegistryManager(), itemStack);
                // 生成实体并尝试驯服
                Entity entity = entityType.spawnFromItemStack(
                        (ServerWorld) world,
                        itemStack,
                        context.getPlayer(),
                        blockPos2,
                        SpawnReason.SPAWN_ITEM_USE,
                        true,
                        !Objects.equals(blockPos, blockPos2) && direction == Direction.UP
                );
                if (entity != null) {
                    // 检查是否可驯服并设置主人
                    if (entity instanceof HighbirdEggEntity tameable) {
                        tameable.isIncubating = true;
                        tameable.setTamedBy(context.getPlayer());
                        tameable.setOwner(context.getPlayer());
                    }
                    itemStack.decrement(1);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
                }

            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        } else if (world instanceof ServerWorld serverWorld) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return ActionResult.PASS;
            } else if (world.canEntityModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                EntityType<?> entityType = this.getEntityType(serverWorld.getRegistryManager(), itemStack);
                Entity entity = entityType.spawnFromItemStack(serverWorld, itemStack, user, blockPos, SpawnReason.SPAWN_ITEM_USE, false, false);
                if (entity == null) {
                    return ActionResult.PASS;
                } else {
                    // 检查是否可驯服并设置主人
                    if (entity instanceof HighbirdEggEntity tameable) {
                        tameable.isIncubating = true;
                        tameable.setTamedBy(user);
                        tameable.setOwner(user);
                    }
                    itemStack.decrementUnlessCreative(1, user);
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());
                    return ActionResult.SUCCESS;
                }
            } else {
                return ActionResult.FAIL;
            }
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
