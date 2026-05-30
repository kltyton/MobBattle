package com.kltyton.mob_battle.config.whitelist;

public final class ClientPermissionState {

    private static boolean whitelisted = false;
    public static boolean isWhitelisted() {
        return whitelisted;
    }

    public static void setWhitelisted(boolean value) {
        whitelisted = value;
    }
}

