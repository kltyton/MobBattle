package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.items.manager.AreaGravityFieldManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class AreaGravityDeviceItem extends Item {
    public static final double RADIUS = 15.0D;
    public static final int DURATION_TICKS = 15 * 20;

    public AreaGravityDeviceItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!(world instanceof ServerLevel serverWorld)) {
            return InteractionResult.SUCCESS;
        }

        AreaGravityFieldManager.addField(serverWorld, user, user.position(), RADIUS, DURATION_TICKS);
        user.getCooldowns().addCooldown(this.getDefaultInstance(), 20);

        return InteractionResult.SUCCESS_SERVER;
    }
}
