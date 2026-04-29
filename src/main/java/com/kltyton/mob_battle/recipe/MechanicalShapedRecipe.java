package com.kltyton.mob_battle.recipe;

import com.kltyton.mob_battle.block.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MechanicalShapedRecipe implements MechanicalCraftingRecipe {
    private final RawShapedRecipe raw;
    private final ItemStack result;
    private final String group;
    private final CraftingRecipeCategory category;
    private final boolean showNotification;
    @Nullable
    private IngredientPlacement ingredientPlacement;

    public MechanicalShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
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
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = IngredientPlacement.forMultipleSlots(this.raw.getIngredients());
        }

        return this.ingredientPlacement;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return this.raw.matches(input);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return this.result.copy();
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.raw.getWidth(),
                        this.raw.getHeight(),
                        this.raw.getIngredients()
                                .stream()
                                .map(ingredient -> (SlotDisplay) ingredient.map(Ingredient::toDisplay).orElse(SlotDisplay.EmptySlotDisplay.INSTANCE))
                                .toList(),
                        new SlotDisplay.StackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(ModBlocks.MACHINE_WORKTABLE_BLOCK.asItem())
                )
        );
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ModRecipeTypes.MECHANICAL_CRAFTING_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<MechanicalShapedRecipe> {
        public static final MapCodec<MechanicalShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(MechanicalShapedRecipe::getGroup),
                                CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(MechanicalShapedRecipe::getCategory),
                                RawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(MechanicalShapedRecipe::showNotification)
                        )
                        .apply(instance, MechanicalShapedRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, MechanicalShapedRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                MechanicalShapedRecipe.Serializer::write,
                MechanicalShapedRecipe.Serializer::read
        );

        @Override
        public MapCodec<MechanicalShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MechanicalShapedRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static MechanicalShapedRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe raw = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            boolean showNotification = buf.readBoolean();
            return new MechanicalShapedRecipe(group, category, raw, result, showNotification);
        }

        private static void write(RegistryByteBuf buf, MechanicalShapedRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification);
        }
    }
}
