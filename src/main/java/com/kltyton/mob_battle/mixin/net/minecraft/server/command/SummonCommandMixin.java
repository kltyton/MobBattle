package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public abstract class SummonCommandMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockSummon(
            CommandSourceStack source,
            Holder.Reference<EntityType<?>> entityType,
            Vec3 pos,
            CompoundTag nbt,
            boolean initialize,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayer player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        if (Mob_battle.MOD_ID.equals(entityType.key().location().getNamespace())) {
            source.sendFailure(Component.literal("你没有权限召唤 " + Mob_battle.MOD_ID + " 的实体。"));
            cir.setReturnValue(0);
        }
    }
}

