package com.kltyton.mob_battle.items.cooldown;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;

import java.util.Optional;
import java.util.UUID;

public final class StackBoundCooldowns {
    private static final String GROUP_PREFIX = "stack_cooldown/";

    private StackBoundCooldowns() {
    }

    public static boolean isCoolingDown(Player player, ItemStack stack, String cooldownId, int cooldownTicks) {
        ensureGroup(stack, cooldownId, cooldownTicks);
        return player.getCooldowns().isOnCooldown(stack);
    }

    public static void start(Player player, ItemStack stack, String cooldownId, int cooldownTicks) {
        ensureGroup(stack, cooldownId, cooldownTicks);
        player.getCooldowns().addCooldown(stack, cooldownTicks);
    }

    public static void ensureGroup(ItemStack stack, String cooldownId, int cooldownTicks) {
        if (cooldownTicks <= 0 || hasStackCooldownGroup(stack, cooldownId)) {
            return;
        }

        Identifier group = Identifier.fromNamespaceAndPath(
                Mob_battle.MOD_ID,
                GROUP_PREFIX + cooldownId + "/" + UUID.randomUUID()
        );
        stack.set(DataComponents.USE_COOLDOWN, new UseCooldown(cooldownTicks / 20.0F, Optional.of(group)));
    }

    private static boolean hasStackCooldownGroup(ItemStack stack, String cooldownId) {
        UseCooldown cooldown = stack.get(DataComponents.USE_COOLDOWN);
        String expectedPrefix = GROUP_PREFIX + cooldownId + "/";
        return cooldown != null
                && cooldown.cooldownGroup()
                .map(group -> group.getNamespace().equals(Mob_battle.MOD_ID)
                        && group.getPath().startsWith(expectedPrefix))
                .orElse(false);
    }
}
