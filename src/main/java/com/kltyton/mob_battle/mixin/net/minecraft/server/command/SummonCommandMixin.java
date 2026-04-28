package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public abstract class SummonCommandMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockSummon(
            ServerCommandSource source,
            RegistryEntry.Reference<EntityType<?>> entityType,
            Vec3d pos,
            NbtCompound nbt,
            boolean initialize,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayerEntity player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        if (Mob_battle.MOD_ID.equals(entityType.registryKey().getValue().getNamespace())) {
            source.sendError(Text.literal("你没有权限召唤 " + Mob_battle.MOD_ID + " 的实体。"));
            cir.setReturnValue(0);
        }
    }
}

