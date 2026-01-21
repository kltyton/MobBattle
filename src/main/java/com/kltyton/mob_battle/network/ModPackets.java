package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPackets {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(
                HighbirdAttackPayload.ID,
                HighbirdAttackPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                HighbirdAngerPayload.ID,
                HighbirdAngerPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                KeepInventoryPayload.ID,
                KeepInventoryPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                SkillPayload.ID,
                SkillPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                LeftClickPacket.ID,
                LeftClickPacket.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                HulkbusterEntityPayload.ID,
                HulkbusterEntityPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                SoundPayload.ID,
                SoundPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                EntityUniversalPayload.ID,
                EntityUniversalPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                EnchantmentPayload.ID,
                EnchantmentPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                SummonDronePayload.ID,
                SummonDronePayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                ItemGroupPayload.ID,
                ItemGroupPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                MasterScepterPayload.ID,
                MasterScepterPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                ItemGroupPayload.ID,
                ItemGroupPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                ILeadUpdatePayload.ID,
                ILeadUpdatePayload.CODEC
        );
    }
}
