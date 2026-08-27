package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.soldier.LittlePersonSoldierArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.soldier.LittlePersonSoldierEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.*;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requested.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 小人物技能实体（LITTLE_PERSON_ENTITIES 家族）的注册领域类。
 *
 * <p>本类集中保存 39 个小人物技能实体的真实注册表达式，字段声明顺序与原
 * ModEntities 中的声明顺序完全一致。每个字段调用 registerLittlePerson 完成注册：
 * Registry.register 写入 BuiltInRegistries.ENTITY_TYPE，并同步填充
 * ModEntities.SPAWN_EGG_ENTITIES 与 ModEntities.LITTLE_PERSON_ENTITIES。</p>
 *
 * <p><b>别名与加载顺序契约：</b></p>
 * <ul>
 *   <li>ModEntities 中对应的 39 个 LITTLE_PERSON_* 公共字段已改为源码兼容别名，
 *       字段名、泛型类型、实体 ID 与 Map 填充语义均与重构前一致；</li>
 *   <li>本类必须经由 ModEntities 的别名字段首次触发静态初始化：此时 ModEntities
 *       的 SPAWN_EGG_ENTITIES / GENERAL_RENDERERS / LITTLE_PERSON_ENTITIES
 *       三个 Map 已完成实例化，registerLittlePerson 才能安全写入；</li>
 *   <li>禁止在 ModEntities 初始化完成前直接加载本类（例如在其他实体注册字段之前
 *       引用本类），否则 Map 可能尚未实例化，或实体注册顺序被意外改变；</li>
 *   <li>39 个注册按本类字段声明顺序执行，注册 ID、EntityType 实例与两个 Map 的
 *       插入顺序与原实现逐项一致。</li>
 * </ul>
 */
public final class LittlePersonEntityTypes {
    private LittlePersonEntityTypes() {
    }

