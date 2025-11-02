package com.kltyton.mob_battle.items.tool.snipe;

import net.minecraft.item.CrossbowItem;

public class VsSnipe extends CrossbowItem {
/*    private static final RawAnimation RELOAD_ANIM = RawAnimation.begin().thenPlayAndHold("reload");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);*/

    public VsSnipe(Settings settings) {
        super(settings);
        //注册我们的项目为服务器端处理。
        //同时启用动画数据同步和服务器端动画触发
/*        GeoItem.registerSyncedAnimatable(this);*/
    }

    // Let's handle our use method so that we activate the animation when right-clicking while holding the box
    //triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "Activation", "activate");

/*    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Main", 0, animTest -> PlayState.STOP)
                .triggerableAnim("reload", RELOAD_ANIM));
    }*/

/*    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private VsSnipeRender renderer;

            @Override
            public VsSnipeRender getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new VsSnipeRender();

                return this.renderer;
            }
        });
    }*/
}
