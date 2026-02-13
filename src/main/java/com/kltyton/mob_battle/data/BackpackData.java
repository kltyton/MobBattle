package com.kltyton.mob_battle.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;

public record BackpackData(int handIndex) {  // 0 = MAIN_HAND, 1 = OFF_HAND
    public static final PacketCodec<ByteBuf, BackpackData> CODEC =
            PacketCodecs.VAR_INT.xmap(BackpackData::new, BackpackData::handIndex);

    public Hand hand() {
        return Hand.values()[handIndex];  // 安全：Hand只有0和1
    }
}