    public static final EntityType<PoisonousSlashEntity> POISONOUS_SLASH = registerLittlePerson(
            "poisonous_slash",
            FabricEntityType.Builder.createMob(
                    PoisonousSlashEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(PoisonousSlashEntity::createLittlePersonAttributes)
                            .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.9F)
            .clientTrackingRange(40)
    );
    public static final EntityType<CyborgEntity> CYBORG = registerLittlePerson(
            "cyborg",
            FabricEntityType.Builder.createMob(
                            CyborgEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(CyborgEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IronManEntity> IRON_MAN = registerLittlePerson(
            "iron_man",
            FabricEntityType.Builder.createMob(
                            IronManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IronManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IronManTrueEntity> IRON_MAN_TRUE = registerLittlePerson(
            "iron_man_true",
            FabricEntityType.Builder.createMob(
                            IronManTrueEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IronManTrueEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<TaiLinEntity> TAI_LIN = registerLittlePerson(
            "tai_lin",
            FabricEntityType.Builder.createMob(
                            TaiLinEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(TaiLinEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<FrenchSphereFlowEntity> FRENCH_SPHERE_FLOW = registerLittlePerson(
            "french_sphere_flow",
            FabricEntityType.Builder.createMob(
                            FrenchSphereFlowEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(FrenchSphereFlowEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<WildManEntity> WILD_MAN = registerLittlePerson(
            "wild_man",
            FabricEntityType.Builder.createMob(
                            WildManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(WildManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<WildBoarEntity> WILD_BOAR = registerLittlePerson(
            "wild_boar",
            FabricEntityType.Builder.createMob(
                            WildBoarEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(WildBoarEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<MagicManEntity> MAGIC_MAN = registerLittlePerson(
            "magic_man",
            FabricEntityType.Builder.createMob(
                            MagicManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(MagicManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<HeavenCrippledFeetEntity> HEAVEN_CRIPPLED_FEET = registerLittlePerson(
            "heaven_crippled_feet",
            FabricEntityType.Builder.createMob(
                            HeavenCrippledFeetEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HeavenCrippledFeetEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );

    public static final EntityType<BloodyBladeEntity> BLOODY_BLADE = registerLittlePerson(
            "bloody_blade",
            FabricEntityType.Builder.createMob(
                            BloodyBladeEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(BloodyBladeEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<HumanShieldEntity> HUMAN_SHIELD = registerLittlePerson(
            "human_shield",
            FabricEntityType.Builder.createMob(
                            HumanShieldEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HumanShieldEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );

    public static final EntityType<HumanHammerEntity> HUMAN_HAMMER = registerLittlePerson(
            "human_hammer",
            FabricEntityType.Builder.createMob(
                            HumanHammerEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HumanHammerEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonSoldierEntity> LITTLE_PERSON_SOLDIER = registerLittlePerson(
            "little_person_soldier",
            FabricEntityType.Builder.createMob(
                            LittlePersonSoldierEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonSoldierEntity::createLittlePersonMilitiaAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonSoldierArcherEntity> LITTLE_PERSON_SOLDIER_ARCHER = registerLittlePerson(
            "little_person_soldier_archer",
            FabricEntityType.Builder.createMob(
                            LittlePersonSoldierArcherEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonSoldierArcherEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<SexEntity> SEX_ENTITY = registerLittlePerson(
            "sex_entity",
            FabricEntityType.Builder.createMob(
                            SexEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(SexEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<AngelCyborgEntity> ANGEL_CYBORG = registerLittlePerson(
            "angel_cyborg",
            FabricEntityType.Builder.createMob(
                            AngelCyborgEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(AngelCyborgEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LivingGhostEntity> LIVING_GHOST = registerLittlePerson(
            "living_ghost",
            FabricEntityType.Builder.createMob(
                            LivingGhostEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LivingGhostEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<ScatteredDemonEntity> SCATTERED_DEMON = registerLittlePerson(
            "scattered_demon",
            FabricEntityType.Builder.createMob(
                            ScatteredDemonEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(ScatteredDemonEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<NinjaEntity> NINJA = registerLittlePerson(
            "ninja",
            FabricEntityType.Builder.createMob(
                            NinjaEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(NinjaEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LaserManEntity> LASER_MAN = registerLittlePerson(
            "laser_man",
            FabricEntityType.Builder.createMob(
                            LaserManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LaserManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<BloodManEntity> BLOOD_MAN = registerLittlePerson(
            "blood_man",
            FabricEntityType.Builder.createMob(
                            BloodManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(BloodManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IceManEntity> ICE_MAN = registerLittlePerson(
            "ice_man",
            FabricEntityType.Builder.createMob(
                            IceManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IceManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<YemoWenluEntity> YEMO_WENLU = registerLittlePerson(
            "yemo_wenlu",
            FabricEntityType.Builder.createMob(
                            YemoWenluEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(YemoWenluEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<RenfuEntity> RENFU = registerLittlePerson(
            "renfu",
            FabricEntityType.Builder.createMob(
                            RenfuEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(RenfuEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<ContradictionManEntity> CONTRADICTION_MAN = registerLittlePerson(
            "contradiction_man",
            FabricEntityType.Builder.createMob(
                            ContradictionManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(ContradictionManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<MaceManEntity> MACE_MAN = registerLittlePerson(
            "mace_man",
            FabricEntityType.Builder.createMob(
                            MaceManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(MaceManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonBoxerEntity> LITTLE_PERSON_BOXER = registerLittlePerson(
            "little_person_boxer",
            FabricEntityType.Builder.createMob(
                            LittlePersonBoxerEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonBoxerEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonCityGuardEntity> LITTLE_PERSON_CITY_GUARD = registerLittlePerson(
            "little_person_city_guard",
            FabricEntityType.Builder.createMob(
                            LittlePersonCityGuardEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonCityGuardEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonServantEntity> LITTLE_PERSON_SERVANT = registerLittlePerson(
            "little_person_servant",
            FabricEntityType.Builder.createMob(
                            LittlePersonServantEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonServantEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<SevenHarvestLittlePersonEntity> SEVEN_HARVEST_LITTLE_PERSON = registerLittlePerson(
            "seven_harvest_little_person",
            FabricEntityType.Builder.createMob(
                            SevenHarvestLittlePersonEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(SevenHarvestLittlePersonEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<KnifeLittlePersonEntity> KNIFE_LITTLE_PERSON = registerLittlePerson(
            "knife_little_person",
            FabricEntityType.Builder.createMob(
                            KnifeLittlePersonEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(KnifeLittlePersonEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<Xbot002Entity> XBOT002 = registerLittlePerson(
            "xbot002",
            FabricEntityType.Builder.createMob(
                            Xbot002Entity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(Xbot002Entity::createLittlePersonAttributes)
                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(40)
                    .fireImmune()
    );
    public static final EntityType<EliteLittlePersonGuardEntity> ELITE_LITTLE_PERSON_GUARD = registerLittlePerson(
            "elite_little_person_guard",
            FabricEntityType.Builder.createMob(
                            EliteLittlePersonGuardEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(EliteLittlePersonGuardEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<ThreeCompanionsEntity> THREE_COMPANIONS = registerLittlePerson(
            "three_companions",
            FabricEntityType.Builder.createMob(
                            ThreeCompanionsEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(ThreeCompanionsEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<GreenManEntity> GREEN_MAN = registerLittlePerson(
            "green_man",
            FabricEntityType.Builder.createMob(
                            GreenManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(GreenManEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonMedicEntity> LITTLE_PERSON_MEDIC = registerLittlePerson(
            "little_person_medic",
            FabricEntityType.Builder.createMob(
                            LittlePersonMedicEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonMedicEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonGeneralEntity> LITTLE_PERSON_GENERAL = registerLittlePerson(
            "little_person_general",
            FabricEntityType.Builder.createMob(
                            LittlePersonGeneralEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGeneralEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<MacroSamuraiEntity> MACRO_SAMURAI = registerLittlePerson(
            "macro_samurai",
            FabricEntityType.Builder.createMob(
                            MacroSamuraiEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(MacroSamuraiEntity::createLittlePersonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );

    public static <T extends Mob> EntityType<T> registerLittlePerson(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        ModEntities.SPAWN_EGG_ENTITIES.put(name, type);
        ModEntities.LITTLE_PERSON_ENTITIES.put(name, type);
        return type;
    }
}
