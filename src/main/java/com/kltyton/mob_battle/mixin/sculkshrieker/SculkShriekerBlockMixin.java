package com.kltyton.mob_battle.mixin.sculkshrieker;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected SculkShriekerBlockMixin(Properties settings) {
        super(settings);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SculkShriekerBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void init(SculkShriekerBlock instance, BlockState blockState) {
        this.registerDefaultState(blockState
                .setValue(SculkShriekerBlock.SHRIEKING, false)
                .setValue(SculkShriekerBlock.WATERLOGGED, false)
                .setValue(SculkShriekerBlock.CAN_SUMMON, true));
    }
}
