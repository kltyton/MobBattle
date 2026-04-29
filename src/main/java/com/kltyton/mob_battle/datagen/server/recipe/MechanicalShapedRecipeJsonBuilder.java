package com.kltyton.mob_battle.datagen.server.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kltyton.mob_battle.recipe.MechanicalShapedRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 机械工作台专用的有序配方 datagen builder。
 *
 * <p>它的写法基本等同于原版 {@code createShaped(...)}，区别是生成的配方
 * {@code type} 会是 {@code mob_battle:mechanical_crafting_shaped}，所以只会被
 * {@code MachineWorktableBlock} 打开的机械工作台识别，普通工作台不会识别。</p>
 *
 * <p>最常用写法：</p>
 * <pre>{@code
 * MechanicalShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModItems.WIRE, 2)
 *         .pattern(" r ")
 *         .pattern("rcr")
 *         .pattern(" r ")
 *         .input('r', Items.REDSTONE)
 *         .input('c', Items.COPPER_INGOT)
 *         .group("wire")
 *         .criterion(hasItem(ModBlocks.MACHINE_WORKTABLE_BLOCK), conditionsFromItem(ModBlocks.MACHINE_WORKTABLE_BLOCK))
 *         .offerTo(exporter, "mob_battle:mechanical/wire");
 * }</pre>
 *
 * <p>参数说明：</p>
 * <ul>
 *     <li>{@code itemLookup}：在 {@code ModRecipeGenerator} 中使用 {@code registries.getOrThrow(RegistryKeys.ITEM)} 获取。</li>
 *     <li>{@code RecipeCategory}：配方书分类；机械工作台当前禁用了配方书，但原版配方 JSON 仍需要这个字段。</li>
 *     <li>{@code output/count}：输出物品和数量。</li>
 *     <li>{@code pattern/input}：和原版有序配方一致，空格表示空槽。</li>
 *     <li>{@code criterion}：解锁条件；不写会报错。建议用机械工作台或核心材料作为解锁条件。</li>
 *     <li>{@code offerTo(exporter, "...")}：建议给机械配方单独路径，避免和普通配方输出同一个物品时 ID 冲突。</li>
 * </ul>
 */
public class MechanicalShapedRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
    private final RegistryEntryLookup<Item> registryLookup;
    private final RecipeCategory category;
    private final Item output;
    private final int count;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> inputs = Maps.newLinkedHashMap();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;

    private MechanicalShapedRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
        this.registryLookup = registryLookup;
        this.category = category;
        this.output = output.asItem();
        this.count = count;
    }

    public static MechanicalShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output) {
        return create(registryLookup, category, output, 1);
    }

    public static MechanicalShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
        return new MechanicalShapedRecipeJsonBuilder(registryLookup, category, output, count);
    }

    public MechanicalShapedRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
        return this.input(c, Ingredient.ofTag(this.registryLookup.getOrThrow(tag)));
    }

    public MechanicalShapedRecipeJsonBuilder input(Character c, ItemConvertible item) {
        return this.input(c, Ingredient.ofItem(item));
    }

    public MechanicalShapedRecipeJsonBuilder input(Character c, Ingredient ingredient) {
        if (this.inputs.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        } else if (c == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.inputs.put(c, ingredient);
            return this;
        }
    }

    public MechanicalShapedRecipeJsonBuilder pattern(String patternStr) {
        if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternStr);
            return this;
        }
    }

    public MechanicalShapedRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    public MechanicalShapedRecipeJsonBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public MechanicalShapedRecipeJsonBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        RawShapedRecipe rawShapedRecipe = this.validate(recipeKey);
        Advancement.Builder builder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        MechanicalShapedRecipe recipe = new MechanicalShapedRecipe(
                Objects.requireNonNullElse(this.group, ""),
                CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
                rawShapedRecipe,
                new ItemStack(this.output, this.count),
                this.showNotification
        );
        exporter.accept(recipeKey, recipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private RawShapedRecipe validate(RegistryKey<Recipe<?>> recipeKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
        } else {
            return RawShapedRecipe.create(this.inputs, this.pattern);
        }
    }
}
