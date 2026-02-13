package com.kltyton.mob_battle.mixin.keybind;

import com.kltyton.mob_battle.mixin.accessor.keybind.BoundKeyAccessor;
import com.kltyton.mob_battle.utils.keybind.KeybindFixer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = KeyBinding.class, priority = 10000)
public abstract class KeybindingMixin {

    @Final @Shadow private static Map<String, KeyBinding> KEYS_BY_ID;
    @Final @Shadow private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
    @Shadow private InputUtil.Key boundKey;

    @Inject(method = "onKeyPressed", at = @At("TAIL"))
    private static void onKeyPressedFixed(InputUtil.Key key, CallbackInfo ci, @Local KeyBinding original) {
        KeybindFixer.INSTANCE.onKeyPressed(key, original, KEY_TO_BINDINGS.get(key));
    }

    @Inject(method = "setKeyPressed", at = @At("TAIL"))
    private static void setKeyPressedFixed(InputUtil.Key key, boolean pressed, CallbackInfo ci, @Local KeyBinding original) {
        KeybindFixer.INSTANCE.setKeyPressed(key, pressed, original, KEY_TO_BINDINGS.get(key));
    }

    @Inject(method = "updateKeysByCode", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void updateByCodeToMultiMap(CallbackInfo ci) {
        KeybindFixer.INSTANCE.clearMap();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            KeybindFixer.INSTANCE.putKey(((BoundKeyAccessor) keyBinding).getBoundKey(), keyBinding);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("TAIL"))
    private void putToMultiMap(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        // 在构造函数结束时，把新创建的按键加入到我们的多重映射中
        KeybindFixer.INSTANCE.putKey(this.boundKey, (KeyBinding) (Object) this);
    }
}
