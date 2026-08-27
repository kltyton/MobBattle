package com.kltyton.mob_battle;

import com.kltyton.mob_battle.bootstrap.MobBattleBootstrap;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组主入口（Fabric ModInitializer 兼容入口）。
 *
 * <p>本类只保留 modid、日志与运行时常量，并把初始化一次性委托给
 * {@link MobBattleBootstrap#initialize()}；全部注册与事件装配调用已移入引导类，
 * 其执行顺序与原 onInitialize 完全一致。
 */
public class Mob_battle implements ModInitializer {
    public static final String MOD_ID = "mob_battle";
    public static final Logger LOGGER = LoggerFactory.getLogger(Mob_battle.class);
    public static int MAX_STACK_SIZE = 102400000;
    public static MinecraftServer SERVER;
    @Override
    public void onInitialize() {
        MobBattleBootstrap.initialize();
    }
}
