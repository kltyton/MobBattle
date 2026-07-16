package com.kltyton.mob_battle.mixin.raid;

import com.kltyton.mob_battle.utils.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Unique private static final int LEVEL_SIX_TOTAL_WAVES = 9;
    @Unique private static final int LEVEL_SIX_VANILLA_BONUS_WAVE = 8;
    @Unique private static final int LEVEL_SIX_CUSTOM_WAVE = 9;

    @Shadow private int raidOmenLevel;
    @Shadow private int groupsSpawned;
    @Shadow private int raidCooldownTicks;
    @Shadow private float totalHealth;
    @Shadow private Optional<BlockPos> waveSpawnPos;
    @Shadow private Set<UUID> heroesOfTheVillage;
    @Shadow @Final private RandomSource random;

    @Shadow public abstract void joinRaid(ServerLevel world, int wave, Raider raider, @Nullable BlockPos pos, boolean existing);
    @Shadow public abstract void updateBossbar();
    @Shadow public abstract boolean isVictory();
    @Shadow public abstract int getTotalRaidersAlive();
    @Shadow public abstract void setLeader(int wave, Raider raider);
    @Shadow private void setDirty(ServerLevel world) {
    }

    @Unique private boolean mobBattle$appliedLevelSixHero;

    @Inject(method = "getMaxRaidOmenLevel", at = @At("HEAD"), cancellable = true)
    private void allowLevelSixRaidOmen(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(6);
    }

    @Inject(method = "hasMoreWaves", at = @At("HEAD"), cancellable = true)
    private void addLevelSixNinthWave(CallbackInfoReturnable<Boolean> cir) {
        if (this.raidOmenLevel >= 6) {
            cir.setReturnValue(this.groupsSpawned < LEVEL_SIX_TOTAL_WAVES);
        }
    }

    @Inject(method = "shouldSpawnGroup", at = @At("HEAD"), cancellable = true)
    private void allowLevelSixExtendedWaveSpawn(CallbackInfoReturnable<Boolean> cir) {
        if (this.raidOmenLevel >= 6) {
            cir.setReturnValue(
                    this.raidCooldownTicks == 0
                            && this.groupsSpawned < LEVEL_SIX_TOTAL_WAVES
                            && this.getTotalRaidersAlive() == 0
            );
        }
    }

    @Inject(method = "spawnGroup", at = @At("HEAD"), cancellable = true)
    private void spawnLevelSixExtendedWaves(ServerLevel world, BlockPos pos, CallbackInfo ci) {
        if (this.raidOmenLevel < 6) {
            return;
        }

        int wave = this.groupsSpawned + 1;
        if (wave == LEVEL_SIX_VANILLA_BONUS_WAVE) {
            spawnLevelSixVanillaBonusWave(world, wave, pos);
            finishLevelSixCustomWave(world);
            ci.cancel();
            return;
        }

        if (wave != LEVEL_SIX_CUSTOM_WAVE) {
            return;
        }

        this.totalHealth = 0.0F;
        boolean leaderSet = false;
        leaderSet = spawnRaiders(world, wave, pos, EntityType.PILLAGER, 50, leaderSet);
        leaderSet = spawnRaiders(world, wave, pos, EntityType.VINDICATOR, 50, leaderSet);
        leaderSet = spawnRaiders(world, wave, pos, EntityType.WITCH, 15, leaderSet);
        leaderSet = spawnRaiders(world, wave, pos, EntityType.EVOKER, 3, leaderSet);
        spawnRaiders(world, wave, pos, EntityType.ILLUSIONER, 10, leaderSet);
        spawnRavagersWithEvokerPassengers(world, wave, pos, 8);

        finishLevelSixCustomWave(world);
        ci.cancel();
    }

    @Inject(method = "joinRaid", at = @At("TAIL"))
    private void configureLevelSixGeneratedRaider(ServerLevel world, int wave, Raider raider, @Nullable BlockPos pos, boolean existing, CallbackInfo ci) {
        if (this.raidOmenLevel >= 6 && !existing) {
            configureLevelSixRaider(world, raider);
        }
    }

    @Unique
    private void spawnLevelSixVanillaBonusWave(ServerLevel world, int wave, BlockPos pos) {
        this.totalHealth = 0.0F;
        DifficultyInstance difficulty = world.getCurrentDifficultyAt(pos);
        boolean leaderSet = false;
        leaderSet = spawnRaiders(world, wave, pos, EntityType.VINDICATOR, 5 + getPillagerOrVindicatorBonus(difficulty), leaderSet);
        leaderSet = spawnRaiders(world, wave, pos, EntityType.EVOKER, 2, leaderSet);
        leaderSet = spawnRaiders(world, wave, pos, EntityType.PILLAGER, 2 + getPillagerOrVindicatorBonus(difficulty), leaderSet);
        spawnRaiders(world, wave, pos, EntityType.WITCH, 1 + getWitchBonus(difficulty), leaderSet);
        spawnRavagersWithVanillaPassengers(world, wave, pos, 2 + getRavagerBonus(difficulty));
    }

    @Unique
    private void finishLevelSixCustomWave(ServerLevel world) {
        this.waveSpawnPos = Optional.empty();
        this.groupsSpawned++;
        this.updateBossbar();
        this.setDirty(world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void extendLevelSixHeroReward(ServerLevel world, CallbackInfo ci) {
        if (this.mobBattle$appliedLevelSixHero || this.raidOmenLevel < 6 || !this.isVictory()) {
            return;
        }
        this.mobBattle$appliedLevelSixHero = true;
        for (UUID uuid : this.heroesOfTheVillage) {
            Entity entity = world.getEntity(uuid);
            if (entity instanceof LivingEntity living && !entity.isSpectator()) {
                living.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 80 * 60 * 20, 5, false, false, true));
            }
        }
    }

    @Unique
    private boolean spawnRaiders(ServerLevel world, int wave, BlockPos pos, EntityType<? extends Raider> type, int count, boolean leaderSet) {
        for (int i = 0; i < count; i++) {
            Raider raider = type.create(world, EntitySpawnReason.EVENT);
            if (raider == null) {
                continue;
            }
            if (!leaderSet && raider.canBeLeader()) {
                raider.setPatrolLeader(true);
                this.setLeader(wave, raider);
                leaderSet = true;
            }
            this.joinRaid(world, wave, raider, pos, false);
        }
        return leaderSet;
    }

    @Unique
    private void spawnRavagersWithEvokerPassengers(ServerLevel world, int wave, BlockPos pos, int count) {
        for (int i = 0; i < count; i++) {
            Raider ravager = EntityType.RAVAGER.create(world, EntitySpawnReason.EVENT);
            Raider evoker = EntityType.EVOKER.create(world, EntitySpawnReason.EVENT);
            if (ravager == null) {
                continue;
            }
            this.joinRaid(world, wave, ravager, pos, false);

            if (evoker != null) {
                this.joinRaid(world, wave, evoker, pos, false);
                evoker.startRiding(ravager);
            }
        }
    }

    @Unique
    private void spawnRavagersWithVanillaPassengers(ServerLevel world, int wave, BlockPos pos, int count) {
        for (int i = 0; i < count; i++) {
            Raider ravager = EntityType.RAVAGER.create(world, EntitySpawnReason.EVENT);
            if (ravager == null) {
                continue;
            }
            this.joinRaid(world, wave, ravager, pos, false);

            Raider rider = i == 0
                    ? EntityType.EVOKER.create(world, EntitySpawnReason.EVENT)
                    : EntityType.VINDICATOR.create(world, EntitySpawnReason.EVENT);
            if (rider != null) {
                this.joinRaid(world, wave, rider, pos, false);
                rider.snapTo(pos, 0.0F, 0.0F);
                rider.startRiding(ravager, false, false);
            }
        }
    }

    @Unique
    private int getPillagerOrVindicatorBonus(DifficultyInstance difficultyInstance) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        int bonusSpawns;
        if (difficulty == Difficulty.EASY) {
            bonusSpawns = this.random.nextInt(2);
        } else if (difficulty == Difficulty.NORMAL) {
            bonusSpawns = 1;
        } else if (difficulty == Difficulty.HARD) {
            bonusSpawns = 2;
        } else {
            return 0;
        }
        return bonusSpawns > 0 ? this.random.nextInt(bonusSpawns + 1) : 0;
    }

    @Unique
    private int getWitchBonus(DifficultyInstance difficultyInstance) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        return difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD ? this.random.nextInt(2) : 0;
    }

    @Unique
    private int getRavagerBonus(DifficultyInstance difficultyInstance) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        return difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD ? this.random.nextInt(2) : 0;
    }

    @Unique
    private void configureLevelSixRaider(ServerLevel world, Raider raider) {
        if (raider.getAttribute(Attributes.ARMOR) != null) {
            raider.getAttribute(Attributes.ARMOR).setBaseValue(20.0D);
        }
        if (raider.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
            raider.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(12.0D);
        }

        ItemStack weapon = ItemStack.EMPTY;
        EntityType<?> type = raider.getType();
        if (type == EntityType.PILLAGER) {
            weapon = enchantedCrossbow(world);
        } else if (type == EntityType.VINDICATOR) {
            weapon = enchantedAxe(world);
        } else if (type == EntityType.ILLUSIONER) {
            weapon = enchantedBow(world);
        }
        if (!weapon.isEmpty()) {
            raider.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            raider.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
    }

    @Unique
    private ItemStack enchantedCrossbow(ServerLevel world) {
        ItemStack stack = new ItemStack(Items.CROSSBOW);
        EnchantmentUtil.addEnchantment(world, stack, Enchantments.QUICK_CHARGE, 3);
        EnchantmentUtil.addEnchantment(world, stack, Enchantments.POWER, 10);
        return stack;
    }

    @Unique
    private ItemStack enchantedAxe(ServerLevel world) {
        ItemStack stack = new ItemStack(Items.IRON_AXE);
        EnchantmentUtil.addEnchantment(world, stack, Enchantments.SHARPNESS, 10);
        return stack;
    }

    @Unique
    private ItemStack enchantedBow(ServerLevel world) {
        ItemStack stack = new ItemStack(Items.BOW);
        EnchantmentUtil.addEnchantment(world, stack, Enchantments.POWER, 15);
        return stack;
    }
}
