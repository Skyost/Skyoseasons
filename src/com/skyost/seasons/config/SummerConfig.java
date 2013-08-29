package com.skyost.seasons.config;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.skyost.seasons.util.Config;

public class SummerConfig extends Config {
	public SummerConfig(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "summer.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n              Summer Configuration                    #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/skyoseasons #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n                                                      #";
		CONFIG_HEADER += "\n             All times are in seconds !               #";
		CONFIG_HEADER += "\n##################################################### #";
	}
	
	public int SeasonLength = 108000;
	
	public int DayLength = 600;
	public int NightLength = 600;
	
	public String Message = "�eIt is summer, enjoy the sunshine !";
}
