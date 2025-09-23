package com.kltyton.mob_battle.entity.highbird.egg;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.HighbirdAndEggEntity;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class HighbirdEggEntity extends HighbirdAndEggEntity implements HighbirdSBEntity {
    // 动画定义
    protected static final RawAnimation IDLE_ANIM_HOT = RawAnimation.begin().thenLoop("idle_hot");
    protected static final RawAnimation IDLE_ANIM_COLD = RawAnimation.begin().thenLoop("idle_cold");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    // 在HighbirdEggEntity类中添加
    private static final TrackedData<String> STATUS_DATA = DataTracker.registerData(HighbirdEggEntity.class, TrackedDataHandlerRegistry.STRING);
    // 添加状态数据票证
    public static final DataTicket<String> STATUS_TICKET = DataTicket.create("highbird_egg_status", String.class);
    public boolean isIncubating = false; // 是否开始孵化
    private static final int MAX_INCUBATION_TIME = 	72000; // 孵化所需时间（72000刻）
    // 状态常量
    public static final String NORMAL_STATUS = "Normal";
    public static final String HOT_STATUS = "Hot";
    public static final String COLD_STATUS = "Cold";

    // 状态变量
    protected String status = NORMAL_STATUS;
    protected int statusDuration = 0; // 当前状态持续时间（刻）
    private int checkCooldown = 0;    // 状态检查冷却

    // 配置常量
    private static final int CHECK_INTERVAL = 20; // 每20刻（1秒）检查一次
    private static final int MAX_STATUS_DURATION = 600; // 30秒（600刻）
    private static final int RADIUS = 5; // 检测半径

    // 方块检测谓词
    private static final Predicate<BlockState> ICE_PREDICATE = state ->
            state.isOf(Blocks.ICE) || state.isOf(Blocks.BLUE_ICE) ||
                    state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.FROSTED_ICE);

    private static final Predicate<BlockState> HEAT_SOURCE_PREDICATE = state ->
            state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE) ||
                    state.isOf(Blocks.LAVA) || state.isOf(Blocks.CAMPFIRE) ||
                    state.isOf(Blocks.SOUL_CAMPFIRE) || state.isOf(Blocks.MAGMA_BLOCK);

    public HighbirdEggEntity(EntityType<? extends HighbirdEggEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(true);
    }
    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0D);
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
    }
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            // 只有在孵化状态下才进行状态检查
            if (isIncubating) {
                if (checkCooldown <= 0) {
                    updateTemperatureStatus();
                    checkCooldown = CHECK_INTERVAL;
                } else {
                    checkCooldown--;
                }
                handleStatusDuration();
            }

            // 处理孵化进度
            if (this.startGrowth()) {
                if (growthValue >= MAX_INCUBATION_TIME) {
                    levelUp();
                }
            }
        }
    }
    @Override
    protected void levelUp() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            HighbirdBabyEntity highbird = ModEntities.HIGHBIRD_BABY.create(serverWorld, SpawnReason.CONVERSION);
            if (highbird != null) {
                if (this.isTamed()) {
                    highbird.setOwner(this.getOwnerReference());
                    highbird.setOwner(this.getOwner());
                    if (this.getOwner() instanceof PlayerEntity player) highbird.setTamedBy(player);
                    highbird.setTamed(true, true);
                    highbird.setPosition(this.getPos());
                }
                serverWorld.spawnEntity(highbird);
                this.discard();
            }
        }
    }

    /**
     * 更新蛋的温度状态
     */
    private void updateTemperatureStatus() {
        BlockPos center = this.getBlockPos();
        World world = this.getWorld();

        boolean hasIce = false;
        boolean hasHeatSource = false;

        // 在5x5x5立方体内检测方块
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (ICE_PREDICATE.test(state)) {
                        hasIce = true;
                    }
                    if (HEAT_SOURCE_PREDICATE.test(state)) {
                        hasHeatSource = true;
                    }
                }
            }
        }

        // 根据昼夜更新状态
        if (isDay()) {
            setStatus((hasIce && !hasHeatSource) ? NORMAL_STATUS : HOT_STATUS);
        } else {
            setStatus((!hasIce && hasHeatSource) ? NORMAL_STATUS : COLD_STATUS);
        }
    }

    /**
     * 处理状态持续时间及死亡逻辑
     */
    private void handleStatusDuration() {
        if (status.equals(HOT_STATUS) || status.equals(COLD_STATUS)) {
            statusDuration++;
            if (statusDuration >= MAX_STATUS_DURATION) {
                this.kill((ServerWorld) this.getWorld()); // 超过30秒后死亡
            }
        } else {
            statusDuration = 0; // 重置正常状态的持续时间
        }
    }

    // ========== NBT 持久化 ==========
    private static final String STATUS_KEY = "Status";
    private static final String STATUS_DURATION_KEY = "StatusDuration";
    private static final String INCUBATING_KEY = "Incubating";

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        setStatus(view.getString(STATUS_KEY, NORMAL_STATUS));
        statusDuration = view.getInt(STATUS_DURATION_KEY, 0);
        isIncubating = view.getBoolean(INCUBATING_KEY, false);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString(STATUS_KEY, status);
        view.putInt(STATUS_DURATION_KEY, statusDuration);
        view.putBoolean(INCUBATING_KEY, isIncubating);
    }
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    @Override
    protected boolean startGrowth() {
        return isIncubating && status.equals(NORMAL_STATUS);
    }
    // ========== 动画控制器 ==========
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
    }

    private PlayState mainController(final AnimationTest<HighbirdBaseEntity> event) {
        // 这里可以根据状态返回不同的动画
        if (this.getStatus().equals(HOT_STATUS)) return event.setAndContinue(IDLE_ANIM_HOT);
        if (this.getStatus().equals(COLD_STATUS)) return event.setAndContinue(IDLE_ANIM_COLD);
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    // ========== Getter ==========
    public String getStatus() {
        return status;
    }

    public int getStatusDuration() {
        return statusDuration;
    }
    // 添加状态获取方法（用于渲染器）
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STATUS_DATA, NORMAL_STATUS);
    }

    // 更新状态时同步到客户端
    private void setStatus(String status) {
        this.status = status;
        if (!this.getWorld().isClient) {
            this.dataTracker.set(STATUS_DATA, status);
        }
    }

    // 客户端获取状态的方法
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (STATUS_DATA.equals(data)) {
            this.status = dataTracker.get(STATUS_DATA);
        }
    }
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!isIncubating && !player.isSneaking()) {
/*            isIncubating = true;
            this.setOwner(player);
            this.setTamedBy(player);*/
            this.dropItem(new ItemStack(ModItems.INCUBATION_EGG), false, false);
            this.discard();
            // 可以添加音效或粒子效果
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }
}
