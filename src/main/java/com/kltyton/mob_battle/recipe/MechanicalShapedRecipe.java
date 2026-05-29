package com.kltyton.mob_battle.recipe;

import com.kltyton.mob_battle.block.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class MechanicalShapedRecipe implements MechanicalCraftingRecipe {
    private final ShapedRecipePattern raw;
    private final ItemStack result;
    private final String group;
    private final CraftingBookCategory category;
    private final boolean showNotification;
    @Nullable
    private PlacementInfo ingredientPlacement;

    public MechanicalShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern raw, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.raw = raw;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public RecipeSerializer<? extends MechanicalShapedRecipe> getSerializer() {
        return ModRecipeTypes.MECHANICAL_SHAPED_SERIALIZER;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = PlacementInfo.createFromOptionals(this.raw.ingredients());
        }

        return this.ingredientPlacement;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean matches(CraftingInput input, Level world) {
        return this.raw.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.raw.width(),
                        this.raw.height(),
                        this.raw.ingredients()
                                .stream()
                                .map(ingredient -> (SlotDisplay) ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE))
                                .toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(ModBlocks.MACHINE_WORKTABLE_BLOCK.asItem())
                )
        );
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.MECHANICAL_CRAFTING_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<MechanicalShapedRecipe> {
        public static final MapCodec<MechanicalShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(MechanicalShapedRecipe::group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(MechanicalShapedRecipe::category),
                                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.raw),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(MechanicalShapedRecipe::showNotification)
                        )
                        .apply(instance, MechanicalShapedRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MechanicalShapedRecipe> PACKET_CODEC = StreamCodec.of(
                MechanicalShapedRecipe.Serializer::write,
                MechanicalShapedRecipe.Serializer::read
        );

        @Override
        public MapCodec<MechanicalShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MechanicalShapedRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static MechanicalShapedRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern raw = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            boolean showNotification = buf.readBoolean();
            return new MechanicalShapedRecipe(group, category, raw, result, showNotification);
        }

        private static void write(RegistryFriendlyByteBuf buf, MechanicalShapedRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.raw);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification);
        }
    }
}
