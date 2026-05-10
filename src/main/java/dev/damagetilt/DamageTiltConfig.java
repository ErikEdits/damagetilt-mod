package dev.damagetilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DamageTiltConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("damagetilt.json");

    private static ConfigData data = new ConfigData();

    public static class ConfigData {
        public boolean enabled = true;
        public int keyCode = GLFW.GLFW_KEY_K;
        public boolean firstLaunch = true;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                data = GSON.fromJson(json, ConfigData.class);
                if (data == null) data = new ConfigData();
            } catch (IOException e) {
                DamageTiltMod.LOGGER.error("Fehler beim Laden der Config: " + e.getMessage());
                data = new ConfigData();
            }
        } else {
            data = new ConfigData();
            save();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            DamageTiltMod.LOGGER.error("Fehler beim Speichern der Config: " + e.getMessage());
        }
    }

    public static boolean isEnabled() { return data.enabled; }
    public static void setEnabled(boolean v) { data.enabled = v; }
    public static int getKeyCode() { return data.keyCode; }
    public static void setKeyCode(int code) { data.keyCode = code; }
    public static boolean isFirstLaunch() { return data.firstLaunch; }
    public static void setFirstLaunch(boolean v) { data.firstLaunch = v; }
}
