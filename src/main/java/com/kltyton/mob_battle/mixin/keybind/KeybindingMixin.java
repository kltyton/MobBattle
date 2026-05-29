package com.kltyton.mob_battle.mixin.keybind;

import com.kltyton.mob_battle.mixin.accessor.keybind.BoundKeyAccessor;
import com.kltyton.mob_battle.utils.keybind.KeybindFixer;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import net.minecraft.client.KeyMapping;

@Mixin(value = KeyMapping.class, priority = 10000)
public abstract class KeybindingMixin {

    @Final @Shadow private static Map<String, KeyMapping> ALL;
    @Final @Shadow private static Map<InputConstants.Key, KeyMapping> MAP;
    @Shadow private InputConstants.Key key;

    @Inject(method = "click", at = @At("TAIL"))
    private static void onKeyPressedFixed(InputConstants.Key key, CallbackInfo ci, @Local KeyMapping original) {
        KeybindFixer.INSTANCE.onKeyPressed(key, original, MAP.get(key));
    }

    @Inject(method = "set", at = @At("TAIL"))
    private static void setKeyPressedFixed(InputConstants.Key key, boolean pressed, CallbackInfo ci, @Local KeyMapping original) {
        KeybindFixer.INSTANCE.setKeyPressed(key, pressed, original, MAP.get(key));
    }

    @Inject(method = "resetMapping", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void updateByCodeToMultiMap(CallbackInfo ci) {
        KeybindFixer.INSTANCE.clearMap();
        for (KeyMapping keyBinding : ALL.values()) {
            KeybindFixer.INSTANCE.putKey(((BoundKeyAccessor) keyBinding).getBoundKey(), keyBinding);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At("TAIL"))
    private void putToMultiMap(String translationKey, InputConstants.Type type, int code, String category, CallbackInfo ci) {
        // 在构造函数结束时，把新创建的按键加入到我们的多重映射中
        KeybindFixer.INSTANCE.putKey(this.key, (KeyMapping) (Object) this);
    }
}
