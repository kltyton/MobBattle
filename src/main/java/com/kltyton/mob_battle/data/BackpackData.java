package com.kltyton.mob_battle.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;

public record BackpackData(int handIndex) {
    public static final PacketCodec<ByteBuf, BackpackData> CODEC =
            PacketCodecs.VAR_INT.xmap(BackpackData::new, BackpackData::handIndex);

    public Hand hand() {
        return handIndex == Hand.OFF_HAND.ordinal() ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}
