package com.kltyton.mob_battle;

import com.kltyton.mob_battle.command.FriendlyProjectileDamageCommand;
import com.kltyton.mob_battle.command.TeamFightCommand;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.event.DataTrackers;
import com.kltyton.mob_battle.event.EntitySelectionHandlerYH;
import com.kltyton.mob_battle.effect.InsectBiteEffect;
import com.kltyton.mob_battle.event.TaskScheduler;
import com.kltyton.mob_battle.event.team.TeamFightHandler;
import com.kltyton.mob_battle.items.*;
import com.kltyton.mob_battle.mixin.ClampedEntityAttributeAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mob_battle implements ModInitializer {
    public static final String MOD_ID = "mob_battle";
    // 直接声明物品实例
    public static MutualAttackStickItem MUTUAL_ATTACK_STICK;
    public static UniversalLeadItem UNIVERSAL_LEAD;
    public static FireballScrollItem FIREBALL_SCROLL;
    public static BIgFireballScrollItem  BIG_FIREBALL_SCROLL;
    public static FiremanScrollItem  FIREMAN_SCROLL;

    public static InsectBiteEffect INSECT_BITE;
    public static final Logger LOGGER = LoggerFactory.getLogger(Mob_battle.class);
    public static void itemsRegister() {
        //注册物品
        MUTUAL_ATTACK_STICK = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "mutual_attack_stick"),
                new MutualAttackStickItem(new Item.Settings().maxCount(1)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(MOD_ID, "mutual_attack_stick")))));

        UNIVERSAL_LEAD = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "universal_lead"),
                new UniversalLeadItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(MOD_ID, "universal_lead")))));
        FIREBALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "fireball_scroll"),
                new FireballScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(MOD_ID, "fireball_scroll")))));
        BIG_FIREBALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "big_fireball_scroll"),
                new BIgFireballScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(MOD_ID, "big_fireball_scroll")))));
        FIREMAN_SCROLL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "fireman_scroll"),
                new FiremanScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(MOD_ID, "fireman_scroll")))));
    }
    @Override
    public void onInitialize() {
        itemsRegister();
        BaseItems.init();
        //注册BUFF
        INSECT_BITE = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "insect_bite"),
                new InsectBiteEffect());

        EntitySelectionHandlerYH.registerEvents();
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TeamFightCommand.register(dispatcher);
        });
        FriendlyProjectileDamageCommand.register();

        // 注册战斗处理器
        TeamFightHandler.register();
        //注册实体
        ModEntities.init();
        registeAttributer();
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TaskScheduler.tick();
        });
    }
    private void registeAttributer() {
        overrideAttribute(EntityAttributes.MAX_HEALTH, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ARMOR, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ARMOR_TOUGHNESS, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ATTACK_DAMAGE, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ATTACK_KNOCKBACK, 0.0, Double.MAX_VALUE);
    }

    private void overrideAttribute(RegistryEntry<EntityAttribute> attributeEntry, double newMin, double newMax) {
        if (attributeEntry == null) {
            LOGGER.warn("属性条目为 null，正在跳过。");
            return;
        }

        EntityAttribute attribute = attributeEntry.value();
        if (!(attribute instanceof ClampedEntityAttribute)) {
            LOGGER.warn("Attribute {} 不是 ClampedEntityAttribute，正在跳过。",
                    Registries.ATTRIBUTE.getId(attribute));
            return;
        }

        ClampedEntityAttribute clampedAttribute = (ClampedEntityAttribute) attribute;
        Identifier id = Registries.ATTRIBUTE.getId(clampedAttribute);
        if (id == null) return;

        try {
            ClampedEntityAttributeAccessor accessor = (ClampedEntityAttributeAccessor) clampedAttribute;
            accessor.setMinValue(newMin);
            accessor.setMaxValue(newMax);
        } catch (ClassCastException e) {
            LOGGER.error("无法覆盖属性 {}", id, e);
        }
    }
}
