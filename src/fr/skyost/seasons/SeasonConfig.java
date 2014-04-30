package fr.skyost.seasons;

import java.io.File;
import java.util.HashMap;

import org.bukkit.block.Biome;

import fr.skyost.seasons.utils.Config;

public class SeasonConfig extends Config {
	
	public String Name;
	public String Next;
	
	public Biome DefaultBiome;
	public HashMap<String, String> Replacements = new HashMap<String, String>();
	
	public int Day_Length;
	public boolean Day_Message_Enable = true;
	public String Day_Message_Message;
	public int Night_Length;
	public boolean Night_Message_Enable = true;
	public String Night_Message_Message;
	
	public boolean CanRain;
	public boolean AlwaysRain;
	public boolean SnowMelt;
	
	public String Message;
	
	public int Months_Number = 3;
	public String Months_Message;
	
	public boolean Spout_StarsVisible = true;
	public int Spout_StarsFrequency = 1500;
	public boolean Spout_CloudsVisible;
	public boolean Spout_SunVisible;
	public int Spout_SunSizePercent;
	public boolean Spout_MoonVisible = true;
	public int Spout_MoonSizePercent = 100;
	
	public SeasonConfig(final File file) {
		CONFIG_FILE = file;
		CONFIG_HEADER = "######################################################### #";
		CONFIG_HEADER += "\n              Skyoseasons Configuration                 #";
		CONFIG_HEADER += "\n Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #";
		CONFIG_HEADER += "\n               for more informations.                   #";
		CONFIG_HEADER += "\n####################################################### #";
		
		Replacements.put(Biome.MUSHROOM_ISLAND.name(), Biome.MUSHROOM_ISLAND.name());
		Replacements.put(Biome.MUSHROOM_SHORE.name(),  Biome.MUSHROOM_SHORE.name());
	}
	
}
