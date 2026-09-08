package com.kltyton.mob_battle.bootstrap;

import com.kltyton.mob_battle.attributer.ModAttributer;
import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.client.screen.ModScreenHandlers;
import com.kltyton.mob_battle.command.ModCommands;
import com.kltyton.mob_battle.components.ModComponents;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.entity.sensor.ModSensorTypes;
import com.kltyton.mob_battle.event.ModEvents;
import com.kltyton.mob_battle.event.SkillAiRecoveryEvent;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorPlayerStateLifecycle;
import com.kltyton.mob_battle.items.itemgroup.ModItemGroups;
import com.kltyton.mob_battle.network.ModPackets;
import com.kltyton.mob_battle.network.ServerPlayNetwork;
import com.kltyton.mob_battle.recipe.ModRecipeTypes;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.sounds.bgm.ServerBgmManager;
import com.kltyton.mob_battle.entity.data.ModEntityDataSerializers;

/**
 * 模组初始化最终装配类（bootstrap）。
 *
 * <p>本类是 {@code Mob_battle#onInitialize()} 的唯一初始化委托目标：把原有的全部
 * {@code init()} 调用原样搬入此类，并按“配置 / 核心注册表 / 内容 / 网络 / 运行时集成”
 * 五个阶段分组。各阶段只是对既有调用序列的连续分区，全局调用顺序与重构前逐行一致，
 * <b>禁止以任何方式调整阶段内部或跨阶段的调用顺序</b>。
 *
 * <p>关键顺序依赖（重构前后保持不变）：
 * <ul>
 *     <li>物品先于实体：{@code ModItems.init()} 的刷怪蛋直接引用 {@code ModEntities}
 *         的 {@code EntityType} 常量（例如 {@code ModEntities.SKULL_KING}），因此物品注册
 *         必须先于实体类型注册表写入；刷怪蛋只持有常量引用，实体注册在其后由
 *         {@code ModEntities.init()} 完成。</li>
 *     <li>数据包先于接收器：{@code ModPackets.init()} 先把全部 payload 类型写入
 *         {@code PayloadTypeRegistry}，随后 {@code ServerPlayNetwork.init()} 才能按
 *         payload ID 注册全局接收器。</li>
 *     <li>配置最先：{@code MobBattleConfig.init()} 是唯一无条件前置阶段，后续内容与
 *         运行时集成阶段会读取配置（例如创造标签页的刷怪蛋开关、技能 AI 恢复的
 *         调试日志开关）。</li>
 * </ul>
 *
 * <p>本类不含接口、工厂或任何抽象层；它只是把原有线性初始化拆成可读的阶段性方法，
 * 便于人工审计依赖关系。
 */
public final class MobBattleBootstrap {

    private MobBattleBootstrap() {
    }

    /**
     * 执行完整的模组初始化。所有调用顺序与原 {@code Mob_battle#onInitialize()} 完全一致。
     */
    public static void initialize() {
        initConfiguration();
        initCoreRegistries();
        initContent();
        initNetwork();
        initRuntimeIntegration();
    }

    /**
     * 阶段一：配置。
     *
     * <p>{@code MobBattleConfig.init()} 注册模组配置系统，必须最先执行：内容阶段
     * （{@code ModItemGroups} 的“显示于原版刷怪蛋标签页”开关）与运行时集成阶段
     * （{@code SkillAiRecoveryEvent} 的调试日志开关）都会读取该配置。
     */
    private static void initConfiguration() {
        MobBattleConfig.init();
    }

    /**
     * 阶段二：核心注册表。
     *
     * <p>注册实体附加数据、传感器类型、自定义属性与数据组件类型。这些是内容阶段的
     * 底层支撑：物品、实体、方块在注册或构造时会引用这些类型，因此必须先于内容阶段完成。
     */
    private static void initCoreRegistries() {
        ModEntityDataSerializers.init();
        ModSensorTypes.init();
        ModEntityAttributes.init();
        ModComponents.init();
    }

    /**
     * 阶段三：内容。
     *
     * <p>按原顺序注册效果、物品、配方类型、附魔、命令、玩法事件、实体、方块、方块实体
     * 与属性范围覆盖。关键顺序依赖：
     * <ul>
     *     <li>{@code ModEffects} 必须先于 {@code ModItems}：压缩标记剑
     *         （{@code ModItems.COMPRESSED_DIAMOND_SWORD} 等）直接引用
     *         {@code ModEffects.DIAMOND_MARK_ENTRY / NETHERITE_MARK_ENTRY}。</li>
     *     <li>{@code ModItems} 必须先于 {@code ModEntities}：刷怪蛋物品引用
     *         {@code ModEntities} 的实体类型常量（见类注释）。</li>
     *     <li>{@code ModBlocks} 必须先于 {@code ModBlockEntities}：方块实体类型
     *         由 {@code ModBlocks} 常量构建。</li>
     *     <li>{@code ModCommands}、{@code ModEvents}、{@code ModAttributer} 按原始位置
     *         位于内容注册之间：命令回调、服务端玩法事件与属性范围覆盖不得提前到
     *         注册表写入之前，也不得挪到网络阶段之后。</li>
     * </ul>
     */
    private static void initContent() {
        ModEffects.init();
        ModItems.init();
        ModRecipeTypes.init();
        ModEnchantments.init();
        ModCommands.init();
        ModEvents.init();
        ModEntities.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModAttributer.init();
    }

    /**
     * 阶段四：网络。
     *
     * <p>{@code ModPackets.init()} 必须先行：先在 {@code PayloadTypeRegistry} 注册全部
     * serverboundPlay / clientboundPlay payload 类型，再由 {@code ServerPlayNetwork.init()}
     * 按 payload ID 注册全局接收器；类型缺失会导致接收器绑定失败。网络阶段必须先于
     * 运行时集成阶段，因为 {@code ServerBgmManager} 与技能/无人机接收器会通过已注册的
     * payload 收发数据。
     */
    private static void initNetwork() {
        ModPackets.init();
        ServerPlayNetwork.init();
    }

    /**
     * 阶段五：运行时集成。
     *
     * <p>注册音效、服务器生命周期/tick 回调、创造模式标签页、技能 AI 恢复与菜单类型。
     * 关键顺序依赖：
     * <ul>
     *     <li>{@code ModSounds} 先于 {@code ServerBgmManager}：BGM 切换通过
     *         {@code SoundPayload} 发送，依赖音效与网络 payload 均已就绪。</li>
     *     <li>{@code ModItemGroups} 引用全部已注册物品（含刷怪蛋）、方块与
     *         {@code ModEnchantments.MAGIC_PROTECTION}，因此必须排在内容阶段全部
     *         注册完成之后。</li>
     *     <li>{@code ModScreenHandlers} 保持为最后一个调用：其扩展菜单工厂在运行时
     *         构造背包/工作台 handler，依赖已注册的物品与方块。</li>
     * </ul>
     */
    private static void initRuntimeIntegration() {
        ModSounds.init();
        ServerBgmManager.init();
        DroneManager.init();
        ModItemGroups.init();
        SkillAiRecoveryEvent.init();
        // 压缩护甲静态玩家状态释放：只注册断线/停服事件，无注册表依赖，
        // 插入此处不影响任何既有初始化顺序。
        CompressArmorPlayerStateLifecycle.init();
        ModScreenHandlers.init();
    }
}
