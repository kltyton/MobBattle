package com.kltyton.mob_battle.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.MobBattleConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class GeoAnimationUtil {
    private static final double FINISHED_TRIGGERED_TIMELINE = -2.0D;
    private static final Map<AnimationController<?>, RawAnimation> LAST_FINISHED_ANIMATIONS =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object, Long> RECENTLY_FINISHED_ANIMATABLES =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<AnimationController<?>, Long> NEXT_SAMPLE_LOG_MS =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<String, Set<String>> ANIMATION_NAME_CACHE = new ConcurrentHashMap<>();
    private static final long FINISHED_ANIMATION_BRIDGE_MS = 250L;
    private static final long ANIMATION_SAMPLE_LOG_MS = 500L;

    private GeoAnimationUtil() {
    }

    public static boolean consumeFinishedTriggeredAnimation(AnimationTest<?> animationTest) {
        AnimationController<?> controller = animationTest.controller();
        clearFinishedMarkerForActiveRun(animationTest, controller);
        if (cleanupFinishedResetTransition(animationTest, controller)) {
            return false;
        }

        if (hasFinishedTriggeredAnimation(controller)) {
            resetFinishedTriggeredAnimation(animationTest, controller);
            return true;
        }

        if (controller.isPlayingTriggeredAnimation()) {
            logAnimationState("playing", animationTest, controller);
        }
        return false;
    }

    public static boolean isLastFinishedAnimation(AnimationTest<?> animationTest, RawAnimation animation) {
        return Objects.equals(LAST_FINISHED_ANIMATIONS.get(animationTest.controller()), animation);
    }

    public static boolean hasRecentlyFinishedTriggeredAnimation(Object animatable) {
        Long finishedAt = RECENTLY_FINISHED_ANIMATABLES.get(animatable);
        return finishedAt != null && System.currentTimeMillis() - finishedAt <= FINISHED_ANIMATION_BRIDGE_MS;
    }

    public static PlayState playTriggeredAnimationOrStop(AnimationTest<?> animationTest) {
        AnimationController<?> controller = animationTest.controller();
        clearFinishedMarkerForActiveRun(animationTest, controller);
        if (cleanupFinishedResetTransition(animationTest, controller)) {
            return PlayState.STOP;
        }

        if (hasFinishedTriggeredAnimation(controller)) {
            resetFinishedTriggeredAnimation(animationTest, controller);
            return PlayState.STOP;
        }

        boolean pendingTriggeredAnimation = controller.getCurrentRawAnimation() != null
                && controller.getPlayState() == PlayState.CONTINUE
                && controller.getCurrentAnimationPoint() == null;
        if (pendingTriggeredAnimation) {
            logAnimationState("pending", animationTest, controller);
        }
        return controller.isPlayingTriggeredAnimation() || pendingTriggeredAnimation ? PlayState.CONTINUE : PlayState.STOP;
    }

    public static boolean hasEntityAnimation(Entity entity, String animationName) {
        String entityPath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
        String resourcePath = "assets/" + Mob_battle.MOD_ID + "/geckolib/animations/" + entityPath + ".animation.json";
        return ANIMATION_NAME_CACHE.computeIfAbsent(resourcePath, GeoAnimationUtil::readAnimationNames)
                .contains(animationName);
    }

    public static boolean hasDeathAnimation(Entity entity) {
        return getDeathAnimationName(entity).isPresent();
    }

    public static Optional<String> getDeathAnimationName(Entity entity) {
        if (hasEntityAnimation(entity, "die")) {
            return Optional.of("die");
        }
        if (hasEntityAnimation(entity, "death")) {
            return Optional.of("death");
        }
        return Optional.empty();
    }

    public static boolean isMissingDeathAnimation(Entity entity, String animationName) {
        return ("die".equals(animationName) || "death".equals(animationName))
                && !hasEntityAnimation(entity, animationName);
    }

    private static Set<String> readAnimationNames(String resourcePath) {
        try (InputStream input = GeoAnimationUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                return Set.of();
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject animations = root.getAsJsonObject("animations");
            return animations == null ? Set.of() : Set.copyOf(animations.keySet());
        } catch (IOException | JsonParseException | IllegalStateException ignored) {
            return Set.of();
        }
    }

    private static boolean hasFinishedTriggeredAnimation(AnimationController<?> controller) {
        return controller.getCurrentTimelineTime() == FINISHED_TRIGGERED_TIMELINE
                || (controller.isPlayingTriggeredAnimation() && controller.hasAnimationFinished());
    }

    private static boolean cleanupFinishedResetTransition(AnimationTest<?> animationTest, AnimationController<?> controller) {
        if (controller.getCurrentTimelineTime() != FINISHED_TRIGGERED_TIMELINE) {
            return false;
        }

        RawAnimation currentAnimation = controller.getCurrentRawAnimation();
        if (currentAnimation != null && Objects.equals(LAST_FINISHED_ANIMATIONS.get(controller), currentAnimation)) {
            logAnimationState("cleanup_before_reset", animationTest, controller);
            controller.reset();
            logAnimationState("cleanup_after_reset", animationTest, controller);
            return true;
        }
        return false;
    }

    private static void clearFinishedMarkerForActiveRun(AnimationTest<?> animationTest, AnimationController<?> controller) {
        RawAnimation currentAnimation = controller.getCurrentRawAnimation();
        if (currentAnimation == null || !Objects.equals(LAST_FINISHED_ANIMATIONS.get(controller), currentAnimation)) {
            return;
        }

        boolean pendingTriggeredAnimation = controller.getPlayState() == PlayState.CONTINUE
                && controller.getCurrentAnimationPoint() == null;
        boolean activeTriggeredAnimation = controller.isPlayingTriggeredAnimation()
                || pendingTriggeredAnimation
                || (controller.getCurrentTimelineTime() == FINISHED_TRIGGERED_TIMELINE
                && controller.getPlayState() != PlayState.STOP);
        if (!activeTriggeredAnimation) {
            return;
        }

        LAST_FINISHED_ANIMATIONS.remove(controller);
        logAnimationState("new_run_cleared_finished_marker", animationTest, controller);
    }

    private static void resetFinishedTriggeredAnimation(AnimationTest<?> animationTest, AnimationController<?> controller) {
        RawAnimation finishedAnimation = controller.getCurrentRawAnimation();
        if (finishedAnimation != null) {
            LAST_FINISHED_ANIMATIONS.put(controller, finishedAnimation);
        }
        RECENTLY_FINISHED_ANIMATABLES.put(animationTest.animatable(), System.currentTimeMillis());
        logAnimationState("finish_detected", animationTest, controller);
        boolean stopped = controller.stopTriggeredAnimation();
        logAnimationState("finish_stop_triggered stopped=" + stopped, animationTest, controller);
    }

    private static void logAnimationState(String event, AnimationTest<?> animationTest, AnimationController<?> controller) {
        if (!MobBattleConfig.isDebugLoggingEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (("playing".equals(event) || "pending".equals(event)) && !shouldLogSample(controller, now)) {
            return;
        }
        Object animatable = animationTest.animatable();
        if (animatable instanceof Entity entity) {
            Mob_battle.LOGGER.info(
                    "[MobBattle][AnimTrace] event={} controller={} raw={} playState={} timeline={} animTime={} speed={} transitionTicks={} timelineNull={} pointNull={} finished={} playingTriggered={} animatingBones={} transitioning={} entity={} id={} uuid={} class={} tick={} removed={} alive={} noAi={} hasSkill={} waitingAxeRecovery={} target={} pos=({}, {}, {})",
                    event,
                    controller.getName(),
                    controller.getCurrentRawAnimation(),
                    controller.getPlayState(),
                    controller.getCurrentTimelineTime(),
                    controller.getCurrentAnimationTime(),
                    controller.getAnimationSpeed(),
                    controller.getTransitionTicks(),
                    controller.getTimeline() == null,
                    controller.getCurrentAnimationPoint() == null,
                    controller.hasAnimationFinished(),
                    controller.isPlayingTriggeredAnimation(),
                    controller.isAnimatingBones(),
                    controller.isTransitioning(),
                    entity.getType(),
                    entity.getId(),
                    entity.getUUID(),
                    entity.getClass().getName(),
                    entity.tickCount,
                    entity.isRemoved(),
                    entity.isAlive(),
                    entity instanceof Mob mob && mob.isNoAi(),
                    readBooleanMethod(animatable, "hasSkill"),
                    readBooleanMethod(animatable, "isWaitingForAxeRecovery"),
                    describeTarget(entity),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ()
            );
            return;
        }
        Mob_battle.LOGGER.info(
                "[MobBattle][AnimTrace] event={} controller={} raw={} playState={} timeline={} animTime={} speed={} transitionTicks={} timelineNull={} pointNull={} finished={} playingTriggered={} animatingBones={} transitioning={} animatable={} hasSkill={} waitingAxeRecovery={}",
                event,
                controller.getName(),
                controller.getCurrentRawAnimation(),
                controller.getPlayState(),
                controller.getCurrentTimelineTime(),
                controller.getCurrentAnimationTime(),
                controller.getAnimationSpeed(),
                controller.getTransitionTicks(),
                controller.getTimeline() == null,
                controller.getCurrentAnimationPoint() == null,
                controller.hasAnimationFinished(),
                controller.isPlayingTriggeredAnimation(),
                controller.isAnimatingBones(),
                controller.isTransitioning(),
                animatable.getClass().getName(),
                readBooleanMethod(animatable, "hasSkill"),
                readBooleanMethod(animatable, "isWaitingForAxeRecovery")
        );
    }

    private static boolean shouldLogSample(AnimationController<?> controller, long now) {
        Long nextLogMs = NEXT_SAMPLE_LOG_MS.get(controller);
        if (nextLogMs != null && now < nextLogMs) {
            return false;
        }

        NEXT_SAMPLE_LOG_MS.put(controller, now + ANIMATION_SAMPLE_LOG_MS);
        return true;
    }

    private static String readBooleanMethod(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);
            return String.valueOf(value);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return "n/a";
        }
    }

    private static String describeTarget(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return "n/a";
        }

        LivingEntity target = mob.getTarget();
        return target == null ? "null" : target.getType() + "#" + target.getId();
    }
}
