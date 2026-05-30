package com.kltyton.mob_battle.accessor;

public interface IModEntityRenderState {
    void setTrueInvisible(boolean invisible);
    boolean isTrueInvisible();
    void setIceAmplifier(int amplifier);
    int getIceAmplifier();
    void setCompressedArmorMarkerType(int markerType);
    int getCompressedArmorMarkerType();
    void setPigSpiritMarkAmplifier(int amplifier);
    int getPigSpiritMarkAmplifier();
    void setHealthBarVisible(boolean visible);
    boolean isHealthBarVisible();
    void setHealthBarHealth(float health);
    float getHealthBarHealth();
    void setHealthBarMaxHealth(float maxHealth);
    float getHealthBarMaxHealth();
}
