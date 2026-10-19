package com.kltyton.mob_battle.items.armor.presence;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.animation.PalMorePlayerAnimationServerHandler;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.registry.PresenceEntityTypes;
import com.kltyton.mob_battle.entity.summon.presence.LivingBlockEntity;
import com.kltyton.mob_battle.entity.summon.presence.PresenceBlockProjectile;
import com.kltyton.mob_battle.entity.summon.presence.PresenceOrbitBlockEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.items.armor.support.ArmorSetRules;
import com.kltyton.mob_battle.items.registry.PresenceArmorItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** “有”套装的服务端状态机：输入只传按键，装备、消耗、动画时点与伤害均在此裁决。 */
public final class PresenceArmorSkills {
    private static final Identifier HEALTH = id("presence_reserved_health");
    private static final Identifier TOUGHNESS = id("presence_orbit_toughness");
    private static final Identifier MOVEMENT = id("presence_stomp_movement");
    private static final Identifier JUMP = id("presence_stomp_jump");
    private static final Map<MinecraftServer, RuntimeState> SERVERS = new HashMap<>();

    private record Action(int skill, long start, Vec3 anchor, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {}
    private static final class RuntimeState {
        final Map<UUID, Action> actions = new HashMap<>();
        final Map<UUID, List<PresenceOrbitBlockEntity>> orbits = new HashMap<>();
        final Map<UUID, LivingBlockEntity> livingBlocks = new HashMap<>();
    }

    private PresenceArmorSkills() {}
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, path); }
    private static RuntimeState runtime(MinecraftServer server) { return SERVERS.computeIfAbsent(server, key -> new RuntimeState()); }

    public static boolean isEquipped(LivingEntity player) {
        return ArmorSetRules.hasFullArmor(player, PresenceArmorItems.MATERIAL);
    }

    /** 不增加协议编号；潜行 Z 将主手方块唤醒，普通 Z 切换全部活方块的模式。 */
    public static void handle(ServerPlayer player, int key) {
        if (!player.isAlive() || !isEquipped(player)) return;
        MinecraftServer server = player.level().getServer();
        RuntimeState runtime = runtime(server);
        PresenceBondLedger ledger = PresenceBondLedger.get(server);
        var preference = ledger.preference(player.getUUID());
        if (key == 0) {
            if (player.isShiftKeyDown()) summon(player);
            else {
                ledger.preference(player.getUUID(), new PresenceBondLedger.Preference(!preference.aggressive(), preference.ammunition(), preference.readyAt()));
                for (LivingBlockEntity block : runtime.livingBlocks.values()) {
                    if (player.getUUID().equals(block.ownerId())) block.resetCombatMemory(player);
                }
            }
            return;
        }
        if (runtime.actions.containsKey(player.getUUID())) return;
        long now = server.overworld().getGameTime();
        if (key == 2) {
            if (!player.onGround() || player.isPassenger()) return;
            if (preference.readyAt() > now) {
                player.sendOverlayMessage(Component.translatable("message.mob_battle.armor_skill_cooling_down", (preference.readyAt() - now + 19) / 20));
                return;
            }
            ledger.preference(player.getUUID(), new PresenceBondLedger.Preference(preference.aggressive(), preference.ammunition(), now + 1100));
            runtime.actions.put(player.getUUID(), new Action(2, now, player.position(), player.level().dimension()));
            PalMorePlayerAnimationServerHandler.play(player, id("presence_stomp"));
        } else if (key == 1 && preference.ammunition() > 0) {
            ledger.preference(player.getUUID(), new PresenceBondLedger.Preference(preference.aggressive(), preference.ammunition() - 1, preference.readyAt()));
            rebuildOrbits(player, runtime, preference.ammunition() - 1);
            runtime.actions.put(player.getUUID(), new Action(1, now, player.position(), player.level().dimension()));
            PalMorePlayerAnimationServerHandler.play(player, id(player.getRandom().nextBoolean() ? "presence_throw_left" : "presence_throw_right"));
        }
    }

    /** 仅由真实投射物的 onHitEntity 调用，爆炸遍历不会进入此入口。 */
    public static void reduceCooldown(ServerPlayer player, int ticks) {
        PresenceBondLedger ledger = PresenceBondLedger.get(player.level().getServer());
        var preference = ledger.preference(player.getUUID());
        long now = player.level().getServer().overworld().getGameTime();
        ledger.preference(player.getUUID(), new PresenceBondLedger.Preference(preference.aggressive(), preference.ammunition(), Math.max(now, preference.readyAt() - ticks)));
    }

    private static void summon(ServerPlayer player) {
        if (!(player.getMainHandItem().getItem() instanceof BlockItem item)) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.presence_hold_block"));
            return;
        }
        var block = item.getBlock().defaultBlockState();
        float hardness = block.getDestroySpeed(player.level(), player.blockPosition());
        if (hardness < 0 || block.isAir()) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.presence_unbreakable"));
            return;
        }
        int cost = hardness < 3 ? 2 : hardness == 3 ? 5 : 8;
        if (player.getMaxHealth() - cost < 1) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.presence_health_needed", cost));
            return;
        }
        LivingBlockEntity blockEntity = new LivingBlockEntity(PresenceEntityTypes.LIVING_BLOCK, player.level());
        blockEntity.configure(player, block, cost, cost == 2 ? 300 : cost == 5 ? 2000 : 5000, cost == 2 ? 30 : cost == 5 ? 100 : 150);
        Vec3 direction = Vec3.directionFromRotation(0, player.getYRot()).scale(1.5);
        blockEntity.setPos(player.position().add(direction));
        if (!player.level().noCollision(blockEntity)) {
            player.sendOverlayMessage(Component.translatable("message.mob_battle.presence_no_space"));
            return;
        }
        PresenceBondLedger ledger = PresenceBondLedger.get(player.level().getServer());
        ledger.bind(player.getUUID(), blockEntity.getUUID(), cost);
        if (!player.level().addFreshEntity(blockEntity)) {
            ledger.release(player.getUUID(), blockEntity.getUUID());
            return;
        }
        updateReservedHealth(player);
    }

    /** 用契约总额重建瞬态属性，不在玩家存档留下独立的永久扣血修饰符。 */
    public static void updateReservedHealth(ServerPlayer player) {
        int reserved = PresenceBondLedger.get(player.level().getServer()).reservedHealth(player.getUUID());
        modifier(player, Attributes.MAX_HEALTH, HEALTH, -reserved, AttributeModifier.Operation.ADD_VALUE);
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

    private static void modifier(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                 Identifier id, double value, AttributeModifier.Operation operation) {
        var instance = player.getAttribute(attribute);
        var current = instance.getModifier(id);
        if (current != null && current.amount() == value && current.operation() == operation) return;
        if (current != null) instance.removeModifier(id);
        if (value != 0) instance.addTransientModifier(new AttributeModifier(id, value, operation));
    }

    private static void clearOrbits(RuntimeState runtime, UUID playerId) {
        List<PresenceOrbitBlockEntity> orbits = runtime.orbits.remove(playerId);
        if (orbits != null) orbits.forEach(PresenceOrbitBlockEntity::discard);
    }

    private static void rebuildOrbits(ServerPlayer player, RuntimeState runtime, int ammunition) {
        clearOrbits(runtime, player.getUUID());
        List<PresenceOrbitBlockEntity> orbits = new ArrayList<>();
        for (int i = 0; i < ammunition; i++) {
            PresenceOrbitBlockEntity orbit = new PresenceOrbitBlockEntity(PresenceEntityTypes.ORBIT, player.level());
            orbit.configure(player, i);
            player.level().addFreshEntity(orbit);
            orbits.add(orbit);
        }
        runtime.orbits.put(player.getUUID(), orbits);
    }

    private static void cancelBonds(ServerPlayer player, RuntimeState runtime) {
        PresenceBondLedger ledger = PresenceBondLedger.get(player.level().getServer());
        ledger.releaseAll(player.getUUID());
        for (LivingBlockEntity block : List.copyOf(runtime.livingBlocks.values())) {
            if (player.getUUID().equals(block.ownerId()) && block.isAlive()) block.kill((net.minecraft.server.level.ServerLevel) block.level());
        }
        updateReservedHealth(player);
    }

    private static void tick(ServerPlayer player, RuntimeState runtime) {
        MinecraftServer server = player.level().getServer();
        PresenceBondLedger ledger = PresenceBondLedger.get(server);
        var preference = ledger.preference(player.getUUID());
        boolean equipped = player.isAlive() && isEquipped(player);
        if (!equipped) {
            if (ledger.reservedHealth(player.getUUID()) > 0) cancelBonds(player, runtime);
            if (preference.ammunition() != 0) ledger.preference(player.getUUID(), new PresenceBondLedger.Preference(preference.aggressive(), 0, preference.readyAt()));
            runtime.actions.remove(player.getUUID());
            clearOrbits(runtime, player.getUUID());
            modifier(player, Attributes.ARMOR_TOUGHNESS, TOUGHNESS, 0, AttributeModifier.Operation.ADD_VALUE);
            modifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT, 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            modifier(player, Attributes.JUMP_STRENGTH, JUMP, 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            updateReservedHealth(player);
            ((PresenceArmorUser) player).mobBattle$setPresenceState(0);
            return;
        }
        long now = server.overworld().getGameTime();
        Action action = runtime.actions.get(player.getUUID());
        if (action != null && !action.dimension().equals(player.level().dimension())) {
            runtime.actions.remove(player.getUUID());
            action = null;
        }
        boolean stomping = action != null && action.skill() == 2;
        if (action != null) {
            long elapsed = now - action.start();
            if (stomping) {
                player.setDeltaMovement(Vec3.ZERO);
                if (player.position().distanceToSqr(action.anchor()) > 0.0001) {
                    player.connection.teleport(action.anchor().x, action.anchor().y, action.anchor().z, player.getYRot(), player.getXRot());
                }
                if (elapsed == 7) {
                    preference = new PresenceBondLedger.Preference(preference.aggressive(), 5, preference.readyAt());
                    ledger.preference(player.getUUID(), preference);
                    rebuildOrbits(player, runtime, 5);
                    for (LivingEntity target : EntityQueries.getNearbyEntity(player, LivingEntity.class, 2, false, EntityQueries.TeamFilter.EXCLUDE_TEAM)) {
                        target.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 200, 1));
                        target.addEffect(new MobEffectInstance(ModEffects.HEART_EATER_ENTRY, 200, 1));
                    }
                    player.level().sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, player.level().getBlockState(player.blockPosition().below())),
                            player.getX(), player.getY() + 0.1, player.getZ(), 35, 1.5, 0.1, 1.5, 0.08);
                }
            } else if (elapsed == 10) {
                PresenceBlockProjectile projectile = new PresenceBlockProjectile(PresenceEntityTypes.PROJECTILE, player.level());
                projectile.setOwner(player);
                projectile.setPos(player.getEyePosition().add(player.getLookAngle().scale(0.7)));
                Vec3 look = player.getLookAngle();
                projectile.shoot(look.x, look.y, look.z, 2.0F, 0);
                player.level().addFreshEntity(projectile);
            }
            if (elapsed >= 20) {
                runtime.actions.remove(player.getUUID());
                stomping = false;
            }
        }
        List<PresenceOrbitBlockEntity> orbits = runtime.orbits.get(player.getUUID());
        if (orbits == null || orbits.size() != preference.ammunition()
                || orbits.stream().anyMatch(orbit -> orbit.isRemoved() || orbit.level() != player.level())) {
            rebuildOrbits(player, runtime, preference.ammunition());
        }
        modifier(player, Attributes.ARMOR_TOUGHNESS, TOUGHNESS, preference.ammunition() * 2, AttributeModifier.Operation.ADD_VALUE);
        modifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT, stomping ? -1 : 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        modifier(player, Attributes.JUMP_STRENGTH, JUMP, stomping ? -1 : 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        updateReservedHealth(player);
        int state = PresenceArmorUser.EQUIPPED | preference.ammunition()
                | (preference.aggressive() ? PresenceArmorUser.AGGRESSIVE : 0)
                | (stomping ? PresenceArmorUser.STOMPING : 0)
                | ((int) Math.max(0, Math.min(1100, preference.readyAt() - now)) << PresenceArmorUser.COOLDOWN_SHIFT);
        ((PresenceArmorUser) player).mobBattle$setPresenceState(state);
    }

    /** 各状态均归属服务器实例；断线移除临时动画与环绕物，持久化召唤契约保留。 */
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            RuntimeState runtime = runtime(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) tick(player, runtime);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof LivingBlockEntity block) runtime(world.getServer()).livingBlocks.put(block.getUUID(), block);
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof LivingBlockEntity) runtime(world.getServer()).livingBlocks.remove(entity.getUUID());
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer player) tick(player, runtime(player.level().getServer()));
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            RuntimeState runtime = runtime(server);
            runtime.actions.remove(handler.getPlayer().getUUID());
            clearOrbits(runtime, handler.getPlayer().getUUID());
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(SERVERS::remove);
    }
}
