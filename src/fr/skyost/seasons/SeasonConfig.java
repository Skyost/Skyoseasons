package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.block.Biome;

import fr.skyost.seasons.utils.Skyoconfig;

public class SeasonConfig extends Skyoconfig {
	
	public String name;
	public String next;
	public String message;
	
	@ConfigOptions(name = "default-biome")
	public Biome defaultBiome;
	public HashMap<String, String> replacements = new HashMap<String, String>();
	
	@ConfigOptions(name = "day.length")
	public int dayLength;
	@ConfigOptions(name = "day.message.enable")
	public boolean dayMessageEnable = true;
	@ConfigOptions(name = "day.message.message")
	public String dayMessageMessage;
	@ConfigOptions(name = "night.length")
	public int nightLength;
	@ConfigOptions(name = "night.message.enable")
	public boolean nightMessageEnable = true;
	@ConfigOptions(name = "night.message.message")
	public String nightMessageMessage;
	
	@ConfigOptions(name = "can-rain")
	public boolean canRain;
	@ConfigOptions(name = "always-rain")
	public boolean alwaysRain;
	@ConfigOptions(name = "snow-melt")
	public boolean snowMelt;
	
	@ConfigOptions(name = "months.number")
	public int monthsNumber = 3;
	@ConfigOptions(name = "months.message")
	public String monthsMessage;
	
	@ConfigOptions(name = "spout.stars.visible")
	public boolean spoutStarsVisible = true;
	@ConfigOptions(name = "spout.stars.frequency")
	public int spoutStarsFrequency = 1500;
	@ConfigOptions(name = "spout.clouds.visible")
	public boolean spoutCloudsVisible;
	@ConfigOptions(name = "spout.sun.visible")
	public boolean spoutSunVisible;
	@ConfigOptions(name = "spout.sun.size-percent")
	public int spoutSunSizePercent;
	@ConfigOptions(name = "spout.moon.visible")
	public boolean spoutMoonVisible = true;
	@ConfigOptions(name = "spout.moon.size-percent")
	public int spoutMoonSizePercent = 100;
	
	public SeasonConfig(final File file) {
		super(file, Arrays.asList("####################################################### #", "\n              Skyoseasons Configuration                 #", "\n Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "\n               for more informations.                   #", "\n####################################################### #"));
		replacements.put(Biome.MUSHROOM_ISLAND.name(), Biome.MUSHROOM_ISLAND.name());
		replacements.put(Biome.MUSHROOM_SHORE.name(),  Biome.MUSHROOM_SHORE.name());
	}
	
}
