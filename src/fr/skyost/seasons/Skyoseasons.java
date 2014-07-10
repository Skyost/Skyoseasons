package fr.skyost.seasons;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.primitives.Ints;

import fr.skyost.seasons.commands.CalendarCommand;
import fr.skyost.seasons.commands.SkyoseasonsCommand;
import fr.skyost.seasons.listeners.EventsListener;
import fr.skyost.seasons.utils.LogsManager;
import fr.skyost.seasons.utils.MetricsLite;
import fr.skyost.seasons.utils.Skyupdater;
import fr.skyost.seasons.utils.Utils;
import fr.skyost.seasons.utils.protocollib.ProtocolLibHook;
import fr.skyost.seasons.utils.spout.SpoutHook;

public class Skyoseasons extends JavaPlugin {
	
	public static PluginConfig config;
	public static CalendarConfig calendar;
	public static LogsManager logsManager;
	public static Skyoseasons instance;
	
	public static SpoutHook spout;
	public static ProtocolLibHook protocolLib;
	
	public static final HashMap<String, Season> seasons = new HashMap<String, Season>();
	public static final HashMap<String, SeasonWorld> worlds = new HashMap<String, SeasonWorld>();
	public static final HashMap<String, Month> months = new HashMap<String, Month>();
	
	@Override
	public final void onEnable() {
		final PluginManager manager = Bukkit.getPluginManager();
		try {
			instance = this;
			final File dataFolder = this.getDataFolder();
			config = new PluginConfig(dataFolder);
			config.load();
			calendar = new CalendarConfig(dataFolder);
			calendar.load();
			logsManager = new LogsManager(config.logsConsoleEnable ? this.getLogger() : null, config.logsFileEnable ? new File(config.logsFileDir) : null);
			manager.registerEvents(new EventsListener(), this);
			if(config.enableSpout && manager.getPlugin("Spout") != null) {
				spout = new SpoutHook();
				logsManager.log("Spout hooked !");
			}
			if(config.enableProtocolLib && manager.getPlugin("ProtocolLib") != null) {
				protocolLib = new ProtocolLibHook();
				logsManager.log("ProtocolLib hooked !");
			}
			if(config.enableMetrics) {
				new MetricsLite(this).start();
			}
			if(config.enableSkyupdater) {
				new Skyupdater(this, 64442, this.getFile(), true, true);
			}
			for(int i = 1; !(i > calendar.months.size()); i++) {
				final HashMap<Object, Object> months = Utils.fromJson(calendar.months.get(String.valueOf(i)));
				final String name = (String)months.get("Name");
				final String next = calendar.months.get(String.valueOf(i + 1));
				Skyoseasons.months.put(name, new Month(name, String.valueOf(next == null ? Utils.fromJson(calendar.months.get("1")).get("Name") : Utils.fromJson(next).get("Name")), i, Ints.checkedCast((long)months.get("Days"))));
			}
			final File seasonsFolder = new File(config.seasonsDir);
			final SeasonConfig[] seasons;
			if(!seasonsFolder.exists()) {
				seasonsFolder.mkdir();
				seasons = getDefaultSeasons(seasonsFolder);
			}
			else {
				final File[] files = seasonsFolder.listFiles();
				if(files.length != 0) {
					final List<SeasonConfig> configs = new ArrayList<SeasonConfig>();
					SeasonConfig season;
					for(final File file : files) {
						final String fileName = file.getName();
						if(fileName.endsWith(".yml")) {
							season = new SeasonConfig(file);
							season.load();
							if(protocolLib != null) {
								for(final Entry<String, String> entry : season.replacements.entrySet()) {
									final String key = entry.getKey();
									final String value = entry.getValue();
									if(protocolLib.biomes.get(Biome.valueOf(key)) == null) {
										logsManager.log("The ProtocolLib hook actually does not supports the biome '" + key + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
										manager.disablePlugin(this);
										return;
									}
									else if(protocolLib.biomes.get(Biome.valueOf(value)) == null) {
										logsManager.log("The ProtocolLib hook actually does not supports the biome '" + value + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
										manager.disablePlugin(this);
										return;
									}
									else if(protocolLib.biomes.get(season.defaultBiome) == null) {
										logsManager.log("The ProtocolLib hook actually does not supports the biome '" + season.defaultBiome.name() + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
										manager.disablePlugin(this);
										return;
									}
								}
							}
							configs.add(season);
						}
						else {
							logsManager.log("'" + fileName + "' is not a valid season file !", Level.WARNING);
						}
					}
					seasons = configs.toArray(new SeasonConfig[configs.size()]);
				}
				else {
					seasons = getDefaultSeasons(seasonsFolder);
				}
			}
			for(final SeasonConfig season : seasons) {
				if(!season.getFile().exists()) {
					season.save();
				}
				Skyoseasons.seasons.put(season.name, new Season(season));
			}
			final File backupsDir;
			if(config.backupsEnable) {
				backupsDir = new File(config.backupsDir);
				if(!backupsDir.exists()) {
					backupsDir.mkdir();
				}
			}
			else {
				backupsDir = null;
			}
			for(final String worldName : config.worlds) {
				final World world = Bukkit.getWorld(worldName);
				if(world != null) {
					if(backupsDir != null) {
						final File backup = new File(backupsDir, worldName);
						if(!backup.exists()) {
							backup.mkdir();
							Utils.copy(new File(worldName), backup);
						}
					}
					final List<String> data = config.savedData.get(worldName);
					if(data != null) {
						final Season season = Skyoseasons.seasons.get(data.get(0));
						final Month month = Skyoseasons.months.get(data.get(3));
						if(season != null && month != null) {
							worlds.put(worldName, new SeasonWorld(world, season, Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), month, Integer.parseInt(data.get(4))));
							continue;
						}
					}
					worlds.put(worldName, new SeasonWorld(world));
				}
				else {
					logsManager.log("The world '" + worldName + "' does not exists !", Level.WARNING);
				}
			}
			PluginCommand command = this.getCommand("skyoseasons");
			command.setUsage(ChatColor.RED + "/skyoseasons [day|month|season|season-month|year] <new day/month/season/season-month/year>.");
			command.setExecutor(new SkyoseasonsCommand());
			command = this.getCommand("calendar");
			command.setUsage(ChatColor.GOLD + "/calendar <world>.");
			command.setExecutor(new CalendarCommand());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			manager.disablePlugin(this);
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			for(final SeasonWorld world : worlds.values()) {
				config.savedData.put(world.world.getName(), Arrays.asList(world.season.name, String.valueOf(world.seasonMonth), String.valueOf(world.day), world.month.name, String.valueOf(world.year)));
			}
			config.save();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final SeasonConfig[] getDefaultSeasons(final File seasonsFolder) {
		final List<SeasonConfig> configs = new ArrayList<SeasonConfig>();
		SeasonConfig season = new SeasonConfig(new File(seasonsFolder, "spring.yml"));
		season.name = "Spring";
		season.next = "Summer";
		season.defaultBiome = Biome.JUNGLE;
		season.canRain = true;
		season.alwaysRain = false;
		season.snowMelt = true;
		season.dayLength = 600;
		season.dayMessageMessage = "§5A new purple Spring day !";
		season.nightLength = 600;
		season.nightMessageMessage = "§5Night is coming. Prepare yourself !";
		season.message = "§dIt is Spring, flowers grow on trees...";
		season.monthsMessage = "§d/month/, when pink and purple are everywhere...";
		season.spoutCloudsVisible = true;
		season.spoutSunVisible = true;
		season.spoutSunSizePercent = 100;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "summer.yml"));
		season.name = "Summer";
		season.next = "Autumn";
		season.defaultBiome = Biome.PLAINS;
		season.canRain = false;
		season.alwaysRain = false;
		season.snowMelt = true;
		season.dayLength = 700;
		season.dayMessageMessage = "§eA beautiful Summer day is coming !";
		season.nightLength = 500;
		season.nightMessageMessage = "§eYet another beautiful but dangerous night.";
		season.message = "§eIt is Summer, enjoy the sunshine !";
		season.monthsMessage = "§eWe are in /month/, let's go to the beach !";
		season.spoutCloudsVisible = false;
		season.spoutSunVisible = true;
		season.spoutSunSizePercent = 120;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "autumn.yml"));
		season.name = "Autumn";
		season.next = "Winter";
		season.defaultBiome = Biome.DESERT;
		season.canRain = true;
		season.alwaysRain = true;
		season.snowMelt = true;
		season.dayLength = 600;
		season.dayMessageMessage = "§7It is a another sad day of Autumn.";
		season.nightLength = 600;
		season.nightMessageMessage = "§7Ready for another night ?";
		season.message = "§8It is Autumn, end of the beach and the sea...";
		season.monthsMessage = "§8We are in the sad month of /month/.";
		season.spoutCloudsVisible = true;
		season.spoutSunVisible = false;
		season.spoutSunSizePercent = 100;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "winter.yml"));
		season.name = "Winter";
		season.next = "Spring";
		season.defaultBiome = Biome.ICE_PLAINS;
		season.canRain = true;
		season.alwaysRain = false;
		season.snowMelt = false;
		season.dayLength = 500;
		season.dayMessageMessage = "§fBrrrr... Winter days are so rude !";
		season.nightLength = 700;
		season.nightMessageMessage = "§fNights are so cold in Winter...";
		season.message = "§fIt is Winter, say welcome to the snow !";
		season.monthsMessage = "§fThe cold month of /month/ is here...";
		season.spoutCloudsVisible = true;
		season.spoutSunVisible = false;
		season.spoutSunSizePercent = 100;
		configs.add(season);
		return configs.toArray(new SeasonConfig[configs.size()]);
	}
	
}
