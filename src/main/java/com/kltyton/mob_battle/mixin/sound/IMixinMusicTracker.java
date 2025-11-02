package com.kltyton.mob_battle.mixin.sound;

import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicTracker.class)
public interface IMixinMusicTracker {
    @Accessor("current")
    SoundInstance getCurrent();
    @Accessor("volume")
    void setVolume(float volume);
}
