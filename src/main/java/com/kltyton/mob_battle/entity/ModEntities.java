package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntity;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectile;
import com.kltyton.mob_battle.entity.bullet.IceArrowEntity;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.cbot.Cbot002Entity;
import com.kltyton.mob_battle.entity.cbot.CbotSnowballEntity;
import com.kltyton.mob_battle.entity.cbot.SnowmanIceBlockEntity;
import com.kltyton.mob_battle.entity.customfireball.MagmaLobsterBigFireballEntity;
import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongEntity;
import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongLargeProjectileEntity;
import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongSmallProjectileEntity;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.diamondgiant.DiamondGiantEntity;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntity;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import com.kltyton.mob_battle.entity.evoker.SuperEvokerEntity;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import com.kltyton.mob_battle.entity.flowerfairy.FlowerFairyEntity;
import com.kltyton.mob_battle.entity.golem.ChestGolemEntity;
import com.kltyton.mob_battle.entity.golem.StrongMinEntity;
import com.kltyton.mob_battle.entity.hiddeneye.HiddenEyeEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.soldier.LittlePersonSoldierArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonWorkerEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.soldier.LittlePersonSoldierEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.*;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requested.*;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.entity.registry.LittlePersonEntityTypes;
import com.kltyton.mob_battle.entity.registry.AttackDroneEntityTypes;
import com.kltyton.mob_battle.entity.registry.BlueIronGolemEntityTypes;
import com.kltyton.mob_battle.entity.registry.BossMonsterEntityTypes;
import com.kltyton.mob_battle.entity.registry.CoalSilverfishEntityTypes;
import com.kltyton.mob_battle.entity.registry.ChuanRenGongEntityTypes;
import com.kltyton.mob_battle.entity.registry.HighbirdEntityTypes;
import com.kltyton.mob_battle.entity.registry.PassiveCreatureEntityTypes;
import com.kltyton.mob_battle.entity.registry.SilencePhantomEntityTypes;
import com.kltyton.mob_battle.entity.registry.SkullEntityTypes;
import com.kltyton.mob_battle.entity.registry.SugarManScorpionEntityTypes;
import com.kltyton.mob_battle.entity.registry.TreatmentDroneEntityTypes;
import com.kltyton.mob_battle.entity.registry.VillagerKingdomEntityTypes;
import com.kltyton.mob_battle.entity.registry.VillagerVariantEntityTypes;
import com.kltyton.mob_battle.entity.registry.VehicleEntityTypes;
import com.kltyton.mob_battle.entity.registry.IceSoldierEntityTypes;
import com.kltyton.mob_battle.entity.registry.VanillaVariantEntityTypes;
import com.kltyton.mob_battle.entity.registry.WitherFactionEntityTypes;
import com.kltyton.mob_battle.entity.registry.CommandedUnitEntityTypes;
import com.kltyton.mob_battle.entity.registry.DiamondGiantEntityTypes;
import com.kltyton.mob_battle.entity.registry.SilverfishEntityTypes;
import com.kltyton.mob_battle.entity.registry.RareCreatureEntityTypes;
import com.kltyton.mob_battle.entity.registry.RoughWhiteZetsuEntityTypes;
import com.kltyton.mob_battle.entity.registry.LittlePersonBaseEntityTypes;
import com.kltyton.mob_battle.entity.registry.SkullSupportEntityTypes;
import com.kltyton.mob_battle.entity.registry.LittlePersonSkillEffectEntityTypes;
import com.kltyton.mob_battle.entity.registry.HazardEntityTypes;
import com.kltyton.mob_battle.entity.registry.SpecialProjectileEntityTypes;
import com.kltyton.mob_battle.entity.registry.LobsterEntityTypes;
import com.kltyton.mob_battle.entity.lobster.LobsterEntity;
import com.kltyton.mob_battle.entity.lobster.MagmaLobsterEntity;
import com.kltyton.mob_battle.entity.meteorite.EnderDragonMeteoriteEntity;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import com.kltyton.mob_battle.entity.min.YoungMinEntity;
import com.kltyton.mob_battle.entity.piglingeneral.PiglinGeneralEntity;
import com.kltyton.mob_battle.entity.cloud.ModifiedDragonBreathCloud;
import com.kltyton.mob_battle.entity.hazard.PoisonousBeachEntity;
import com.kltyton.mob_battle.entity.shield.ShieldEntity;
import com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity;
import com.kltyton.mob_battle.entity.projectile.LittleStoneEntity;
import com.kltyton.mob_battle.entity.projectile.MoneyGunProjectileEntity;
import com.kltyton.mob_battle.entity.roughwhitezetsu.RoughWhiteZetsuEntity;
import com.kltyton.mob_battle.entity.vehicle.obsidianboat.ObsidianBoatEntity;
import com.kltyton.mob_battle.entity.littleperson.icesoldier.IceSoldierEntity;
import com.kltyton.mob_battle.entity.registry.ProjectileEntityTypes;
import com.kltyton.mob_battle.entity.silencephantom.SilencePhantomEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.*;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.entity.skull.mage.NewSkullMageEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import com.kltyton.mob_battle.entity.skull.mage.SummonedSkeletonEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import com.kltyton.mob_battle.entity.snowgolem.NewSnowGolemEntity;
import com.kltyton.mob_battle.entity.summon.SummonedVexEntity;
import com.kltyton.mob_battle.entity.sugarmanscorpion.SugarManScorpion;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillager;
import com.kltyton.mob_battle.entity.villager.villagerking.VillagerKingEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.entity.villager.trading.EvokerVillagerEntity;
import com.kltyton.mob_battle.entity.villager.trading.PiglinVillagerEntity;
import com.kltyton.mob_battle.entity.villager.trading.WitherSkeletonVillagerEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralAxeEntity;
import com.kltyton.mob_battle.entity.voidcell.VoidCellEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullBulletEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.DualBladeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.ShieldAxeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.zombie.Zombie;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体类型兼容门面。
 *
 * <p>公开注册键、实体类型字段和三张兼容映射保持原名称，避免破坏既有源码调用、
 * 存档标识与数据生成入口。实体构造和即时注册已按领域下沉到
 * {@code entity.registry}；本类只保留稳定别名、历史注册顺序及外部扩展入口。</p>
 *
 * <p>不要在此重新加入实体业务逻辑。新增实体应进入职责明确的领域类型类，并由本门面
 * 暴露兼容字段；需要即时注册的类型统一通过
 * {@link com.kltyton.mob_battle.entity.registry.EntityRegistrySupport}。</p>
 */
