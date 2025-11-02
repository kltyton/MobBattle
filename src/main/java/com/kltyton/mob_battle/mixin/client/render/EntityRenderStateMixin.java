package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.accessor.ILeadRenderData;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.LeashData.class)
@Implements(@Interface(iface = ILeadRenderData.class, prefix = "custom$"))
public abstract class EntityRenderStateMixin {
    @Unique
    private boolean shouldRender = true;
    @Unique
    public boolean custom$shouldRender() {
        return this.shouldRender;
    }

    @Unique
    public void custom$setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }
}
