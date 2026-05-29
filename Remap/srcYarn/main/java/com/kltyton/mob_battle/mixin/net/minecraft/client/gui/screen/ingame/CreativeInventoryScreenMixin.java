package com.kltyton.mob_battle.mixin.net.minecraft.client.gui.screen.ingame;

import com.kltyton.mob_battle.config.whitelist.ClientItemFilter;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> implements FabricCreativeInventoryScreen {


    @Shadow
    private float scrollPosition;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Unique
    private void mob_battle$filterDisplayedStacks() {
        if (ClientPermissionState.isWhitelisted()) {
            return;
        }

        for (int i = this.handler.itemList.size() - 1; i >= 0; i--) {
            ItemStack stack = this.handler.itemList.get(i);
            if (ClientItemFilter.shouldHide(stack)) {
                this.handler.itemList.remove(i);
            }
        }
    }

    @Unique
    private void mob_battle$refreshAfterFilter() {
        if (this.scrollPosition < 0.0F) {
            this.scrollPosition = 0.0F;
        }
        if (this.scrollPosition > 1.0F) {
            this.scrollPosition = 1.0F;
        }
        this.handler.scrollItems(this.scrollPosition);
    }

    /**
     * 1. 搜索结果过滤
     * 覆盖：
     * - 搜索框输入搜索
     * - 搜索框为空时的 search tab 默认显示内容
     */
    @Inject(method = "search", at = @At("TAIL"))
    private void mob_battle$filterSearchResults(CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }

    /**
     * 2. 普通创造标签页过滤
     * updateDisplayParameters() 里会调用 refreshSelectedTab(...)
     * 所以这个注入可以覆盖“已选标签页重新刷新”的情况
     */
    @Inject(method = "refreshSelectedTab", at = @At("TAIL"))
    private void mob_battle$filterRefreshSelectedTab(Collection<ItemStack> displayStacks, CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }

    /**
     * 3. 切换标签页时再兜底过滤一次
     * 覆盖 CATEGORY / HOTBAR / SEARCH / 其他切页逻辑
     */
    @Inject(method = "setSelectedTab", at = @At("TAIL"))
    private void mob_battle$filterSetSelectedTab(ItemGroup group, CallbackInfo ci) {
        this.mob_battle$filterDisplayedStacks();
        this.mob_battle$refreshAfterFilter();
    }
}


