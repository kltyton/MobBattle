package com.kltyton.mob_battle.block.doubleblock;

import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ScarecrowBlockEntity extends BlockEntity {
    private List<UUID> trackedGolems = new ArrayList<>();
    private static final int MAX_GOLEMS = 2;
    public static final double RANGE =256.0;
    private static final double DETECT_VILLAGER_RANGE = 5.0;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCARECROW_ENTITY, pos, state);
    }

    // 每秒运行一次逻辑 (20 ticks)，节省性能
    public static void tick(World world, BlockPos pos, BlockState state, ScarecrowBlockEntity be) {
        if (world.isClient || world.getTime() % 20 != 0) return;
        ServerWorld serverWorld = (ServerWorld) world;
        be.validateGolems(serverWorld, pos);
        if (be.trackedGolems.size() < MAX_GOLEMS) {
            be.tryTransformVillager(serverWorld, pos);
        }
    }

    private void validateGolems(ServerWorld world, BlockPos pos) {
        Iterator<UUID> iterator = trackedGolems.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = world.getEntity(uuid);

            // 如果实体不存在、不是铁傀儡、已死亡、或距离超过64格，则移除
            if (!(entity instanceof MilitiaWarriorVillager golem) || !golem.isAlive() ||
                    golem.squaredDistanceTo(pos.toCenterPos()) > RANGE * RANGE) {
                iterator.remove();
                markDirty();
            }
        }
    }

    private void tryTransformVillager(ServerWorld world, BlockPos pos) {
        Box box = new Box(pos).expand(DETECT_VILLAGER_RANGE);
        List<VillagerEntity> villagers = world.getEntitiesByClass(VillagerEntity.class, box,
                v -> !v.isBaby() && v.getVillagerData().profession().matchesKey(VillagerProfession.NONE));

        if (!villagers.isEmpty()) {
            VillagerEntity villager = villagers.getFirst();
            // 转化
            MilitiaWarriorVillager golem = ModEntities.MILITIA_WARRIOR_VILLAGER.create(world, SpawnReason.CONVERSION);
            if (golem != null) {
                golem.setHomePos(pos);
                golem.refreshPositionAndAngles(villager.getX(), villager.getY(), villager.getZ(), villager.getYaw(), villager.getPitch());
                world.spawnEntity(golem);
                trackedGolems.add(golem.getUuid());
                villager.discard();
                markDirty();
            }
        }
    }

    public void applyGlowingToTracked(ServerWorld world, PlayerEntity player) {
        if (trackedGolems.isEmpty()) {
            return;
        }
        for (UUID uuid : trackedGolems) {
            Entity entity = world.getEntity(uuid);
            if (entity instanceof MilitiaWarriorVillager warrior && entity.isAlive()) {
                warrior.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING,
                        5 * 20,
                        0,
                        false,
                        false
                ));
            }
        }
    }
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("TrackedGolems", Uuids.CODEC.listOf(), this.trackedGolems);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.trackedGolems = new ArrayList<>(view.read("TrackedGolems", Uuids.CODEC.listOf()).orElse(new ArrayList<>()));
    }
}
