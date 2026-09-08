package com.kltyton.mob_battle.client;

import com.geckolib.animatable.GeoAnimatable;
import com.kltyton.mob_battle.client.lifecycle.ClientLevelLifecycleState;
import com.kltyton.mob_battle.entity.player.IGeoEntityAnimationTickInvoker;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerSkillAccessor;
import com.kltyton.mob_battle.skill.api.SkillEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 客户端 Gecko 动画 tick 热路径。
 *
 * <p>职责：客户端每个 tick 推进玩家与附近技能实体的 Gecko 动画渲染状态。
 * 候选实体集合由 {@link ClientEntityEvents} 的实体加载/卸载事件增量维护，tick
 * 只遍历候选快照，不再对 192 格范围执行逐帧全量 AABB 查询；实体技能状态改为
 * 通过 {@link SkillEntity} 契约读取，彻底移除对 hasSkill 的反射。
 */
public final class ClientGeckoAnimationTicker {
    private static final float PARTIAL_TICK = 0.0F;
    private static final double SCAN_RANGE = 192.0D;

    /**
     * 候选实体身份集合：当前客户端世界中“可能播放技能动画”的实体。
     *
     * <p>使用 IdentityHashMap 显式按实体身份去重，避免依赖 Entity 的
     * equals/hashCode 语义。集合只在客户端线程的
     * ENTITY_LOAD/ENTITY_UNLOAD 回调中修改；tick 通过快照数组遍历，
     * 因此不引入锁也不会产生迭代中的并发修改异常。
     */
    private static final Set<Entity> ANIMATION_CANDIDATES =
            Collections.newSetFromMap(new IdentityHashMap<>());

    /** 客户端世界生命周期状态；负责区分首次绑定、切换世界与断开。 */
    private static final ClientLevelLifecycleState<ClientLevel> LEVEL_STATE =
            new ClientLevelLifecycleState<>();

    private ClientGeckoAnimationTicker() {
    }

    public static void init() {
        ClientEntityEvents.ENTITY_LOAD.register(ClientGeckoAnimationTicker::onEntityLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(ClientGeckoAnimationTicker::onEntityUnload);
        ClientTickEvents.END_CLIENT_TICK.register(ClientGeckoAnimationTicker::tick);
    }

    /**
     * 实体加载进客户端世界时登记为候选。
     *
     * <p>登记条件与原 192 格查询的过滤一致：必须是 Gecko 可动画实体，且通过
     * {@link SkillEntity} 契约暴露技能状态。hasSkill 状态是动态的（由服务端
     * 同步的实体数据驱动），因此登记时不要求 hasSkill() 为 true，改由 tick
     * 时按契约实时判断，保证技能中途开始/结束的动画语义与旧实现一致。
     */
    private static void onEntityLoad(Entity entity, ClientLevel level) {
        if (isAnimationCandidate(entity)) {
            ANIMATION_CANDIDATES.add(entity);
        }
    }

    /** 实体从客户端世界卸载时移出候选集合，避免 tick 继续引用已卸载实体。 */
    private static void onEntityUnload(Entity entity, ClientLevel level) {
        ANIMATION_CANDIDATES.remove(entity);
    }

    private static boolean isAnimationCandidate(Entity entity) {
        return entity instanceof GeoAnimatable && entity instanceof SkillEntity;
    }

    private static void tick(Minecraft client) {
        ClientLevel currentLevel = client.level;
        if (currentLevel == null) {
            // 断开连接：世界引用为空，整体清空候选集合。
            refreshLevelContext(null);
            return;
        }
        if (client.player == null) {
            refreshLevelContext(currentLevel);
            return;
        }
        refreshLevelContext(currentLevel);
        if (client.isPaused()) {
            return;
        }

        tickPlayerIfNeeded(client);

        Player player = client.player;
        // 每个 tick 只生成一次扫描盒；候选遍历是 O(1) 的盒相交判断，
        // 不再是 level 级全量 AABB 查询。
        AABB scanBox = player.getBoundingBox().inflate(SCAN_RANGE);
        // 快照数组：迭代期间即使事件回调修改集合，也不会产生并发修改异常。
        Entity[] snapshot = ANIMATION_CANDIDATES.toArray(Entity[]::new);
        for (Entity entity : snapshot) {
            if (entity == player) {
                continue;
            }
            // 防御性生命周期检查：若卸载事件与快照之间出现遗漏，跳过已移除或
            // 不属于当前世界的实体，保证绝不触碰过期实体。
            if (entity.isRemoved() || entity.level() != currentLevel) {
                continue;
            }
            if (!scanBox.intersects(entity.getBoundingBox())) {
                continue;
            }
            if (entity instanceof SkillEntity skillEntity && skillEntity.hasSkill()) {
                tickEntityRenderer(client, entity);
            }
        }
    }

    /**
     * 世界切换或断开时清空候选集合。
     *
     * <p>ENTITY_UNLOAD 虽然会逐个移除实体，但维度切换/断线重连期间的事件时序
     * 不保证覆盖所有残留引用；因此 tick 观察到世界引用变化时整体清空一次，
     * 确保旧世界的实体不会被新世界的 tick 遍历到。
     */
    private static void refreshLevelContext(ClientLevel currentLevel) {
        LEVEL_STATE.observe(currentLevel, ANIMATION_CANDIDATES::clear);
    }

    private static void tickPlayerIfNeeded(Minecraft client) {
        Player player = client.player;
        if (player instanceof GeoAnimatable
                && ((IPlayerEntityAccessor) player).isUsingGeckoLib()
                && (client.options.getCameraType() == CameraType.FIRST_PERSON || ((IPlayerSkillAccessor) player).mobBattle$hasSkill())) {
            tickEntityRenderer(client, player);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void tickEntityRenderer(Minecraft client, Entity entity) {
        EntityRenderer renderer = client.getEntityRenderDispatcher().getRenderer(entity);
        if (renderer instanceof IGeoEntityAnimationTickInvoker invoker) {
            try {
                invoker.mobBattle$tickGeckoAnimations(entity, PARTIAL_TICK);
            } catch (IllegalArgumentException ignored) {
                // 一些GeckoLib渲染器可能没有一个完整的渲染状态，直到他们的第一个正常渲染。
            }
        }
    }
}
