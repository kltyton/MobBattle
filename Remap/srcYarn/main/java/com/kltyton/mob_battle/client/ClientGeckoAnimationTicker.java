package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.player.IGeoEntityAnimationTickInvoker;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerSkillAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import software.bernie.geckolib.animatable.GeoAnimatable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientGeckoAnimationTicker {
    private static final float PARTIAL_TICK = 1.0F;
    private static final double SCAN_RANGE = 192.0D;
    private static final Map<Class<?>, Method> HAS_SKILL_METHODS = new ConcurrentHashMap<>();

    private ClientGeckoAnimationTicker() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientGeckoAnimationTicker::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null || client.isPaused()) {
            return;
        }

        tickPlayerIfNeeded(client);

        Box scanBox = client.player.getBoundingBox().expand(SCAN_RANGE);
        for (Entity entity : client.world.getEntitiesByClass(Entity.class, scanBox, ClientGeckoAnimationTicker::needsEntityAnimationTick)) {
            if (entity != client.player) {
                tickEntityRenderer(client, entity);
            }
        }
    }

    private static void tickPlayerIfNeeded(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player instanceof GeoAnimatable
                && ((IPlayerEntityAccessor) player).isUsingGeckoLib()
                && (client.options.getPerspective() == Perspective.FIRST_PERSON || ((IPlayerSkillAccessor) player).mobBattle$hasSkill())) {
            tickEntityRenderer(client, player);
        }
    }

    private static boolean needsEntityAnimationTick(Entity entity) {
        if (!(entity instanceof GeoAnimatable)) {
            return false;
        }
        if (entity instanceof GeneralEntity<?> generalEntity) {
            return generalEntity.hasSkill();
        }
        if (entity instanceof GeneralEntityOnlyOneSkill<?> generalEntity) {
            return generalEntity.hasSkill();
        }
        return hasReflectiveSkill(entity);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void tickEntityRenderer(MinecraftClient client, Entity entity) {
        EntityRenderer renderer = client.getEntityRenderDispatcher().getRenderer(entity);
        if (renderer instanceof IGeoEntityAnimationTickInvoker invoker) {
            try {
                invoker.mobBattle$tickGeckoAnimations(entity, PARTIAL_TICK);
            } catch (IllegalArgumentException ignored) {
                // 一些GeckoLib渲染器可能没有一个完整的渲染状态，直到他们的第一个正常渲染。
            }
        }
    }

    private static boolean hasReflectiveSkill(Entity entity) {
        Method method = HAS_SKILL_METHODS.computeIfAbsent(entity.getClass(), ClientGeckoAnimationTicker::findHasSkillMethod);
        if (method == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(method.invoke(entity));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static Method findHasSkillMethod(Class<?> type) {
        try {
            Method method = type.getMethod("hasSkill");
            return method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class ? method : null;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
