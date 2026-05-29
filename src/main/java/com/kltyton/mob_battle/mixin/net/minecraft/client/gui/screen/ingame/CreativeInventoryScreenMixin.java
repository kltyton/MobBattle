package com.kltyton.mob_battle.mixin.net.minecraft.client.gui.screen.ingame;

import com.kltyton.mob_battle.config.whitelist.ClientItemFilter;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements FabricCreativeInventoryScreen {


    @Shadow
    private float scrollOffs;

    public CreativeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Unique
    private void mob_battle$filterDisplayedStacks() {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        for (int i = this.menu.items.size() - 1; i >= 0; i--) {
            ItemStack stack = this.menu.items.get(i);
            if (ClientItemFilter.shouldHide(stack)) {
                this.menu.items.remove(i);
            }
        }
    }

    @Unique
    private void mob_battle$refreshAfterFilter() {
        if (this.scrollOffs < 0.0F) {
            this.scrollOffs = 0.0F;
        }
        if (this.scrollOffs > 1.0F) {
            this.scrollOffs = 1.0F;
        }
        this.menu.scrollTo(this.scrollOffs);
    }

    /**
     * 1. 搜索结果过滤
     * 覆盖：
     * - 搜索框输入搜索
     * - 搜索框为空时的 search tab 默认显示内容
     */
    @Inject(method = "refreshSearchResults", at = @At("TAIL"))
    private void mob_battle$filterSearchResults(CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }

    /**
     * 2. 普通创造标签页过滤
     * updateDisplayParameters() 里会调用 refreshSelectedTab(...)
     * 所以这个注入可以覆盖“已选标签页重新刷新”的情况
     */
    @Inject(method = "refreshCurrentTabContents", at = @At("TAIL"))
    private void mob_battle$filterRefreshSelectedTab(Collection<ItemStack> displayStacks, CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }

    /**
     * 3. 切换标签页时再兜底过滤一次
     * 覆盖 CATEGORY / HOTBAR / SEARCH / 其他切页逻辑
     */
    @Inject(method = "selectTab", at = @At("TAIL"))
    private void mob_battle$filterSetSelectedTab(CreativeModeTab group, CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }
}


