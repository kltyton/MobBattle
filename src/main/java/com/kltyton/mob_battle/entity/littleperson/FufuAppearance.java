package com.kltyton.mob_battle.entity.littleperson;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/** 小人彩蛋的共享名称和资源边界；不改变实体标识、存档或技能协议。 */
public final class FufuAppearance {
    public static final String NAME = "fufu";
    private static final Set<String> VARIANTS = Set.of(
            "little_person_civilian",
            "little_person_worker",
            "little_person_militia",
            "little_person_archer",
            "little_person_soldier",
            "little_person_soldier_archer",
            "little_person_guard",
            "little_person_king",
            "little_person_giant",
            "little_person_boxer",
            "little_person_city_guard",
            "little_person_servant",
            "little_person_medic",
            "little_person_general",
            "elite_little_person_guard",
            "knife_little_person",
            "seven_harvest_little_person",
            "macro_samurai",
            "contradiction_man",
            "mace_man",
            "renfu",
            "chuan_ren_gong",
            "green_man",
            "yemo_wenlu",
            "three_companions",
            "angel_cyborg",
            "cyborg",
            "bloody_blade",
            "blood_man",
            "french_sphere_flow",
            "heaven_crippled_feet",
            "human_hammer",
            "human_shield",
            "ice_man",
            "ice_soldier",
            "iron_man",
            "iron_man_true",
            "laser_man",
            "living_ghost",
            "magic_man",
            "new_skull_mage",
            "ninja",
            "poisonous_slash",
            "scattered_demon",
            "sex_entity",
            "tai_lin",
            "wild_man",
            "wild_boar",
            "xbot002",
            "zombie/little_person_zombie"
    );

    private FufuAppearance() {
    }

    /** 与铁砧产生的原生 CUSTOM_NAME 组件精确匹配，大小写和空格均有意义。 */
    public static boolean isNamed(@Nullable Component customName) {
        return Component.literal(NAME).equals(customName);
    }

    /** 仅完整保留了两套资源的实体可切换，未提供旧素材的新增僵尸继续使用现有资源。 */
    public static String resourcePath(String original, boolean fufu) {
        return fufu && VARIANTS.contains(original) ? "fufu/" + original : original;
    }
}
