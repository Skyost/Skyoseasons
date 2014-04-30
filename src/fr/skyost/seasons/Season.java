package fr.skyost.seasons;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Biome;

import fr.skyost.seasons.utils.spout.SpoutEffects;

public class Season {
	
	public final String name;
	public final String next;
	
	public final Biome defaultBiome;
	public final HashMap<Biome, Biome> replacements = new HashMap<Biome, Biome>();
	
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
	
	public final SpoutEffects effects;
	
	public Season(final SeasonConfig config) {
		name = config.Name;
		next = config.Next;
		defaultBiome = config.DefaultBiome;
		for(final Entry<String, String> entry : config.Replacements.entrySet()) {
			replacements.put(Biome.valueOf(entry.getKey()), Biome.valueOf(entry.getValue()));
		}
		canRain = config.CanRain;
		alwaysRain = config.AlwaysRain;
		snowMelt = config.SnowMelt;
		message = config.Message;
		months = config.Months_Number;
		monthsMessage = config.Months_Message;
		daylength = config.Day_Length;
		dayMessageEnabled = config.Day_Message_Enable;
		dayMessage = dayMessageEnabled ? config.Day_Message_Message : null;
		nightLength = config.Night_Length;
		nightMessageEnabled = config.Night_Message_Enable;
		nightMessage = nightMessageEnabled ? config.Night_Message_Message : null;
		if(Skyoseasons.spout != null) {
			effects = new SpoutEffects();
			effects.cloudsVisible = config.Spout_CloudsVisible;
			effects.moonSizePercent = config.Spout_MoonSizePercent;
			effects.moonVisible = config.Spout_MoonVisible;
			effects.starsFrequency = config.Spout_StarsFrequency;
			effects.sunSizePercent = config.Spout_SunSizePercent;
			effects.sunVisible = config.Spout_SunVisible;
		}
		else {
			effects = null;
		}
	}
	
}
