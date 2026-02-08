package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public class EntityRenderStateMixin {
    @Unique
    private boolean trueInvisible;
    @Unique
    public boolean custom$isTrueInvisible() {
        return trueInvisible;
    }
    @Unique
    public void custom$setTrueInvisible(boolean invisible) {
        this.trueInvisible = invisible;
    }
}
