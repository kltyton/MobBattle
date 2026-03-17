package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.items.manager.AreaGravityFieldManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AreaGravityDeviceItem extends Item {
    public static final double RADIUS = 15.0D;
    public static final int DURATION_TICKS = 15 * 20;

    public AreaGravityDeviceItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        AreaGravityFieldManager.addField(serverWorld, user, user.getPos(), RADIUS, DURATION_TICKS);
        user.getItemCooldownManager().set(this.getDefaultStack(), 20);

        return ActionResult.SUCCESS_SERVER;
    }
}
