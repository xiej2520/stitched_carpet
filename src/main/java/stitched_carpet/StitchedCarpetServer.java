package stitched_carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import carpet.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StitchedCarpetServer implements CarpetExtension, ModInitializer {
    public static SettingsManager settingsManager;
    public static final Logger LOGGER = LogManager.getLogger(StitchedCarpetServer.class);
    public static final String MOD_ID = "stitched_carpet";
    public static final String FANCY_NAME = "StitchedCarpet";
    public static final String VERSION = "0.1";

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new StitchedCarpetServer());
    }

    @Override
    public void onGameStarted() {
        // create custom settingsManager
        settingsManager = new SettingsManager(VERSION, MOD_ID, FANCY_NAME);
        settingsManager.parseSettingsClass(StitchedCarpetSettings.class);

        CarpetServer.settingsManager.parseSettingsClass(StitchedCarpetSettings.class);

        // workaround for rule being overwritten: https://github.com/gnembon/fabric-carpet/issues/802
        CarpetServer.settingsManager.addRuleObserver((source, rule, value) -> {
            ParsedRule<?> stitchedCarpetRule = settingsManager.getRule(rule.name);
            ParsedRule<?> carpetRule = CarpetServer.settingsManager.getRule(rule.name);

            // check if the rule being changed exists in minitweaks, but isn't the same rule as the one in carpet's settingsManager
            // if so, update the rule (if types are the same)
            if(stitchedCarpetRule != null && carpetRule != null && stitchedCarpetRule != carpetRule && stitchedCarpetRule.type == carpetRule.type) {
                stitchedCarpetRule.set(source, value);
            }
        });
    }
}
