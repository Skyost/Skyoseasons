package fr.skyost.seasons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public class Season {
	
	/**
	 * The name of the season.
	 */
	
	public final String name;
	
	/**
	 * The season that comes after this one.
	 */
	
	public final String next;
	
	/**
	 * The default biome.
	 */
	
	public final Biome defaultBiome;
	
	/**
	 * Replacement (key biome will be replaced by value biome).
	 */
	
	public final HashMap<Biome, Biome> replacements = new HashMap<Biome, Biome>();
	
	/**
	 * If there is a specific resource pack for this season.
	 */
	
	public final String resourcePackUrl;
	
	/**
	 * If it can rain during this season.
	 */
	
	public final boolean canRain;
	
	/**
	 * If it always rain during this season.
	 */
	
	public final boolean alwaysRain;
	
	/**
	 * If no snow (/ ice) should be automatically placed by the server.
	 */
	
	public final boolean cancelAutoSnowPlacing;
	
	/**
	 * If snow melt is enabled during this season.
	 */
	
	public final boolean snowMeltEnabled;
	
	/**
	 * The Y coord where there is no snow melt.
	 */
	
	public final int snowMeltEternalY;
	
	/**
	 * The max delay for snow melt.
	 */
	
	public final int snowMeltDelay;
	
	/**
	 * The message when a world comes to this season.
	 */
	
	public final String message;
	
	/**
	 * Number of months (duration of this season).
	 */
	
	public final int months;
	
	/**
	 * The message when the month change.
	 */
	
	public final String monthsMessage;
	
	/**
	 * A day length.
	 */
	
	public final int daylength;
	
	/**
	 * If the message that announces the day should be broadcasted.
	 */
	
	public final boolean dayMessageEnabled;
	
	/**
	 * The message that announces the day.
	 */
	
	public final String dayMessage;
	
	/**
	 * A night length.
	 */
	
	public final int nightLength;
	
	/**
	 * If the message that announces the night should be broadcasted.
	 */
	
	public final boolean nightMessageEnabled;
	
	/**
	 * The message that announces the night.
	 */
	
	public final String nightMessage;
	
	/**
	 * If snow placer is enabled (consumes a lot of memory).
	 */
	
	public final boolean snowPlacerEnabled;
	
	/**
	 * The forbidden blocks (for snow placer).
	 */
	
	public Set<Material> snowPlacerForbiddenTypes = new HashSet<Material>();
	
	/**
	 * The forbidden biomes (for snow placer).
	 */
	
	public Set<Biome> snowPlacerForbiddenBiomes = new HashSet<Biome>();
	
	/**
	 * Allows stacks for the snow placer.
	 */
	
	public final boolean snowPlacerAllowStacks;
	
	/**
	 * Max delay for snow placer.
	 */
	
	public final int snowPlacerDelay;
	
	/**
	 * Creates a new Season.
	 * 
	 * @param config The configuration.
	 */
	
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
		cancelAutoSnowPlacing = config.cancelAutoSnowPlacing;
		
		snowMeltEnabled = config.snowMeltEnable;
		snowMeltEternalY = config.snowMeltEternalY;
		snowMeltDelay = config.snowMeltMaxDelay;
		
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
		for(final String material : config.protocollibSnowPlacerForbiddenTypes) {
			snowPlacerForbiddenTypes.add(Material.valueOf(material));
		}
		for(final String biome : config.protocollibSnowPlacerForbiddenBiomes) {
			snowPlacerForbiddenBiomes.add(Biome.valueOf(biome));
		}
	}
	
}