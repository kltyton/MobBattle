package com.kltyton.mob_battle.items.misc;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;

public class IncubationEggItem extends SpawnEggItem {
    public IncubationEggItem(EntityType<? extends Mob> type, Properties settings) {
        super(type, settings);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            ItemStack itemStack = context.getItemInHand();
            BlockPos blockPos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockState = world.getBlockState(blockPos);
            if (world.getBlockEntity(blockPos) instanceof Spawner spawner) {
                EntityType<?> entityType = this.getType(world.registryAccess(), itemStack);
                spawner.setEntityId(entityType, world.getRandom());
                world.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
                world.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.shrink(1);
            } else {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.relative(direction);
                }

                EntityType<?> entityType = this.getType(world.registryAccess(), itemStack);
                // 生成实体并尝试驯服
                Entity entity = entityType.spawn(
                        (ServerLevel) world,
                        itemStack,
                        context.getPlayer(),
                        blockPos2,
                        EntitySpawnReason.SPAWN_ITEM_USE,
                        true,
                        !Objects.equals(blockPos, blockPos2) && direction == Direction.UP
                );
                if (entity != null) {
                    // 检查是否可驯服并设置主人
                    if (entity instanceof HighbirdEggEntity tameable) {
                        tameable.isIncubating = true;
                        tameable.tame(context.getPlayer());
                        tameable.setOwner(context.getPlayer());
                    }
                    itemStack.shrink(1);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
                }

            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else if (world instanceof ServerLevel serverWorld) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
                return InteractionResult.PASS;
            } else if (world.mayInteract(user, blockPos) && user.mayUseItemAt(blockPos, blockHitResult.getDirection(), itemStack)) {
                EntityType<?> entityType = this.getType(serverWorld.registryAccess(), itemStack);
                Entity entity = entityType.spawn(serverWorld, itemStack, user, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
                if (entity == null) {
                    return InteractionResult.PASS;
                } else {
                    // 检查是否可驯服并设置主人
                    if (entity instanceof HighbirdEggEntity tameable) {
                        tameable.isIncubating = true;
                        tameable.tame(user);
                        tameable.setOwner(user);
                    }
                    itemStack.consume(1, user);
                    user.awardStat(Stats.ITEM_USED.get(this));
                    world.gameEvent(user, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.SUCCESS;
        }
    }
}
