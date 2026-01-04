package com.kltyton.mob_battle.mixin.sculkshrieker;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.Waterloggable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BlockWithEntity implements Waterloggable {
    protected SculkShriekerBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SculkShriekerBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private void init(SculkShriekerBlock instance, BlockState blockState) {
        this.setDefaultState(blockState
                .with(SculkShriekerBlock.SHRIEKING, false)
                .with(SculkShriekerBlock.WATERLOGGED, false)
                .with(SculkShriekerBlock.CAN_SUMMON, true));
    }
}
