package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	
	@ConfigOptions(name = "protocollib.snow-placer.enable")
	public boolean protocolLibSnowPlacerEnabled = false;
	@ConfigOptions(name = "protocollib.snow-placer.allow-stacks")
	public boolean protocolLibSnowPlacerAllowStacks = true;
	@ConfigOptions(name = "protocollib.snow-placer.max-delay")
	public int protocolLibSnowPlacerMaxDelay = 60;
	
	public SeasonConfig(final File file) {
		this(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
	}
	
	public SeasonConfig(final File file, final String name, final String next, final Biome defaultBiome, final boolean canRain, final boolean alwaysRain, final boolean snowMelt, final int dayLength, final String dayMessageMessage, final int nightLength, final String nightMessageMessage, final String message, final String monthsMessage, final boolean protocolLibSnowPlacerEnabled) {
		this(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
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
		this.protocolLibSnowPlacerEnabled = protocolLibSnowPlacerEnabled;
	}
	
	private SeasonConfig(final File file, final List<String> header) {
		super(file, header);
		replacements.put(Biome.ICE_FLATS.name(), Biome.ICE_FLATS.name());
		replacements.put(Biome.ICE_MOUNTAINS.name(), Biome.ICE_MOUNTAINS.name());
		replacements.put(Biome.MUTATED_ICE_FLATS.name(), Biome.MUTATED_ICE_FLATS.name());
		replacements.put(Biome.FROZEN_RIVER.name(), Biome.FROZEN_RIVER.name());
		replacements.put(Biome.FROZEN_OCEAN.name(), Biome.FROZEN_OCEAN.name());
		replacements.put(Biome.COLD_BEACH.name(), Biome.COLD_BEACH.name());
		replacements.put(Biome.TAIGA.name(), Biome.TAIGA.name());
		replacements.put(Biome.TAIGA_COLD.name(), Biome.TAIGA_COLD.name());
		replacements.put(Biome.TAIGA_HILLS.name(), Biome.TAIGA_HILLS.name());
		replacements.put(Biome.TAIGA_COLD_HILLS.name(), Biome.TAIGA_COLD_HILLS.name());
		replacements.put(Biome.EXTREME_HILLS.name(), Biome.EXTREME_HILLS.name());
		replacements.put(Biome.EXTREME_HILLS_WITH_TREES.name(), Biome.EXTREME_HILLS_WITH_TREES.name());
		
		replacements.put(Biome.DESERT.name(), Biome.DESERT.name());
		replacements.put(Biome.MESA.name(), Biome.MESA.name());
	}
	
}