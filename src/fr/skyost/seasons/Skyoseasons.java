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
			config.init();
			calendar = new CalendarConfig(dataFolder);
			calendar.init();
			logsManager = new LogsManager(config.Logs_Console_Enable ? this.getLogger() : null, config.Logs_File_Enable ? new File(config.Logs_File_Directory) : null);
			manager.registerEvents(new EventsListener(), this);
			new ProtocolLibHook();
			if(config.Enable_Spout && manager.getPlugin("Spout") != null) {
				spout = new SpoutHook();
				logsManager.log("Spout hooked !");
			}
			if(config.Enable_ProtocolLib && manager.getPlugin("ProtocolLib") != null) {
				protocolLib = new ProtocolLibHook();
				logsManager.log("ProtocolLib hooked !");
			}
			if(config.Enable_Metrics) {
				new MetricsLite(this).start();
			}
			if(config.Enable_Skyupdater) {
				new Skyupdater(this, 64442, this.getFile(), true, true);
			}
			for(int i = 1; !(i > calendar.Months.size()); i++) {
				final HashMap<Object, Object> months = Utils.fromJson(calendar.Months.get(String.valueOf(i)));
				final String name = (String)months.get("Name");
				final String next = calendar.Months.get(String.valueOf(i + 1));
				Skyoseasons.months.put(name, new Month(name, String.valueOf(next == null ? Utils.fromJson(calendar.Months.get("1")).get("Name") : Utils.fromJson(next).get("Name")), i, Ints.checkedCast((long)months.get("Days"))));
			}
			final File seasonsFolder = new File(config.SeasonsFolder);
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
							season = new SeasonConfig(seasonsFolder);
							season.load(file);
							if(protocolLib != null) {
								for(final Entry<String, String> entry : season.Replacements.entrySet()) {
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
									else if(protocolLib.biomes.get(season.DefaultBiome) == null) {
										logsManager.log("The ProtocolLib hook actually does not supports the biome '" + season.DefaultBiome.name() + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
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
				season.save();
				Skyoseasons.seasons.put(season.Name, new Season(season));
			}
			final File backupsDir;
			if(config.Backups_Enable) {
				backupsDir = new File(config.Backups_Directory);
				if(!backupsDir.exists()) {
					backupsDir.mkdir();
				}
			}
			else {
				backupsDir = null;
			}
			for(final String worldName : config.Worlds) {
				final World world = Bukkit.getWorld(worldName);
				if(world != null) {
					if(backupsDir != null) {
						final File backup = new File(backupsDir, worldName);
						if(!backup.exists()) {
							backup.mkdir();
							Utils.copy(new File(worldName), backup);
						}
					}
					final List<String> data = config.SavedData.get(worldName);
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
				config.SavedData.put(world.world.getName(), Arrays.asList(world.season.name, String.valueOf(world.seasonMonth), String.valueOf(world.day), world.month.name, String.valueOf(world.year)));
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
		season.Name = "Spring";
		season.Next = "Summer";
		season.DefaultBiome = Biome.JUNGLE;
		season.CanRain = true;
		season.AlwaysRain = false;
		season.SnowMelt = true;
		season.Day_Length = 600;
		season.Day_Message_Message = "§5A new purple Spring day !";
		season.Night_Length = 600;
		season.Night_Message_Message = "§5Night is coming. Prepare yourself !";
		season.Message = "§dIt is Spring, flowers grow on trees...";
		season.Months_Message = "§d/month/, when pink and purple are everywhere...";
		season.Spout_CloudsVisible = true;
		season.Spout_SunVisible = true;
		season.Spout_SunSizePercent = 100;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "summer.yml"));
		season.Name = "Summer";
		season.Next = "Autumn";
		season.DefaultBiome = Biome.PLAINS;
		season.CanRain = false;
		season.AlwaysRain = false;
		season.SnowMelt = true;
		season.Day_Length = 700;
		season.Day_Message_Message = "§eA beautiful Summer day is coming !";
		season.Night_Length = 500;
		season.Night_Message_Message = "§eYet another beautiful but dangerous night.";
		season.Message = "§eIt is Summer, enjoy the sunshine !";
		season.Months_Message = "§eWe are in /month/, let's go to the beach !";
		season.Spout_CloudsVisible = false;
		season.Spout_SunVisible = true;
		season.Spout_SunSizePercent = 120;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "autumn.yml"));
		season.Name = "Autumn";
		season.Next = "Winter";
		season.DefaultBiome = Biome.DESERT;
		season.CanRain = true;
		season.AlwaysRain = true;
		season.SnowMelt = true;
		season.Day_Length = 600;
		season.Day_Message_Message = "§7It is a another sad day of Autumn.";
		season.Night_Length = 600;
		season.Night_Message_Message = "§7Ready for another night ?";
		season.Message = "§8It is Autumn, end of the beach and the sea...";
		season.Months_Message = "§8We are in the sad month of /month/.";
		season.Spout_CloudsVisible = true;
		season.Spout_SunVisible = false;
		season.Spout_SunSizePercent = 100;
		configs.add(season);
		season = new SeasonConfig(new File(seasonsFolder, "winter.yml"));
		season.Name = "Winter";
		season.Next = "Spring";
		season.DefaultBiome = Biome.ICE_PLAINS;
		season.CanRain = true;
		season.AlwaysRain = false;
		season.SnowMelt = false;
		season.Day_Length = 500;
		season.Day_Message_Message = "§fBrrrr... Winter days are so rude !";
		season.Night_Length = 700;
		season.Night_Message_Message = "§fNights are so cold in Winter...";
		season.Message = "§fIt is Winter, say welcome to the snow !";
		season.Months_Message = "§fThe cold month of /month/ is here...";
		season.Spout_CloudsVisible = true;
		season.Spout_SunVisible = false;
		season.Spout_SunSizePercent = 100;
		configs.add(season);
		return configs.toArray(new SeasonConfig[configs.size()]);
	}
	
}
