package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import fr.skyost.seasons.utils.Skyoconfig;
import fr.skyost.seasons.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class SeasonConfig extends Skyoconfig {
	
	public String name;
	public String next;
	public String message;
	
	@ConfigOptions(name = "title.enable")
	public boolean titleEnable = !Utils.MC_SERVER_VERSION.startsWith("v1_7");
	@ConfigOptions(name = "title.message")
	public String titleMessage = ChatColor.GOLD + "New season !";
	@ConfigOptions(name = "title.subtitle")
	public String titleSubtitle = ChatColor.YELLOW + "A new season is coming...";
	@ConfigOptions(name = "title.fade-in")
	public int titleFadeIn = 10;
	@ConfigOptions(name = "title.stay")
	public int titleStay = 70;
	@ConfigOptions(name = "title.fade-out")
	public int titleFadeOut = 20;
	
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
	@ConfigOptions(name = "cancel-auto-snow-placing")
	public boolean cancelAutoSnowPlacing = true;
	
	@ConfigOptions(name = "snow-melt.enable")
	public boolean snowMeltEnable;
	@ConfigOptions(name = "snow-melt.eternal-y")
	public int snowMeltEternalY = 95;
	@ConfigOptions(name = "snow-melt.max-delay")
	public int snowMeltMaxDelay = 60;
	
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
	@ConfigOptions(name = "protocollib.snow-placer.forbidden-blocks")
	public List<String> protocollibSnowPlacerForbiddenTypes = Arrays.asList(Material.ICE.name());
	@ConfigOptions(name = "protocollib.snow-placer.forbidden-biomes")
	public List<String> protocollibSnowPlacerForbiddenBiomes = Arrays.asList(Biome.DESERT.name());
	
	public SeasonConfig(final File file) {
		this(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
	}
	
	public SeasonConfig(final File file, final String name, final String next, final String titleMessage, final String titleSubtitle, final Biome defaultBiome, final boolean canRain, final boolean alwaysRain, final boolean snowMeltEnable, final int dayLength, final String dayMessageMessage, final int nightLength, final String nightMessageMessage, final String message, final String monthsMessage, final boolean protocolLibSnowPlacerEnabled) {
		this(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
		this.name = name;
		this.next = next;
		this.titleMessage = titleMessage;
		this.titleSubtitle = titleSubtitle;
		this.defaultBiome = defaultBiome;
		this.canRain = canRain;
		this.alwaysRain = alwaysRain;
		this.snowMeltEnable = snowMeltEnable;
		
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
		
		replacements.put(Biome.DESERT.name(), Biome.DESERT.name());
		replacements.put(Biome.MESA.name(), Biome.MESA.name());
	}
	
}