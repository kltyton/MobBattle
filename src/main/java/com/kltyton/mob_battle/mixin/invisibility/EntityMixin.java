package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public abstract class EntityMixin {

    @Unique
    public void custom$setTrueInvisible(boolean invisible) {

    }

    @Unique
    public boolean custom$isTrueInvisible() {
        return false;
    }
}
