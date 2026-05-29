package com.kltyton.mob_battle.mixin.net.minecraft.command.argument;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(SuggestionProviders.LocalProvider.class)
public abstract class SuggestionProvidersLocalProviderMixin {

    @Shadow @Final
    Identifier id;

    @Inject(method = "getSuggestions", at = @At("RETURN"), cancellable = true)
    private void mob_battle$filterSummonableEntities(
            CommandContext<CommandSource> context,
            SuggestionsBuilder builder,
            CallbackInfoReturnable<CompletableFuture<Suggestions>> cir
    ) {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        if (!Identifier.ofVanilla("summonable_entities").equals(this.id)) {
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

