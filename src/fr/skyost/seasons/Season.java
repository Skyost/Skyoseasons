package fr.skyost.seasons;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Biome;

public class Season {
	
	public final String name;
	public final String next;
	
	public final Biome defaultBiome;
	public final HashMap<Biome, Biome> replacements = new HashMap<Biome, Biome>();
	public final String resourcePackUrl;
	
	public final boolean canRain;
	public final boolean alwaysRain;
	public final boolean snowMelt;
	
	public final String message;
	
	public final int months;
	public final String monthsMessage;
	
	public final int daylength;
	public final boolean dayMessageEnabled;
	public final String dayMessage;
	public final int nightLength;
	public final boolean nightMessageEnabled;
	public final String nightMessage;
	
	public final boolean snowPlacerEnabled;
	public final boolean snowPlacerAllowStacks;
	public final int snowPlacerDelay;
	
	public Season(final SeasonConfig config) {
		name = config.name;
		next = config.next;
		defaultBiome = config.defaultBiome;
		for(final Entry<String, String> entry : config.replacements.entrySet()) {
			replacements.put(Biome.valueOf(entry.getKey()), Biome.valueOf(entry.getValue()));
		}
		resourcePackUrl = !config.resourcePackUrl.equalsIgnoreCase("NONE") && config.resourcePackUrl.startsWith("http") ? config.resourcePackUrl : null;
		canRain = config.canRain;
		alwaysRain = config.alwaysRain;
		snowMelt = config.snowMelt;
		message = config.message;
		months = config.monthsNumber;
		monthsMessage = config.monthsMessage;
		daylength = config.dayLength;
		dayMessageEnabled = config.dayMessageEnable;
		dayMessage = dayMessageEnabled ? config.dayMessageMessage : null;
		nightLength = config.nightLength;
		nightMessageEnabled = config.nightMessageEnable;
		nightMessage = nightMessageEnabled ? config.nightMessageMessage : null;
		snowPlacerEnabled = config.protocolLibSnowPlacerEnabled;
		snowPlacerAllowStacks = config.protocolLibSnowPlacerAllowStacks;
		snowPlacerDelay = config.protocolLibSnowPlacerMaxDelay;
	}
	
}
