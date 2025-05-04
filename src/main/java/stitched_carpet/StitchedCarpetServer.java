package stitched_carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StitchedCarpetServer implements CarpetExtension, ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(StitchedCarpetServer.class);
    public static final String MOD_ID = "stitch";
    public static final String FANCY_NAME = "StitchedCarpet";
    public static final String VERSION = "0.1.0";
    public static SettingsManager settingsManager;

    @Override
    public void onInitialize() {
        settingsManager = new SettingsManager(VERSION, MOD_ID, FANCY_NAME);
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onGameStarted() {
        settingsManager.parseSettingsClass(StitchedCarpetSettings.class);
        LOGGER.info("Stitched Carpet loaded!");
    }

    @Override
    public SettingsManager customSettingsManager() {
        // this will ensure that our settings are loaded properly when world loads
        return settingsManager;
    }
}
