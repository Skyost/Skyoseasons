package com.skyost.seasons.util;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.skyost.seasons.api.Season;

public class PluginConfig extends Config {
	public PluginConfig(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n              Skyoseasons Configuration               #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/skyoseasons #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n                                                      #";
		CONFIG_HEADER += "\n             All times are in seconds !               #";
		CONFIG_HEADER += "\n##################################################### #";
	}
	
	public String Worlds = "WorldA,WorldB,WorldC";
	
	public String PluginFile;
	
	public Season CurrentSeason = Season.SPRING;
	
	public int SnowmeltMultiplicator = 5;
	public int TimeRefreshRate = 25;
	
	public boolean CheckForUpdates = true;
}
