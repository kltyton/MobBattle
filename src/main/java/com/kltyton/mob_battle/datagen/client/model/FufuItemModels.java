package com.kltyton.mob_battle.datagen.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.FufuAppearance;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;
import java.util.Set;

/** 使用原生物品模型选择器覆盖所有显示场景，未命名物品继续使用原模型。 */
final class FufuItemModels {
    private static final Set<String> SHARED_LEGACY_EGGS = Set.of(
            "angel_cyborg",
            "blood_man",
            "chuan_ren_gong",
            "contradiction_man",
            "elite_little_person_guard",
            "flower_fairy",
            "green_man",
            "hidden_eye_spawn_egg",
            "highbird_egg_spawn_egg",
            "ice_man",
            "knife_little_person",
            "laser_man",
            "little_person_boxer",
            "little_person_city_guard",
            "little_person_claw_zombie",
            "little_person_general",
            "little_person_headless_zombie",
            "little_person_medic",
            "little_person_servant",
            "little_person_shield_zombie",
            "little_person_sprayer_zombie",
            "little_person_sprayer_zombie_head",
            "little_person_zombie",
            "living_ghost",
            "mace_man",
            "macro_samurai",
            "new_snow_golem",
            "ninja",
            "renfu",
            "rough_white_zetsu",
            "scattered_demon",
            "seven_harvest_little_person",
            "super_evoker",
            "three_companions",
            "void_cell_spawn_egg",
            "wild_boar",
            "yemo_wenlu",
            "young_min_spawn_egg",
            "zombie_elite_little_person_guard",
            "zombie_little_person_archer",
            "zombie_little_person_giant",
            "zombie_little_person_guard",
            "zombie_little_person_king",
            "zombie_little_person_militia"
    );
    private static final ModelTemplate EGG = new ModelTemplate(
            Optional.of(id("item/dan")), Optional.empty(), TextureSlot.LAYER0);

    private FufuItemModels() {
    }

    static boolean usesSharedLegacyEgg(String name) {
        return SHARED_LEGACY_EGGS.contains(name);
    }

    static ItemModelGenerators wrap(ItemModelGenerators original, FabricPackOutput output) {
        return new ItemModelGenerators(new ItemModelOutput() {
            @Override
            public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
                String name = BuiltInRegistries.ITEM.getKey(item).getPath();
                String texture = "item/fufu/" + (item instanceof SpawnEggItem ? "dan/"
                        : name.equals("piglin_cannon") ? "weapon/"
                        : name.equals("master_scepter") || name.equals("mutual_attack_stick") ? "control/" : "") + name;
                if (output.getModContainer().findPath("assets/" + Mob_battle.MOD_ID + "/textures/" + texture + ".png").isPresent()) {
                    ModelTemplate template = item instanceof SpawnEggItem ? EGG
                            : name.endsWith("_sword") || name.equals("master_scepter") || name.equals("mutual_attack_stick")
                            ? ModelTemplates.FLAT_HANDHELD_ITEM : ModelTemplates.FLAT_ITEM;
                    Identifier alternate = template.create(id("item/fufu/" + name),
                            TextureMapping.layer0(new Material(id(texture))), original.modelOutput);
                    model = ItemModelUtils.select(new ComponentContents<>(DataComponents.CUSTOM_NAME), model,
                            ItemModelUtils.when(Component.literal(FufuAppearance.NAME), ItemModelUtils.plainModel(alternate)));
                }
                original.itemModelOutput.accept(item, model, properties);
            }

            @Override
            public void copy(Item donor, Item acceptor) {
                original.itemModelOutput.copy(donor, acceptor);
            }
        }, original.modelOutput);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, path);
    }
}
