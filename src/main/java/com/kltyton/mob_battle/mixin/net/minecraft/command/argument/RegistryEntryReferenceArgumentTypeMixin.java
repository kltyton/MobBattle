package com.kltyton.mob_battle.mixin.net.minecraft.command.argument;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.arguments.ResourceArgument;

@Mixin(ResourceArgument.class)
public abstract class RegistryEntryReferenceArgumentTypeMixin {

    @Inject(method = "listSuggestions", at = @At("RETURN"), cancellable = true)
    private <S> void mob_battle$filterSuggestions(
            CommandContext<S> context,
            SuggestionsBuilder builder,
            CallbackInfoReturnable<CompletableFuture<Suggestions>> cir
    ) {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        cir.setReturnValue(cir.getReturnValue().thenApply(suggestions -> {
            List<Suggestion> filtered = suggestions.getList().stream()
                    .filter(s -> !s.getText().startsWith(Mob_battle.MOD_ID + ":"))
                    .toList();
            return Suggestions.create(builder.getInput(), filtered);
        }));
    }
}