public class ModEntities {
    /** 需要自动生成刷怪蛋的实体目录；键保持既有注册 ID。 */
    public static Map<String, EntityType<?>> SPAWN_EGG_ENTITIES = new HashMap<>();
    /** 使用通用客户端渲染器的实体目录。 */
    public static Map<String, EntityType<?>> GENERAL_RENDERERS = new HashMap<>();
    /** 小人族实体目录，供渲染、数据生成与兼容入口复用。 */
    public static Map<String, EntityType<?>> LITTLE_PERSON_ENTITIES = new HashMap<>();
    public static final ResourceKey<EntityType<?>> militia_warrior_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"militia_warrior_villager"));
    public static final ResourceKey<EntityType<?>> militia_archer_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"militia_archer_villager"));
    public static final ResourceKey<EntityType<?>> warrior_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"warrior_villager"));
    public static final ResourceKey<EntityType<?>> archer_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"archer_villager"));
    public static final ResourceKey<EntityType<?>> wither_skeleton_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skeleton_villager"));
    public static final ResourceKey<EntityType<?>> piglin_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"piglin_villager"));
    public static final ResourceKey<EntityType<?>> evoker_villager = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"evoker_villager"));
    public static final ResourceKey<EntityType<?>> void_cell = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"void_cell"));
    public static final ResourceKey<EntityType<?>> xun_sheng= ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"xun_sheng"));
    public static final ResourceKey<EntityType<?>> deep_creature = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"deep_creature"));
    public static final ResourceKey<EntityType<?>> highbird_baby = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_baby"));
    public static final ResourceKey<EntityType<?>> highbird_teenage = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_teenage"));
    public static final ResourceKey<EntityType<?>> highbird_adulthood = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_adulthood"));
    public static final ResourceKey<EntityType<?>> highbird_egg = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_egg"));
    public static final ResourceKey<EntityType<?>> bigfireball = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"bigfireball"));
    public static final ResourceKey<EntityType<?>> meteorite = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"meteorite"));
    public static final ResourceKey<EntityType<?>> missile = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"missile"));
    public static final ResourceKey<EntityType<?>> firewall = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"fairewall"));
    public static final ResourceKey<EntityType<?>> blue_iron_golem = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"blue_iron_golem"));
    public static final ResourceKey<EntityType<?>> sugar_man_scorpion = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"sugar_man_scorpion"));
    public static final ResourceKey<EntityType<?>> wither_skeleton_king = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skeleton_king"));
    public static final ResourceKey<EntityType<?>> vindicator_general = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"vindicator_general"));
    public static final ResourceKey<EntityType<?>> vindicator_general_axe = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"vindicator_general_axe"));
    public static final ResourceKey<EntityType<?>> hulkbuster = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"hulkbuster"));
    public static final ResourceKey<EntityType<?>> skull_king = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_king"));
    public static final ResourceKey<EntityType<?>> skull_archer = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_archer"));
    public static final ResourceKey<EntityType<?>> skull_warrior = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_warrior"));
    public static final ResourceKey<EntityType<?>> skull_mage = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_mage"));
    public static final ResourceKey<EntityType<?>> summoned_skeleton = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"summoned_skeleton"));
    public static final ResourceKey<EntityType<?>> young_min = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"young_min"));
    public static final ResourceKey<EntityType<?>> hidden_eye = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"hidden_eye"));
    public static final ResourceKey<EntityType<?>> silence_phantom = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"silence_phantom"));
    public static final ResourceKey<EntityType<?>> coal_silverfish = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"coal_silverfish"));
    public static final ResourceKey<EntityType<?>> enhanced_wither = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"enhanced_wither"));
    public static final ResourceKey<EntityType<?>> dual_blade_wither_skeleton = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"dual_blade_wither_skeleton"));
    public static final ResourceKey<EntityType<?>> shield_axe_wither_skeleton = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"shield_axe_wither_skeleton"));
    public static final ResourceKey<EntityType<?>> cbot002 = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"cbot002"));
    public static final ResourceKey<EntityType<?>> cbot_snowball = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"cbot_snowball"));
    public static final ResourceKey<EntityType<?>> piglin_general = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"piglin_general"));
    public static final ResourceKey<EntityType<?>> bow_zombie_mod = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"bow_zombie_mod"));
    public static final ResourceKey<EntityType<?>> piglin_brute_spear_mod = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"piglin_brute_spear_mod"));
    public static final ResourceKey<EntityType<?>> wither_skeleton_dog = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skeleton_dog"));
    public static final ResourceKey<EntityType<?>> laser = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"laser"));
    public static final ResourceKey<EntityType<?>> blood_sword_energy = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"blood_sword_energy"));
    public static final ResourceKey<EntityType<?>> ice_sword_energy = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_sword_energy"));
    public static final ResourceKey<EntityType<?>> ice_bomb = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_bomb"));
    public static final ResourceKey<EntityType<?>> ice_fangs = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_fangs"));
    public static final ResourceKey<EntityType<?>> diamond_giant = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"diamond_giant"));
    public static final ResourceKey<EntityType<?>> ninja_clone = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"ninja_clone"));
    public static final ResourceKey<EntityType<?>> elemental_sword_projectile = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"elemental_sword_projectile"));
    public static final ResourceKey<EntityType<?>> green_concrete_projectile = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"green_concrete_projectile"));

    public static final ResourceKey<EntityType<?>> bullet = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"bullet"));
    public static final ResourceKey<EntityType<?>> wither_skull_bullet = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skull_bullet"));
    public static final ResourceKey<EntityType<?>> iron_man_bullet = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"iron_man_bullet"));
    public static final ResourceKey<EntityType<?>> little_arrow = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_arrow"));
    public static final ResourceKey<EntityType<?>> stone_arrow = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"stone_arrow"));
    public static final ResourceKey<EntityType<?>> poison_arrow = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"poison_arrow"));
    public static final ResourceKey<EntityType<?>> spear_bullet = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"spear_bullet"));
    public static final ResourceKey<EntityType<?>> villager_iron_golem = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"iron_golem"));
    public static final ResourceKey<EntityType<?>> villager_king = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"villager_king"));
    public static final ResourceKey<EntityType<?>> attack_drone =  ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"attack_drone"));
    public static final ResourceKey<EntityType<?>> treatment_drone = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"treatment_drone"));
    public static final ResourceKey<EntityType<?>> little_person_civilian = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_civilian"));
    public static final ResourceKey<EntityType<?>> little_person_militia = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_militia"));
    public static final ResourceKey<EntityType<?>> little_person_archer = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_archer"));
    public static final ResourceKey<EntityType<?>> little_person_giant = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_giant"));
    public static final ResourceKey<EntityType<?>> little_person_guard = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_guard"));
    public static final ResourceKey<EntityType<?>> little_person_king = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_king"));
    public static final EntityType<MilitiaWarriorVillager> MILITIA_WARRIOR_VILLAGER = VillagerVariantEntityTypes.MILITIA_WARRIOR_VILLAGER;
    public static final EntityType<MilitiaArcherVillager> MILITIA_ARCHER_VILLAGER = VillagerVariantEntityTypes.MILITIA_ARCHER_VILLAGER;
    public static final EntityType<WarriorVillager> WARRIOR_VILLAGER = VillagerVariantEntityTypes.WARRIOR_VILLAGER;
    public static final EntityType<ArcherVillager> ARCHER_VILLAGER = VillagerVariantEntityTypes.ARCHER_VILLAGER;
    public static final EntityType<WitherSkeletonVillagerEntity> WITHER_SKELETON_VILLAGER = VillagerVariantEntityTypes.WITHER_SKELETON_VILLAGER;
    public static final EntityType<PiglinVillagerEntity> PIGLIN_VILLAGER = VillagerVariantEntityTypes.PIGLIN_VILLAGER;
    public static final EntityType<EvokerVillagerEntity> EVOKER_VILLAGER = VillagerVariantEntityTypes.EVOKER_VILLAGER;
    public static final EntityType<VoidCellEntity> VOID_CELL = BossMonsterEntityTypes.VOID_CELL;

    public static final EntityType<XunShengEntity> XUN_SHENG = BossMonsterEntityTypes.XUN_SHENG;
    public static final EntityType<DeepCreatureEntity> DEEP_CREATURE = BossMonsterEntityTypes.DEEP_CREATURE;
    public static final EntityType<WitherSkeletonKingEntity> WITHER_SKELETON_KING = BossMonsterEntityTypes.WITHER_SKELETON_KING;
    public static final EntityType<VindicatorGeneralEntity> VINDICATOR_GENERAL = BossMonsterEntityTypes.VINDICATOR_GENERAL;
    public static final EntityType<HulkbusterEntity> HULKBUSTER = BossMonsterEntityTypes.HULKBUSTER;

    public static final EntityType<SkullKingEntity> SKULL_KING = SkullEntityTypes.SKULL_KING;
    public static final EntityType<SkullArcherEntity> SKULL_ARCHER = SkullEntityTypes.SKULL_ARCHER;
    public static final EntityType<SkullWarriorEntity> SKULL_WARRIOR = SkullEntityTypes.SKULL_WARRIOR;
    public static final EntityType<SkullMageEntity> SKULL_MAGE = SkullEntityTypes.SKULL_MAGE;
    public static final EntityType<SummonedSkeletonEntity> SUMMONED_SKELETON = SkullEntityTypes.SUMMONED_SKELETON;
    public static final EntityType<YoungMinEntity> YOUNG_MIN = PassiveCreatureEntityTypes.YOUNG_MIN;
    public static final EntityType<HiddenEyeEntity> HIDDEN_EYE = PassiveCreatureEntityTypes.HIDDEN_EYE;

    /* 鈫撯啌鈫?鍦?ModEntities 绫婚噷鏂板 鈫撯啌鈫?*/
    public static final EntityType<HighbirdBabyEntity> HIGHBIRD_BABY = HighbirdEntityTypes.HIGHBIRD_BABY;
    /* 鈫撯啌鈫?鍦?ModEntities 绫婚噷鏂板 鈫撯啌鈫?*/
    public static final EntityType<HighbirdEggEntity> HIGHBIRD_EGG = HighbirdEntityTypes.HIGHBIRD_EGG;
    /* 鈫撯啌鈫?鍦?ModEntities 绫婚噷鏂板 鈫撯啌鈫?*/
    public static final EntityType<HighbirdTeenageEntity> HIGHBIRD_TEENAGE = HighbirdEntityTypes.HIGHBIRD_TEENAGE;
    public static final EntityType<HighbirdAdulthoodEntity> HIGHBIRD_ADULTHOOD = HighbirdEntityTypes.HIGHBIRD_ADULTHOOD;
    public static final EntityType<VillagerIronGolemEntity> VILLAGER_IRON_GOLEM_ENTITY = VillagerKingdomEntityTypes.VILLAGER_IRON_GOLEM_ENTITY;
    public static final EntityType<VillagerKingEntity> VILLAGER_KING_ENTITY = VillagerKingdomEntityTypes.VILLAGER_KING_ENTITY;
    /**
     * 弹射物 / 危险区域实体（projectile / hazard 家族）的源码兼容别名。
     *
     * <p>真实类型构造表达式已迁移至
     * {@link com.kltyton.mob_battle.entity.registry.ProjectileEntityTypes}，
     * 本块保留 11 个同类型、同字段名、同实体 ID（含 fairewall）的别名，保证现有调用方
     * （数据生成、物品、渲染器、技能逻辑等）源码兼容。</p>
     *
     * <p><b>加载顺序契约：</b>本类声明顺序中 SPAWN_EGG_ENTITIES、GENERAL_RENDERERS、
     * LITTLE_PERSON_ENTITIES 三个 Map 均位于本块之前完成实例化；首次求值本块任一别名时
     * 触发 ProjectileEntityTypes 静态初始化，按原声明顺序完成全部 11 个类型构造；
     * {@link #init()} 仍按历史时机执行注册。注册 ID、尺寸、分类、客户端追踪范围与
     * 更新频率均与重构前一致。这 11 个实体原本不参与
     * SPAWN_EGG_ENTITIES / GENERAL_RENDERERS 的 Map 填充，别名重构不改变该语义。</p>
     */
    public static final EntityType<WitherSkullBulletEntity> WITHER_SKULL_BULLET_ENTITY = ProjectileEntityTypes.WITHER_SKULL_BULLET_ENTITY;
    public static final EntityType<IronManBulletEntity> IRON_MAN_BULLET_ENTITY = ProjectileEntityTypes.IRON_MAN_BULLET_ENTITY;
    public static final EntityType<BulletEntity> BULLET_ENTITY = ProjectileEntityTypes.BULLET_ENTITY;
    public static final EntityType<LittleArrowEntity> LITTLE_ARROW = ProjectileEntityTypes.LITTLE_ARROW;
    public static final EntityType<LittleArrowEntity> STONE_ARROW = ProjectileEntityTypes.STONE_ARROW;
    public static final EntityType<LittleArrowEntity> POISON_ARROW = ProjectileEntityTypes.POISON_ARROW;
    public static final EntityType<LittleArrowEntity> SPEAR_BULLET = ProjectileEntityTypes.SPEAR_BULLET;
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL = ProjectileEntityTypes.BIG_CUSTOM_FIREBALL;
    public static final EntityType<MeteoriteEntity> METEORITE = ProjectileEntityTypes.METEORITE;
    public static final EntityType<MissileEntity> MISSILE = ProjectileEntityTypes.MISSILE;
    public static final EntityType<FireWallEntity> FIRE_WALL = ProjectileEntityTypes.FIRE_WALL;
    public static final EntityType<BlueIronGolemEntity> BLUE_IRON_GOLEM = BlueIronGolemEntityTypes.BLUE_IRON_GOLEM;
    public static final EntityType<SugarManScorpion> SUGAR_MAN_SCORPION = SugarManScorpionEntityTypes.SUGAR_MAN_SCORPION;


    public static final EntityType<AttackDroneEntity> ATTACK_DRONE = AttackDroneEntityTypes.ATTACK_DRONE;
    public static final EntityType<SilencePhantomEntity> SILENCE_PHANTOM = SilencePhantomEntityTypes.SILENCE_PHANTOM;
    public static final EntityType<CoalSilverfishEntity> COAL_SILVERFISH = CoalSilverfishEntityTypes.COAL_SILVERFISH;
    public static final EntityType<Zombie> BOW_ZOMBIE_MOD = VanillaVariantEntityTypes.BOW_ZOMBIE_MOD;
    public static final EntityType<PiglinBrute> PIGLIN_BRUTE_SPEAR_MOD = VanillaVariantEntityTypes.PIGLIN_BRUTE_SPEAR_MOD;
    public static final EntityType<EnhancedWitherEntity> ENHANCED_WITHER = WitherFactionEntityTypes.ENHANCED_WITHER;
    public static final EntityType<DualBladeWitherSkeletonEntity> DUAL_BLADE_WITHER_SKELETON = WitherFactionEntityTypes.DUAL_BLADE_WITHER_SKELETON;
    public static final EntityType<ShieldAxeWitherSkeletonEntity> SHIELD_AXE_WITHER_SKELETON = WitherFactionEntityTypes.SHIELD_AXE_WITHER_SKELETON;
    public static final EntityType<Cbot002Entity> CBOT002 = CommandedUnitEntityTypes.CBOT002;
    public static final EntityType<PiglinGeneralEntity> PIGLIN_GENERAL = CommandedUnitEntityTypes.PIGLIN_GENERAL;
    public static final EntityType<WitherSkeletonDogEntity> WITHER_SKELETON_DOG = CommandedUnitEntityTypes.WITHER_SKELETON_DOG;
    public static final EntityType<SummonedVexEntity> SUMMONED_VEX = CommandedUnitEntityTypes.SUMMONED_VEX;
    public static final EntityType<NewSnowGolemEntity> NEW_SNOW_GOLEM = CommandedUnitEntityTypes.NEW_SNOW_GOLEM;
    public static final EntityType<ChestGolemEntity> CHEST_GOLEM = CommandedUnitEntityTypes.CHEST_GOLEM;
    public static final EntityType<StrongMinEntity> STRONG_MIN = CommandedUnitEntityTypes.STRONG_MIN;
    public static final EntityType<LiruiSilverfishEntity> LIRUI_SILVERFISH = SilverfishEntityTypes.LIRUI_SILVERFISH;
    public static final EntityType<DrillSilverfishEntity> DRILL_SILVERFISH = SilverfishEntityTypes.DRILL_SILVERFISH;
    public static final EntityType<PoisonousSilverfishEntity> POISONOUS_SILVERFISH = SilverfishEntityTypes.POISONOUS_SILVERFISH;
    public static final EntityType<LoadSilverfishEntity> LOAD_SILVERFISH = SilverfishEntityTypes.LOAD_SILVERFISH;
    public static final EntityType<LongWhipSilverfishEntity> LONG_WHIP_SILVERFISH = SilverfishEntityTypes.LONG_WHIP_SILVERFISH;
    public static final EntityType<AngrySilverfishEntity> ANGRY_SILVERFISH = SilverfishEntityTypes.ANGRY_SILVERFISH;
    public static final EntityType<FlowerFairyEntity> FLOWER_FAIRY = RareCreatureEntityTypes.FLOWER_FAIRY;
    public static final EntityType<SuperEvokerEntity> SUPER_EVOKER = RareCreatureEntityTypes.SUPER_EVOKER;
    public static final EntityType<ChuanRenGongEntity> CHUAN_REN_GONG = ChuanRenGongEntityTypes.CHUAN_REN_GONG;
    public static final EntityType<ChuanRenGongSmallProjectileEntity> CHUAN_REN_GONG_SMALL_PROJECTILE = ChuanRenGongEntityTypes.SMALL_PROJECTILE;
    public static final EntityType<ChuanRenGongLargeProjectileEntity> CHUAN_REN_GONG_LARGE_PROJECTILE = ChuanRenGongEntityTypes.LARGE_PROJECTILE;
    public static final EntityType<RoughWhiteZetsuEntity> ROUGH_WHITE_ZETSU = RoughWhiteZetsuEntityTypes.ROUGH_WHITE_ZETSU;
    public static final EntityType<ObsidianBoatEntity> OBSIDIAN_BOAT = VehicleEntityTypes.OBSIDIAN_BOAT;
    public static final EntityType<IceSoldierEntity> ICE_SOLDIER = IceSoldierEntityTypes.ICE_SOLDIER;
    public static final EntityType<TreatmentDroneEntity> TREATMENT_DRONE = TreatmentDroneEntityTypes.TREATMENT_DRONE;
    public static final EntityType<LittlePersonCivilianEntity> LITTLE_PERSON_CIVILIAN = LittlePersonBaseEntityTypes.LITTLE_PERSON_CIVILIAN;
    public static final EntityType<LittlePersonWorkerEntity> LITTLE_PERSON_WORKER = LittlePersonBaseEntityTypes.LITTLE_PERSON_WORKER;
    public static final EntityType<LittlePersonMilitiaEntity> LITTLE_PERSON_MILITIA = LittlePersonBaseEntityTypes.LITTLE_PERSON_MILITIA;
    public static final EntityType<LittlePersonArcherEntity> LITTLE_PERSON_ARCHER = LittlePersonBaseEntityTypes.LITTLE_PERSON_ARCHER;
    public static final EntityType<LittlePersonGiantEntity> LITTLE_PERSON_GIANT = LittlePersonBaseEntityTypes.LITTLE_PERSON_GIANT;
    public static final EntityType<LittlePersonGuardEntity> LITTLE_PERSON_GUARD = LittlePersonBaseEntityTypes.LITTLE_PERSON_GUARD;
    public static final EntityType<LittlePersonKingEntity> LITTLE_PERSON_KING = LittlePersonBaseEntityTypes.LITTLE_PERSON_KING;
    public static final EntityType<NewSkullMageEntity> NEW_SKULL_MAGE = SkullSupportEntityTypes.NEW_SKULL_MAGE;
    /**
     * 小人物技能实体（LITTLE_PERSON_ENTITIES 家族）的源码兼容别名。
     *
     * <p>真实注册表达式已迁移至
     * {@link com.kltyton.mob_battle.entity.registry.LittlePersonEntityTypes}，
     * 本块保留 39 个同类型、同字段名、同实体 ID 的别名，保证现有调用方（数据生成、
     * 物品、渲染器等）源码兼容。</p>
     *
     * <p><b>加载顺序契约：</b>本类声明顺序中 SPAWN_EGG_ENTITIES、GENERAL_RENDERERS、
     * LITTLE_PERSON_ENTITIES 三个 Map 均位于本块之前完成实例化；首次求值本块任一别名时
     * 触发 LittlePersonEntityTypes 静态初始化，按原声明顺序完成全部 39 个注册与 Map 填充，
     * 因此注册 ID、注册顺序与两个 Map 的插入顺序均与重构前一致。</p>
     */
    public static final EntityType<PoisonousSlashEntity> POISONOUS_SLASH = LittlePersonEntityTypes.POISONOUS_SLASH;
    public static final EntityType<CyborgEntity> CYBORG = LittlePersonEntityTypes.CYBORG;
    public static final EntityType<IronManEntity> IRON_MAN = LittlePersonEntityTypes.IRON_MAN;
    public static final EntityType<IronManTrueEntity> IRON_MAN_TRUE = LittlePersonEntityTypes.IRON_MAN_TRUE;
    public static final EntityType<TaiLinEntity> TAI_LIN = LittlePersonEntityTypes.TAI_LIN;
    public static final EntityType<FrenchSphereFlowEntity> FRENCH_SPHERE_FLOW = LittlePersonEntityTypes.FRENCH_SPHERE_FLOW;
    public static final EntityType<WildManEntity> WILD_MAN = LittlePersonEntityTypes.WILD_MAN;
    public static final EntityType<WildBoarEntity> WILD_BOAR = LittlePersonEntityTypes.WILD_BOAR;
    public static final EntityType<MagicManEntity> MAGIC_MAN = LittlePersonEntityTypes.MAGIC_MAN;
    public static final EntityType<HeavenCrippledFeetEntity> HEAVEN_CRIPPLED_FEET = LittlePersonEntityTypes.HEAVEN_CRIPPLED_FEET;
    public static final EntityType<BloodyBladeEntity> BLOODY_BLADE = LittlePersonEntityTypes.BLOODY_BLADE;
    public static final EntityType<HumanShieldEntity> HUMAN_SHIELD = LittlePersonEntityTypes.HUMAN_SHIELD;
    public static final EntityType<HumanHammerEntity> HUMAN_HAMMER = LittlePersonEntityTypes.HUMAN_HAMMER;
    public static final EntityType<LittlePersonSoldierEntity> LITTLE_PERSON_SOLDIER = LittlePersonEntityTypes.LITTLE_PERSON_SOLDIER;
    public static final EntityType<LittlePersonSoldierArcherEntity> LITTLE_PERSON_SOLDIER_ARCHER = LittlePersonEntityTypes.LITTLE_PERSON_SOLDIER_ARCHER;
    public static final EntityType<SexEntity> SEX_ENTITY = LittlePersonEntityTypes.SEX_ENTITY;
    public static final EntityType<AngelCyborgEntity> ANGEL_CYBORG = LittlePersonEntityTypes.ANGEL_CYBORG;
    public static final EntityType<LivingGhostEntity> LIVING_GHOST = LittlePersonEntityTypes.LIVING_GHOST;
    public static final EntityType<ScatteredDemonEntity> SCATTERED_DEMON = LittlePersonEntityTypes.SCATTERED_DEMON;
    public static final EntityType<NinjaEntity> NINJA = LittlePersonEntityTypes.NINJA;
    public static final EntityType<LaserManEntity> LASER_MAN = LittlePersonEntityTypes.LASER_MAN;
    public static final EntityType<BloodManEntity> BLOOD_MAN = LittlePersonEntityTypes.BLOOD_MAN;
    public static final EntityType<IceManEntity> ICE_MAN = LittlePersonEntityTypes.ICE_MAN;
    public static final EntityType<YemoWenluEntity> YEMO_WENLU = LittlePersonEntityTypes.YEMO_WENLU;
    public static final EntityType<RenfuEntity> RENFU = LittlePersonEntityTypes.RENFU;
    public static final EntityType<ContradictionManEntity> CONTRADICTION_MAN = LittlePersonEntityTypes.CONTRADICTION_MAN;
    public static final EntityType<MaceManEntity> MACE_MAN = LittlePersonEntityTypes.MACE_MAN;
    public static final EntityType<LittlePersonBoxerEntity> LITTLE_PERSON_BOXER = LittlePersonEntityTypes.LITTLE_PERSON_BOXER;
    public static final EntityType<LittlePersonCityGuardEntity> LITTLE_PERSON_CITY_GUARD = LittlePersonEntityTypes.LITTLE_PERSON_CITY_GUARD;
    public static final EntityType<LittlePersonServantEntity> LITTLE_PERSON_SERVANT = LittlePersonEntityTypes.LITTLE_PERSON_SERVANT;
    public static final EntityType<SevenHarvestLittlePersonEntity> SEVEN_HARVEST_LITTLE_PERSON = LittlePersonEntityTypes.SEVEN_HARVEST_LITTLE_PERSON;
    public static final EntityType<KnifeLittlePersonEntity> KNIFE_LITTLE_PERSON = LittlePersonEntityTypes.KNIFE_LITTLE_PERSON;
    public static final EntityType<Xbot002Entity> XBOT002 = LittlePersonEntityTypes.XBOT002;
    public static final EntityType<EliteLittlePersonGuardEntity> ELITE_LITTLE_PERSON_GUARD = LittlePersonEntityTypes.ELITE_LITTLE_PERSON_GUARD;
    public static final EntityType<ThreeCompanionsEntity> THREE_COMPANIONS = LittlePersonEntityTypes.THREE_COMPANIONS;
    public static final EntityType<GreenManEntity> GREEN_MAN = LittlePersonEntityTypes.GREEN_MAN;
    public static final EntityType<LittlePersonMedicEntity> LITTLE_PERSON_MEDIC = LittlePersonEntityTypes.LITTLE_PERSON_MEDIC;
    public static final EntityType<LittlePersonGeneralEntity> LITTLE_PERSON_GENERAL = LittlePersonEntityTypes.LITTLE_PERSON_GENERAL;
    public static final EntityType<MacroSamuraiEntity> MACRO_SAMURAI = LittlePersonEntityTypes.MACRO_SAMURAI;
    public static final EntityType<SkillProjectileEntity> LASER = LittlePersonSkillEffectEntityTypes.LASER;
    public static final EntityType<SkillProjectileEntity> SEVEN_HARVEST_BULLET = LittlePersonSkillEffectEntityTypes.SEVEN_HARVEST_BULLET;
    public static final EntityType<SkillProjectileEntity> SEVEN_HARVEST_EXPLOSIVE_BULLET = LittlePersonSkillEffectEntityTypes.SEVEN_HARVEST_EXPLOSIVE_BULLET;
    public static final EntityType<SkillProjectileEntity> KNIFE_PROJECTILE = LittlePersonSkillEffectEntityTypes.KNIFE_PROJECTILE;
    public static final EntityType<SkillProjectileEntity> SKELETON_HEAD_PROJECTILE = LittlePersonSkillEffectEntityTypes.SKELETON_HEAD_PROJECTILE;
    public static final EntityType<SkillProjectileEntity> BLOOD_SWORD_ENERGY = LittlePersonSkillEffectEntityTypes.BLOOD_SWORD_ENERGY;
    public static final EntityType<SkillProjectileEntity> ICE_SWORD_ENERGY = LittlePersonSkillEffectEntityTypes.ICE_SWORD_ENERGY;
    public static final EntityType<SkillProjectileEntity> ICE_BOMB = LittlePersonSkillEffectEntityTypes.ICE_BOMB;
    public static final EntityType<SnowmanIceBlockEntity> SNOWMAN_ICE_BLOCK = LittlePersonSkillEffectEntityTypes.SNOWMAN_ICE_BLOCK;
    public static final EntityType<CbotSnowballEntity> CBOT_SNOWBALL = LittlePersonSkillEffectEntityTypes.CBOT_SNOWBALL;
    public static final EntityType<VindicatorGeneralAxeEntity> VINDICATOR_GENERAL_AXE = LittlePersonSkillEffectEntityTypes.VINDICATOR_GENERAL_AXE;
    public static final EntityType<SkillVisualEntity> ICE_FANGS = LittlePersonSkillEffectEntityTypes.ICE_FANGS;
    public static final EntityType<SkillVisualEntity> NINJA_CLONE = LittlePersonSkillEffectEntityTypes.NINJA_CLONE;
    public static final EntityType<DiamondGiantEntity> DIAMOND_GIANT = DiamondGiantEntityTypes.DIAMOND_GIANT;
    public static final EntityType<SkillVisualEntity> DIAMOND_GIANT_SPIKE = DiamondGiantEntityTypes.DIAMOND_GIANT_SPIKE;



    public static final EntityType<ShieldEntity> SHIELD = HazardEntityTypes.SHIELD;

    public static final EntityType<PoisonousBeachEntity> POISONOUS_BEACH = HazardEntityTypes.POISONOUS_BEACH;

    public static final EntityType<ModifiedDragonBreathCloud> MODIFIED_DRAGON_BREATH_CLOUD = HazardEntityTypes.MODIFIED_DRAGON_BREATH_CLOUD;
    public static final EntityType<EnderDragonMeteoriteEntity> ENDER_DRAGON_METEORITE = HazardEntityTypes.ENDER_DRAGON_METEORITE;

    public static final EntityType<MagmaLobsterBigFireballEntity> MAGMA_LOBBER_BIG_FIREBALL = SpecialProjectileEntityTypes.MAGMA_LOBBER_BIG_FIREBALL;
    public static final EntityType<IceArrowEntity> ICE_ARROW = SpecialProjectileEntityTypes.ICE_ARROW;
    public static final EntityType<GoldenTrailProjectile> GOLDEN_TRAIL_PROJECTILE = SpecialProjectileEntityTypes.GOLDEN_TRAIL_PROJECTILE;
    public static final EntityType<GoldenBulletEntity> GOLDEN_BULLET = SpecialProjectileEntityTypes.GOLDEN_BULLET;
    public static final EntityType<GreenConcreteProjectileEntity> GREEN_CONCRETE_PROJECTILE = SpecialProjectileEntityTypes.GREEN_CONCRETE_PROJECTILE;
    public static final EntityType<LittleStoneEntity> LITTLE_STONE_PROJECTILE = SpecialProjectileEntityTypes.LITTLE_STONE_PROJECTILE;
    public static final EntityType<ElementalSwordProjectileEntity> ELEMENTAL_SWORD_PROJECTILE = SpecialProjectileEntityTypes.ELEMENTAL_SWORD_PROJECTILE;
    public static final EntityType<MoneyGunProjectileEntity> MONEY_GUN_PROJECTILE = SpecialProjectileEntityTypes.MONEY_GUN_PROJECTILE;
    public static final EntityType<LobsterEntity> LOBSTER = LobsterEntityTypes.LOBSTER;
    public static final EntityType<MagmaLobsterEntity> MAGMA_LOBSTER = LobsterEntityTypes.MAGMA_LOBSTER;
    static {
        SPAWN_EGG_ENTITIES.put("enhanced_wither", ENHANCED_WITHER);
        SPAWN_EGG_ENTITIES.put("dual_blade_wither_skeleton", DUAL_BLADE_WITHER_SKELETON);
        SPAWN_EGG_ENTITIES.put("shield_axe_wither_skeleton", SHIELD_AXE_WITHER_SKELETON);
        SPAWN_EGG_ENTITIES.put("cbot002", CBOT002);
        SPAWN_EGG_ENTITIES.put("piglin_general", PIGLIN_GENERAL);
        SPAWN_EGG_ENTITIES.put("wither_skeleton_villager", WITHER_SKELETON_VILLAGER);
        SPAWN_EGG_ENTITIES.put("piglin_villager", PIGLIN_VILLAGER);
        SPAWN_EGG_ENTITIES.put("evoker_villager", EVOKER_VILLAGER);
        GENERAL_RENDERERS.put("cbot002", CBOT002);
        GENERAL_RENDERERS.put("piglin_general", PIGLIN_GENERAL);
    }
    public static void init() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, militia_warrior_villager, MILITIA_WARRIOR_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, militia_archer_villager, MILITIA_ARCHER_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, warrior_villager, WARRIOR_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, archer_villager, ARCHER_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, wither_skeleton_villager, WITHER_SKELETON_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, piglin_villager, PIGLIN_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, evoker_villager, EVOKER_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, void_cell, VOID_CELL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, xun_sheng, XUN_SHENG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, deep_creature, DEEP_CREATURE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, wither_skeleton_king, WITHER_SKELETON_KING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, vindicator_general, VINDICATOR_GENERAL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_king, SKULL_KING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_archer, SKULL_ARCHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_warrior, SKULL_WARRIOR);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_mage, SKULL_MAGE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, summoned_skeleton, SUMMONED_SKELETON);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, young_min, YOUNG_MIN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, hidden_eye, HIDDEN_EYE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, meteorite, METEORITE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_baby, HIGHBIRD_BABY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_egg, HIGHBIRD_EGG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_teenage, HIGHBIRD_TEENAGE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_adulthood, HIGHBIRD_ADULTHOOD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, bigfireball, BIG_CUSTOM_FIREBALL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, firewall, FIRE_WALL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, blue_iron_golem, BLUE_IRON_GOLEM);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, sugar_man_scorpion, SUGAR_MAN_SCORPION);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, wither_skull_bullet, WITHER_SKULL_BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, bullet, BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_arrow, LITTLE_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, stone_arrow, STONE_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, poison_arrow, POISON_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, hulkbuster, HULKBUSTER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, missile, MISSILE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, coal_silverfish, COAL_SILVERFISH);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, bow_zombie_mod, BOW_ZOMBIE_MOD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, piglin_brute_spear_mod, PIGLIN_BRUTE_SPEAR_MOD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, silence_phantom, SILENCE_PHANTOM);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, enhanced_wither, ENHANCED_WITHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, cbot002, CBOT002);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, piglin_general, PIGLIN_GENERAL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, iron_man_bullet, IRON_MAN_BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, spear_bullet, SPEAR_BULLET);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, villager_iron_golem, VILLAGER_IRON_GOLEM_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, villager_king, VILLAGER_KING_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, attack_drone, ATTACK_DRONE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, treatment_drone, TREATMENT_DRONE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_civilian, LITTLE_PERSON_CIVILIAN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_militia, LITTLE_PERSON_MILITIA);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_archer, LITTLE_PERSON_ARCHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_giant, LITTLE_PERSON_GIANT);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_guard, LITTLE_PERSON_GUARD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_king, LITTLE_PERSON_KING);
    }
    public static <T extends Entity> EntityType<T> createEntityType(String name, EntityType.Builder<T> builder, boolean registerSpawnEgg, boolean generalRenderer) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        if (registerSpawnEgg) SPAWN_EGG_ENTITIES.put(name, type);
        if (generalRenderer) GENERAL_RENDERERS.put(name, type);
        return type;
    }
    public static <T extends Mob> EntityType<T> createLittlePersonEntityType(String name, EntityType.Builder<T> builder) {
        return LittlePersonEntityTypes.registerLittlePerson(name, builder);
    }
}


