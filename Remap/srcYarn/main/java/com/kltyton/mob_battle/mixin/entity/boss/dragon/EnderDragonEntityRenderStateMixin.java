package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.entity.boss.dragon.IEnderDragonEntityRenderStateAccessor;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnderDragonEntityRenderState.class)
@Implements(@Interface(iface = IEnderDragonEntityRenderStateAccessor.class, prefix = "custom$"))
public class EnderDragonEntityRenderStateMixin {
    @Unique
    private boolean isShadow = false;
    @Unique
    public boolean custom$isShadow() {
        return isShadow;
    }
    @Unique
    public void custom$setShadow(boolean shadow) {
        isShadow = shadow;
    }
}
