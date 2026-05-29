package com.kltyton.mob_battle.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

public record BackpackData(int handIndex) {
    public static final StreamCodec<ByteBuf, BackpackData> CODEC =
            ByteBufCodecs.VAR_INT.map(BackpackData::new, BackpackData::handIndex);

    public InteractionHand hand() {
        return handIndex == InteractionHand.OFF_HAND.ordinal() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
