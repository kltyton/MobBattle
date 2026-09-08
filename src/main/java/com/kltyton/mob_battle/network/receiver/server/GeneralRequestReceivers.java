package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.command.CombatLogSystem;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.event.masterscepter.MasterScepterManager;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.network.packet.EnchantmentPayload;
import com.kltyton.mob_battle.network.packet.ItemGroupPayload;
import com.kltyton.mob_battle.network.packet.MasterScepterPayload;
import com.kltyton.mob_battle.network.packet.SummonDronePayload;
import com.kltyton.mob_battle.items.armor.support.ArmorSetRules;
import com.kltyton.mob_battle.enchantment.support.EnchantmentAccess;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 通用物品/权限请求接收器（服务端）。
 *
 * <p>承载原 {@code ServerPlayNetwork.init()} 中第 7~10 位的四个接收器：
 * {@link EnchantmentPayload}（附魔请求）、{@link SummonDronePayload}（无人机
 * 模式切换）、{@link ItemGroupPayload}（创造标签页权限回查）、
 * {@link MasterScepterPayload}（权杖命令）。每个接收器都有独立的
 * {@code init...} 方法，可被调用方按原注册顺序逐个插入（紧接第 6 位
 * {@code HeldItemActionReceivers.initLeftClick()} 之后），单个方法内只注册一个
 * payload，因此不会改变总注册顺序。
 *
 * <p>信任边界：
 * <ol>
 *   <li>附魔：负载中的 {@code ItemStack} 属于客户端可伪造数据，本类一律不作为
 *       操作对象。只允许在发送玩家当前主手或副手中，寻找与负载
 *       “同物品且物品组件一致”（{@link ItemStack#isSameItemSameComponents}）的
 *       服务端 {@code ItemStack} 上施加附魔；主手/副手均不匹配或负载栈为空时
 *       直接拒绝。附魔等级限定为 0..255，越界拒绝。其余行为（线程模型、
 *       {@link EnchantmentAccess#addEnchantment} 语义）与 HEAD 一致。</li>
 *   <li>无人机：保持 HEAD 语义，要求全套 Iron-Gold 盔甲（
 *       {@link ArmorSetRules#hasFullArmor}）后方可切换模式，模式号仍由
 *       {@link DroneManager} 裁决。</li>
 *   <li>物品组：仅按服务端权限 {@link MobBattlePermissions#canUseProtectedContent}
 *       计算并回包，负载本身不含任何输入数据。</li>
 *   <li>权杖：仅当发送玩家主手或副手实际持有 {@link ModItems#MASTER_SCEPTER}
 *       时才执行命令，防止无杖客户端驱动服务端命令；命令字符串白名单与冷却由
 *       {@link MasterScepterManager} 裁决。</li>
 * </ol>
 *
 * <p>本文件不新增权限、距离或速率限制等推测性约束；所有拒绝均为静默返回，
 * 与 HEAD 的拒绝风格保持一致。
 */
public final class GeneralRequestReceivers {
    private static final int MIN_SUMMON_DRONE_MODE = 1;
    private static final int MAX_SUMMON_DRONE_MODE = 3;

    private GeneralRequestReceivers() {
    }

    /**
     * 附魔请求接收器（原注册顺序第 7 位）。
     *
     * <p>安全修复：不信任并拒绝使用 {@code payload.itemStack()} 这份客户端可伪造、
     * 可能脱离库存的栈。仅当发送玩家主手或副手中存在与负载同物品且组件一致的
     * 服务端 {@code ItemStack} 时才对其施加附魔，均不匹配或负载栈为空则拒绝；
     * 等级限定 0..255，越界拒绝。线程模型与 HEAD 一致（无
     * {@code server.execute} 包装）。
     */
    public static void initEnchantment() {
        ServerPlayNetworking.registerGlobalReceiver(EnchantmentPayload.ID,
                (payload, context) -> {
                    int level = payload.level();
                    if (level < 0 || level > 255) {
                        return;
                    }

                    ItemStack payloadStack = payload.itemStack();
                    if (payloadStack.isEmpty()) {
                        return;
                    }
                    ResourceKey<Enchantment> enchantment = payload.enchantment();

                    ServerPlayer player = context.player();
                    ItemStack matchedStack = null;
                    if (ItemStack.isSameItemSameComponents(player.getMainHandItem(), payloadStack)) {
                        matchedStack = player.getMainHandItem();
                    } else if (ItemStack.isSameItemSameComponents(player.getOffhandItem(), payloadStack)) {
                        matchedStack = player.getOffhandItem();
                    }
                    if (matchedStack == null) {
                        return;
                    }

                    tryAddEnchantment(player, matchedStack, enchantment, level);
                }
        );
    }

    /**
     * 只对当前服务器注册表中存在的附魔执行写入；客户端伪造的未知键直接拒绝。
     *
     * <p>返回值供 GameTest 锁定“未知键无异常且不修改物品”的边界行为。
     */
    static boolean tryAddEnchantment(
            ServerPlayer player,
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level
    ) {
        if (enchantment == null) {
            return false;
        }
        var enchantmentRegistry = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (enchantmentRegistry.get(enchantment).isEmpty()) {
            return false;
        }
        EnchantmentAccess.addEnchantment(player, stack, enchantment, level);
        return true;
    }

    /**
     * 无人机模式切换接收器（原注册顺序第 8 位，保留全套 Iron-Gold 盔甲门）。
     * Fabric play 阶段回调已经运行在服务器线程，不再二次排队。
     */
    public static void initSummonDrone() {
        ServerPlayNetworking.registerGlobalReceiver(SummonDronePayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            int type = payload.mode();
            if (!isKnownSummonDroneMode(type)) {
                return;
            }
            CombatLogSystem.logAction(player, "切换无人机模式 " + type);
            if (!ArmorSetRules.hasFullArmor(player, ModMaterial.IRON_GOLD_INSTANCE)) {
                return;
            }
            if (type == 1) DroneManager.handleSummonRequest(player);
            if (type == 2) DroneManager.handleAttackDroneMode(player);
            if (type == 3) DroneManager.handleTreatmentDroneMode(player);
        });
    }

    /**
     * 验证无人机协议 mode，必须在日志和业务分派前执行。
     *
     * @param mode 客户端携带的模式编号
     * @return 仅当编号属于现有兼容协议 1、2、3 时返回 true
     */
    static boolean isKnownSummonDroneMode(int mode) {
        return mode >= MIN_SUMMON_DRONE_MODE && mode <= MAX_SUMMON_DRONE_MODE;
    }

    /**
     * 创造标签页权限回查接收器（原注册顺序第 9 位，保留 HEAD 语义：
     * 服务端计算权限后回发 {@link ItemGroupPayload}）。
     */
    public static void initItemGroup() {
        ServerPlayNetworking.registerGlobalReceiver(ItemGroupPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            ServerPlayNetworking.send(player, new ItemGroupPayload(MobBattlePermissions.canUseProtectedContent(player)));
        });
    }

    /**
     * 权杖命令接收器（原注册顺序第 10 位）。
     *
     * <p>安全修复：仅当发送玩家主手或副手实际持有 {@link ModItems#MASTER_SCEPTER}
     * 时才执行命令；命令字符串与冷却由 {@link MasterScepterManager#runCommand}
     * 裁决。回调已经处于服务器线程，直接校验并执行，避免额外一 tick 排队。
     */
    public static void initMasterScepter() {
        ServerPlayNetworking.registerGlobalReceiver(MasterScepterPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            if (!player.getMainHandItem().is(ModItems.MASTER_SCEPTER)
                    && !player.getOffhandItem().is(ModItems.MASTER_SCEPTER)) {
                return;
            }
            String command = payload.id();
            MasterScepterManager.runCommand(player, command);
        });
    }
}
