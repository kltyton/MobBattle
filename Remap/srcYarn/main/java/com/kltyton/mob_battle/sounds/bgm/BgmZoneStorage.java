package com.kltyton.mob_battle.sounds.bgm;

import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Box;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BgmZoneStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "bgm_zones.json";
    private static Path getFilePath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve("mob_battle").resolve(FILE_NAME);
    }

    public static void save(MinecraftServer server, Collection<BgmZone> zones) {
        Path path = getFilePath(server);
        try {
            Files.createDirectories(path.getParent());
            List<JsonObject> list = new ArrayList<>();
            for (BgmZone zone : zones) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", zone.name());
                obj.addProperty("musicId", zone.musicId().toString());
                obj.addProperty("volume", zone.volume());
                JsonObject box = new JsonObject();
                box.addProperty("minX", zone.area().minX);
                box.addProperty("minY", zone.area().minY);
                box.addProperty("minZ", zone.area().minZ);
                box.addProperty("maxX", zone.area().maxX);
                box.addProperty("maxY", zone.area().maxY);
                box.addProperty("maxZ", zone.area().maxZ);
                obj.add("box", box);
                list.add(obj);
            }
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
                GSON.toJson(list, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<BgmZone> load(MinecraftServer server) {
        Path path = getFilePath(server);
        if (!Files.exists(path)) return Collections.emptyList();
        try (Reader reader = new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            List<BgmZone> result = new ArrayList<>();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                String name = obj.get("name").getAsString();
                Identifier id = Identifier.of(obj.get("musicId").getAsString());
                float volume = obj.get("volume").getAsFloat();
                JsonObject box = obj.get("box").getAsJsonObject();
                Box area = new Box(
                        box.get("minX").getAsDouble(),
                        box.get("minY").getAsDouble(),
                        box.get("minZ").getAsDouble(),
                        box.get("maxX").getAsDouble(),
                        box.get("maxY").getAsDouble(),
                        box.get("maxZ").getAsDouble()
                );
                result.add(new BgmZone(name, area, id, volume));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
