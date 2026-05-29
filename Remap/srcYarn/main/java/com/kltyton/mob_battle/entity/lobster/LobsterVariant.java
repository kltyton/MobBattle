package com.kltyton.mob_battle.entity.lobster;

public enum LobsterVariant {
    RED(0, "red"),
    BLUE(1, "blue"),
    GRAY(2, "gray"),
    WHITE(3, "white"),
    GOLD(4, "gold");

    private final int id;
    private final String name;

    LobsterVariant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static LobsterVariant byId(int id) {
        for (LobsterVariant variant : values()) {
            if (variant.id == id) return variant;
        }
        return RED;
    }
}
