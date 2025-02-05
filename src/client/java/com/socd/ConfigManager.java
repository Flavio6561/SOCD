package com.socd;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    private static Config config;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("SOCD.json");

    private static class Config {
        boolean toggleMod;
        boolean toggleMovement;
        boolean toggleStrafe;
        boolean movementContinueAfterRelease;
        boolean strafeContinueAfterRelease;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File configFile = configPath.toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, Config.class);
                if (config == null)
                    restoreDefaultConfig();
            } catch (JsonSyntaxException | IOException exception) {
                restoreDefaultConfig();
            }
        } else
            restoreDefaultConfig();
        applyConfig();
    }

    private static void restoreDefaultConfig() {
        config = new Config();
        config.toggleMod = SOCDClient.toggleMod;
        config.toggleMovement = SOCDClient.toggleMovement;
        config.toggleStrafe = SOCDClient.toggleStrafe;
        config.movementContinueAfterRelease = SOCDClient.movementContinueAfterRelease;
        config.strafeContinueAfterRelease = SOCDClient.strafeContinueAfterRelease;

        saveConfig();
    }

    private static void applyConfig() {
        SOCDClient.toggleMod = config.toggleMod;
        SOCDClient.toggleMovement = config.toggleMovement;
        SOCDClient.toggleStrafe = config.toggleStrafe;
        SOCDClient.movementContinueAfterRelease = config.movementContinueAfterRelease;
        SOCDClient.strafeContinueAfterRelease = config.strafeContinueAfterRelease;

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File configFile = configPath.toFile();
        Config currentConfig = new Config();

        currentConfig.toggleMod = SOCDClient.toggleMod;
        currentConfig.toggleMovement = SOCDClient.toggleMovement;
        currentConfig.toggleStrafe = SOCDClient.toggleStrafe;
        currentConfig.movementContinueAfterRelease = SOCDClient.movementContinueAfterRelease;
        currentConfig.strafeContinueAfterRelease = SOCDClient.strafeContinueAfterRelease;

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(currentConfig, writer);
        } catch (IOException exception) {
            restoreDefaultConfig();
        }
    }
}