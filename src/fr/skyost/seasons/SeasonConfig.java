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
	@ConfigOptions(name = "resource-pack-url")
	public String resourcePackUrl = "NONE";
	
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
	
	@ConfigOptions(name = "protocollib.snow-placer.enable")
	public boolean protocolLibSnowPlacerEnabled = false;
	@ConfigOptions(name = "protocollib.snow-placer.allow-stacks")
	public boolean protocolLibSnowPlacerAllowStacks = true;
	@ConfigOptions(name = "protocollib.snow-placer.max-delay")
	public int protocolLibSnowPlacerMaxDelay = 100;
	
	public SeasonConfig(final File file) {
		super(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
		replacements.put(Biome.MUSHROOM_ISLAND.name(), Biome.MUSHROOM_ISLAND.name());
		replacements.put(Biome.MUSHROOM_SHORE.name(),  Biome.MUSHROOM_SHORE.name());
	}
	
	public SeasonConfig(final File file, final String name, final String next, final Biome defaultBiome, final boolean canRain, final boolean alwaysRain, final boolean snowMelt, final int dayLength, final String dayMessageMessage, final int nightLength, final String nightMessageMessage, final String message, final String monthsMessage, final boolean spoutCloudsVisible, final boolean spoutSunVisible, final int spoutSunSizePercent, final boolean protocolLibSnowPlacerEnabled) {
		super(file);
		this.name = name;
		this.next = next;
		this.defaultBiome = defaultBiome;
		this.canRain = canRain;
		this.alwaysRain = alwaysRain;
		this.snowMelt = snowMelt;
		this.dayLength = dayLength;
		this.dayMessageMessage = dayMessageMessage;
		this.nightLength = nightLength;
		this.nightMessageMessage = nightMessageMessage;
		this.message = message;
		this.monthsMessage = monthsMessage;
		this.spoutCloudsVisible = spoutCloudsVisible;
		this.spoutSunVisible = spoutSunVisible;
		this.spoutSunSizePercent = spoutSunSizePercent;
		this.protocolLibSnowPlacerEnabled = protocolLibSnowPlacerEnabled;
	}
	
}