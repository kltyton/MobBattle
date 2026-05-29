package com.kltyton.mob_battle.block.doubleblock.scarecrow;

import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillager;
import com.kltyton.mob_battle.utils.EntityUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class ScarecrowBlockEntity extends BlockEntity {
    private List<UUID> trackedGolems = new ArrayList<>();
    private static final int MAX_GOLEMS = 2;
    public static final double RANGE =256.0;
    private static final double DETECT_VILLAGER_RANGE = 5.0;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCARECROW_ENTITY, pos, state);
    }

    // 每秒运行一次逻辑 (20 ticks)，节省性能
    public static void tick(Level world, BlockPos pos, BlockState state, ScarecrowBlockEntity be) {
        if (world.isClientSide || world.getGameTime() % 20 != 0) return;
        ServerLevel serverWorld = (ServerLevel) world;
        be.validateGolems(serverWorld, pos);
        if (be.trackedGolems.size() < MAX_GOLEMS) {
            be.tryTransformVillager(serverWorld, pos);
        }
    }

    private void validateGolems(ServerLevel world, BlockPos pos) {
        Iterator<UUID> iterator = trackedGolems.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = world.getEntity(uuid);

            // 如果实体不存在、不是铁傀儡、已死亡、或距离超过64格，则移除
            if (!(entity instanceof MilitiaWarriorVillager golem) || !golem.isAlive()) {
                iterator.remove();
                setChanged();
            }
        }
    }

    private void tryTransformVillager(ServerLevel world, BlockPos pos) {
        AABB box = new AABB(pos).inflate(DETECT_VILLAGER_RANGE);
        List<Villager> villagers = world.getEntitiesOfClass(Villager.class, box,
                v -> !v.isBaby() && v.getVillagerData().profession().is(VillagerProfession.NONE));

        if (!villagers.isEmpty()) {
            Villager villager = villagers.getFirst();
            // 转化
            MilitiaWarriorVillager golem = ModEntities.MILITIA_WARRIOR_VILLAGER.create(world, EntitySpawnReason.CONVERSION);
            if (golem != null) {
                golem.setHomePos(pos);
                golem.snapTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                world.addFreshEntity(golem);
                trackedGolems.add(golem.getUUID());
                EntityUtil.joinSameTeam(golem, villager);
                villager.discard();
                setChanged();
            }
        }
    }
    public void killTrackedGolems(ServerLevel world) {
        if (trackedGolems.isEmpty()) {
            return;
        }

        for (UUID uuid : trackedGolems) {
            Entity entity = world.getEntity(uuid);
            if (entity instanceof MilitiaWarriorVillager warrior && warrior.isAlive()) {
                warrior.setHomePos(new BlockPos(0, 9999, 0));
            }
        }

        trackedGolems.clear();
        setChanged();
    }
    public void applyGlowingToTracked(ServerLevel world, Player player) {
        if (trackedGolems.isEmpty()) {
            return;
        }
        for (UUID uuid : trackedGolems) {
            Entity entity = world.getEntity(uuid);
            if (entity instanceof MilitiaWarriorVillager warrior && entity.isAlive()) {
                warrior.addEffect(new MobEffectInstance(
                        MobEffects.GLOWING,
                        5 * 20,
                        0,
                        false,
                        false
                ));
            }
        }
    }
    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.store("TrackedGolems", UUIDUtil.AUTHLIB_CODEC.listOf(), this.trackedGolems);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.trackedGolems = new ArrayList<>(view.read("TrackedGolems", UUIDUtil.AUTHLIB_CODEC.listOf()).orElse(new ArrayList<>()));
    }
}
