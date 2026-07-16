package com.kltyton.mob_battle.entity.golem;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.IronGolem;

public class ChestGolemRenderer extends IronGolemRenderer {
    private static final Identifier WITH_VINES = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID,
            "textures/entity/golem/chest_golem.png"
    );
    private static final Identifier NO_VINES = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID,
            "textures/entity/golem/chest_golem_no_vines.png"
    );

    public ChestGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public IronGolemRenderState createRenderState() {
        return new ChestGolemRenderState();
    }

    @Override
    public void extractRenderState(IronGolem entity, IronGolemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof ChestGolemRenderState chestState && entity instanceof ChestGolemEntity chestGolem) {
            chestState.hasVines = chestGolem.hasVines();
        }
    }

    @Override
    public Identifier getTextureLocation(IronGolemRenderState state) {
        return state instanceof ChestGolemRenderState chestState && !chestState.hasVines ? NO_VINES : WITH_VINES;
    }

    public static class ChestGolemRenderState extends IronGolemRenderState {
        public boolean hasVines = true;
    }
}
