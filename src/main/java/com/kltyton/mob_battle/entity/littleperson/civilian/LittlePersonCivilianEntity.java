package com.kltyton.mob_battle.entity.littleperson.civilian;

import com.google.common.collect.ImmutableList;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.sensor.ModSensorTypes;
import com.kltyton.mob_battle.items.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class LittlePersonCivilianEntity extends Villager implements LittlePersonEntity {
    private static final String NEXT_RESTOCK_TIME_KEY = "MobBattleNextLittlePersonRestock";
    private static final long DAY_TICKS = 24000L;
    private static final int TRADE_COUNT = 2;
    private static final List<LittlePersonTradeFactory> TRADE_POOL = List.of(
            () -> offer(Blocks.HAY_BLOCK, 6, ModItems.NIBI_BAG, 1, 3),
            () -> offer(ModItems.NIBI, 2, ModItems.LITTLE_PERSON_TOOL, 1, 3),
            () -> offer(ModItems.NIBI, 1, Items.GLOWSTONE, 1, 9),
            () -> offer(Items.BREAD, 20, ModItems.NIBI, 2, 3),
            () -> offer(Items.BAKED_POTATO, 20, ModItems.NIBI, 2, 3),
            () -> offer(Items.FERMENTED_SPIDER_EYE, 1, ModItems.NIBI, 3, 3),
            () -> offer(Blocks.HAY_BLOCK, 1, ModItems.NIBI, 1, 3),
            () -> offer(ModItems.NIBI, 1, Items.SPRUCE_LOG, 1, 3),
            () -> offer(ModItems.NIBI, 3, Blocks.PODZOL, 3, 3),
            () -> offer(ModItems.NIBI, 2, Items.GLOWSTONE, 2, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.MOSS_BLOCK, 1, 3),
            () -> offer(ModItems.NIBI, 5, Blocks.MANGROVE_PROPAGULE, 1, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.WILDFLOWERS, 1, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.BLUE_WOOL, 1, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.ORANGE_TULIP, 1, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.POPPY, 1, 3),
            () -> offer(ModItems.NIBI, 1, Blocks.CORNFLOWER, 1, 3),
            () -> offer(Blocks.STONE, 64, ModItems.NIBI, 1, 6),
            () -> offer(Items.EMERALD, 2, ModItems.NIBI, 1, 6),
            () -> offer(ModItems.NIBI_BAG, 3, Items.EMERALD, 1, 3),
            () -> offer(ModItems.NIBI, 10, ModItems.SMALL_BACKPACK, 1, 2),
            () -> offer(Items.LEATHER, 5, ModItems.NIBI, 1, 6),
            () -> offer(Blocks.PUMPKIN, 1, ModItems.NIBI, 1, 6),
            () -> offer(Items.PUMPKIN_PIE, 1, ModItems.NIBI, 2, 6),
            () -> offer(Blocks.CARVED_PUMPKIN, 1, ModItems.NIBI, 2, 6),
            () -> offer(Blocks.JACK_O_LANTERN, 1, ModItems.NIBI, 2, 6),
            () -> offer(Items.DIAMOND_PICKAXE, 1, ModItems.NIBI, 6, 2),
            () -> offer(ModItems.LITTLE_PERSON_TOOL, 1, ModItems.NIBI, 1, 3),
            () -> offer(Items.WATER_BUCKET, 1, Items.GOLD_INGOT, 1, 3),
            () -> offer(ModItems.NIBI_BAG, 2, Items.DIAMOND, 1, 2)
    );
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.NEAREST_BED,
            SensorType.HURT_BY,
            ModSensorTypes.LITTLE_PERSON_HOSTILES,
            SensorType.VILLAGER_BABIES,
            SensorType.SECONDARY_POIS,
            SensorType.GOLEM_DETECTED
    );
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.HOME,
            MemoryModuleType.JOB_SITE,
            MemoryModuleType.POTENTIAL_JOB_SITE,
            MemoryModuleType.MEETING_POINT,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.DOORS_TO_CLOSE,
            MemoryModuleType.NEAREST_BED,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.NEAREST_HOSTILE,
            MemoryModuleType.SECONDARY_JOB_SITE,
            MemoryModuleType.HIDING_PLACE,
            MemoryModuleType.HEARD_BELL_TIME,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.LAST_SLEPT,
            MemoryModuleType.LAST_WOKEN,
            MemoryModuleType.LAST_WORKED_AT_POI,
            MemoryModuleType.GOLEM_DETECTED_RECENTLY
    );
    private long nextRestockGameTime;

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.setVillagerData(this.getVillagerData().withProfession(world.registryAccess(), VillagerProfession.NITWIT).withLevel(1));
        if (world instanceof ServerLevel) {
            this.getOffers();
            this.ensureRestockScheduled();
        }
        return data;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        // 强制只接受 NITWIT 职业
        super.setVillagerData(villagerData.withProfession(this.level().registryAccess(), VillagerProfession.NITWIT));
    }

    @Override
    protected Component getTypeName() {
        return this.getType().getDescription();
    }

    protected Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_MODULES, SENSORS, entity -> java.util.List.of());
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public LittlePersonCivilianEntity(EntityType<? extends Villager> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.tickCount % 20 == 0) this.heal(1.0F);
            if (this.tickCount % 200 == 0) this.restockLittlePersonTrades();
        }
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.offers;
        if (offers == null || !offers.isEmpty()) {
            return;
        }

        List<LittlePersonTradeFactory> pool = new ArrayList<>(TRADE_POOL);
        for (int i = 0; i < TRADE_COUNT && !pool.isEmpty(); i++) {
            offers.add(pool.remove(this.random.nextInt(pool.size())).create());
        }
        this.ensureRestockScheduled();
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putLong(NEXT_RESTOCK_TIME_KEY, this.nextRestockGameTime);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.nextRestockGameTime = input.getLongOr(NEXT_RESTOCK_TIME_KEY, 0L);
    }

    private void ensureRestockScheduled() {
        if (this.nextRestockGameTime <= 0L) {
            this.scheduleNextRestock(this.level().getGameTime());
        }
    }

    private void scheduleNextRestock(long fromGameTime) {
        this.nextRestockGameTime = fromGameTime + (4L + this.random.nextInt(3)) * DAY_TICKS;
    }

    private void restockLittlePersonTrades() {
        if (this.offers == null || this.offers.isEmpty()) {
            return;
        }
        long gameTime = this.level().getGameTime();
        this.ensureRestockScheduled();
        if (gameTime < this.nextRestockGameTime || this.offers.stream().noneMatch(MerchantOffer::needsRestock)) {
            return;
        }

        for (MerchantOffer offer : this.offers) {
            offer.updateDemand();
            offer.resetUses();
        }
        this.scheduleNextRestock(gameTime);
        Player tradingPlayer = this.getTradingPlayer();
        if (tradingPlayer != null) {
            tradingPlayer.sendMerchantOffers(
                    tradingPlayer.containerMenu.containerId,
                    this.offers,
                    this.getVillagerData().level(),
                    this.getVillagerXp(),
                    this.showProgressBar(),
                    this.canRestock()
            );
        }
    }

    private static MerchantOffer offer(ItemLike cost, int costCount, ItemLike result, int resultCount, int maxUses) {
        return offer(new ItemCost(cost, costCount), result, resultCount, maxUses);
    }

    private static MerchantOffer offer(ItemCost cost, ItemLike result, int resultCount, int maxUses) {
        return new MerchantOffer(cost, new ItemStack(result, resultCount), maxUses, 0, 0.0F);
    }

    private interface LittlePersonTradeFactory {
        MerchantOffer create();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 0, this::mainController));
    }

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private PlayState mainController(final AnimationTest<LittlePersonCivilianEntity> event) {
        return event.isMoving() ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(IDLE_ANIM);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    public static AttributeSupplier.Builder createLittlePersonCivilianAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes().add(Attributes.MOVEMENT_SPEED, 0.4).add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    public boolean canSkill() {
        return false;
    }
}
