package fr.skyost.seasons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.primitives.Ints;

import fr.skyost.seasons.commands.CalendarCommand;
import fr.skyost.seasons.commands.SkyoseasonsCommand;
import fr.skyost.seasons.commands.subcommands.skyoseasons.*;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;
import fr.skyost.seasons.listeners.EventsListener;
import fr.skyost.seasons.utils.LogsManager;
import fr.skyost.seasons.utils.Metrics;
import fr.skyost.seasons.utils.MonthLinkedHashMap;
import fr.skyost.seasons.utils.Skyupdater;
import fr.skyost.seasons.utils.Utils;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook.PacketPluginHookInitializationException;

public class Skyoseasons extends JavaPlugin {
	
	protected PluginConfig config;
	protected CalendarConfig calendar;
	protected LogsManager logsManager;
	
	protected AbstractProtocolLibHook protocolLib;
	
	protected final HashMap<String, Season> seasons = new HashMap<String, Season>();
	protected final HashMap<String, SeasonWorld> worlds = new HashMap<String, SeasonWorld>();
	protected final MonthLinkedHashMap<String, Month> months = new MonthLinkedHashMap<String, Month>();
	
	@Override
	public final void onEnable() {
		final PluginManager manager = Bukkit.getPluginManager();
		try {
			setupPlugin(manager);
			setupMonths();
			setupSeasons(manager);
			setupWorlds();
			setupCommands();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			manager.disablePlugin(this);
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			final File worldsDir = new File(config.worldsDir);
			if(!worldsDir.exists()) {
				worldsDir.mkdir();
			}
			for(final SeasonWorld world : worlds.values()) {
				new WorldConfig(new File(worldsDir, world.world.getName() + ".yml"), world).save();
			}
			config.save();
			Utils.clearFields(this);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private final void setupPlugin(final PluginManager manager) throws InvalidConfigurationException, PacketPluginHookInitializationException, IOException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		final File dataFolder = this.getDataFolder();
		config = new PluginConfig(dataFolder);
		config.load();
		calendar = new CalendarConfig(dataFolder);
		calendar.load();
		logsManager = new LogsManager(config.logsConsoleEnable ? this.getLogger() : null, config.logsFileEnable ? new File(config.logsFileDir) : null);
		manager.registerEvents(new EventsListener(), this);
		if(config.enableMetrics) {
			new Metrics(this);
		}
		if(config.enableSkyupdater) {
			new Skyupdater(this, 64442, this.getFile(), true, true);
		}
		if(!config.enableProtocolLib) {
			return;
		}
		final Plugin protocolLibPlugin = manager.getPlugin("ProtocolLib");
		if(protocolLibPlugin != null && protocolLibPlugin.isEnabled()) {
			String state;
			try {
				protocolLib = (AbstractProtocolLibHook)Class.forName("fr.skyost.seasons.utils.packets." + Utils.MC_SERVER_VERSION + ".ProtocolLibHook").getConstructor(Plugin.class, Plugin.class).newInstance(protocolLibPlugin, this);
				state = "loaded";
			}
			catch(final ClassNotFoundException ex) {
				state = "cannot be found";
			}
			logsManager.log("ProtocolLib hook for CB " + Utils.MC_SERVER_VERSION + " " + state + " !");
		}
		if(protocolLib == null) {
			logsManager.log("ProtocolLib was not found (or the hook has been disabled). Please not that this plugin may not work properly without ProtocolLib.", Level.WARNING);
		}
	}
	
	private final void setupMonths() {
		for(int i = 1; i <= calendar.months.size(); i++) {
			final HashMap<Object, Object> months = Utils.fromJson(calendar.months.get(String.valueOf(i)));
			final String name = (String)months.get("Name");
			final String next = calendar.months.get(String.valueOf(i + 1));
			this.months.put(name, new Month(name, String.valueOf(next == null ? Utils.fromJson(calendar.months.get("1")).get("Name") : Utils.fromJson(next).get("Name")), i, Ints.checkedCast((long)months.get("Days"))));
		}
	}
	
	private final void setupSeasons(final PluginManager manager) throws InvalidConfigurationException {
		final File seasonsDir = new File(config.seasonsDir);
		final SeasonConfig[] seasons;
		if(!seasonsDir.exists() || seasonsDir.list().length == 0) {
			seasonsDir.mkdir();
			seasons = getDefaultSeasons(seasonsDir);
		}
		else {
			final File[] files = seasonsDir.listFiles();
			if(files.length != 0) {
				final List<SeasonConfig> configs = new ArrayList<SeasonConfig>();
				for(final File file : files) {
					final String fileName = file.getName();
					if(!file.isFile() || !fileName.endsWith(".yml")) {
						logsManager.log("'" + fileName + "' is not a valid season file !", Level.WARNING);
						continue;
					}
					final SeasonConfig season = new SeasonConfig(file);
					season.load();
					if(protocolLib != null) {
						for(final Entry<String, String> entry : season.replacements.entrySet()) {
							final String key = entry.getKey();
							final String value = entry.getValue();
							if(protocolLib.getBiomeID(Biome.valueOf(key)) == null) {
								logsManager.log("Currently, the ProtocolLib hook does not support the biome '" + key + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
								manager.disablePlugin(this);
								continue;
							}
							else if(protocolLib.getBiomeID(Biome.valueOf(value)) == null) {
								logsManager.log("Currently, the ProtocolLib hook does not support the biome '" + value + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
								manager.disablePlugin(this);
								continue;
							}
							else if(protocolLib.getBiomeID(season.defaultBiome) == null) {
								logsManager.log("Currently, the ProtocolLib hook does not support the biome '" + season.defaultBiome.name() + "'. Try another one or disable the ProtocolLib hook.", Level.SEVERE);
								manager.disablePlugin(this);
								continue;
							}
						}
					}
					configs.add(season);
				}
				seasons = configs.toArray(new SeasonConfig[configs.size()]);
			}
			else {
				seasons = getDefaultSeasons(seasonsDir);
			}
		}
		for(final SeasonConfig season : seasons) {
			if(!season.getFile().exists()) {
				season.save();
			}
			this.seasons.put(season.name, new Season(season));
		}
	}
	
	private final void setupWorlds() throws IOException, InvalidConfigurationException {
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
		final File worldsDir = new File(config.worldsDir);
		if(!worldsDir.exists()) {
			worldsDir.mkdir();
		}
		for(final String worldName : config.worlds) {
			final World world = Bukkit.getWorld(worldName);
			if(world == null) {
				logsManager.log("The world '" + worldName + "' does not exist !", Level.WARNING);
				continue;	
			}
			if(backupsDir != null) {
				final File backup = new File(backupsDir, worldName);
				if(!backup.exists()) {
					Utils.copy(new File(worldName), backup);
				}
			}
			final File configFile = new File(worldsDir, worldName + ".yml");
			final SeasonWorld seasonWorld;
			if(configFile.exists()) {
				final WorldConfig config = new WorldConfig(configFile);
				config.load();
				seasonWorld = new SeasonWorld(world, config);
				if(seasonWorld.month == null) {
					logsManager.log("The month " + config.month + " was not found. Please delete this world save.", Level.SEVERE, world);
					throw new NullPointerException();
				}
				if(seasonWorld.season == null) {
					logsManager.log("The season " + config.season + " was not found. Please delete this world save.", Level.SEVERE, world);
					throw new NullPointerException();
				}
				worlds.put(worldName, seasonWorld);
				SkyoseasonsAPI.callSeasonChange(seasonWorld, seasonWorld.season, ModificationCause.PLUGIN);
				seasonWorld.seasonMonth = config.seasonMonth;
				continue;
			}
			seasonWorld = new SeasonWorld(world);
			worlds.put(worldName, seasonWorld);
			final Season winter = SkyoseasonsAPI.getSeasonExact("Winter");
			SkyoseasonsAPI.callSeasonChange(seasonWorld, winter == null ? SkyoseasonsAPI.getSeasons()[0] : winter, ModificationCause.PLUGIN);
		}
	}
	
	private final void setupCommands() {
		final SkyoseasonsCommand executor = new SkyoseasonsCommand();
		executor.registerSubCommand(new SkyoseasonsDay());
		executor.registerSubCommand(new SkyoseasonsMonth());
		executor.registerSubCommand(new SkyoseasonsSeason());
		executor.registerSubCommand(new SkyoseasonsSeasonMonth());
		executor.registerSubCommand(new SkyoseasonsYear());
		
		final PluginCommand skyoseasons = this.getCommand("skyoseasons");
		skyoseasons.setUsage(ChatColor.RED + skyoseasons.getUsage());
		skyoseasons.setExecutor(executor);
		
		final PluginCommand calendar = this.getCommand("calendar");
		calendar.setUsage(ChatColor.GOLD + calendar.getUsage());
		calendar.setExecutor(new CalendarCommand());
	}
	
	private static final SeasonConfig[] getDefaultSeasons(final File seasonsDir) {
		Biome winter;
		try { // 1.9 update
			winter = Biome.valueOf("ICE_PLAINS");
		}
		catch(final IllegalArgumentException ex) {
			winter = Biome.valueOf("ICE_FLATS");
		}
		final List<SeasonConfig> configs = new ArrayList<SeasonConfig>();
		configs.add(new SeasonConfig(
				new File(seasonsDir, "spring.yml"),
				"Spring",
				"Summer",
				ChatColor.DARK_PURPLE + "SPRING",
				ChatColor.LIGHT_PURPLE + "We're in Spring !",
				Biome.JUNGLE,
				true,
				false,
				true,
				600,
				ChatColor.DARK_PURPLE + "A new purple Spring day !",
				600,
				ChatColor.DARK_PURPLE + "Night is coming. Prepare yourself !",
				ChatColor.LIGHT_PURPLE + "It is Spring, flowers grow on trees...",
				ChatColor.LIGHT_PURPLE + "/month/, when pink and purple are everywhere...",
				false
		));
		configs.add(new SeasonConfig(
				new File(seasonsDir, "summer.yml"),
				"Summer",
				"Autumn",
				ChatColor.GOLD + "SUMMER",
				ChatColor.YELLOW + "We're in Summer !",
				Biome.PLAINS,
				false,
				false,
				false,
				700,
				ChatColor.YELLOW + "A beautiful Summer day is coming !",
				500,
				ChatColor.YELLOW + "Yet another beautiful but dangerous night.",
				ChatColor.YELLOW + "It is Summer, enjoy the sunshine !",
				ChatColor.YELLOW + "We are in /month/, let's go to the beach !",
				false
		));
		configs.add(new SeasonConfig(
				new File(seasonsDir, "autumn.yml"),
				"Autumn",
				"Winter",
				ChatColor.DARK_GRAY + "AUTUMN",
				ChatColor.GRAY + "We're in Autumn !",
				Biome.DESERT,
				true,
				true,
				false,
				600,
				ChatColor.GRAY + "It is a another sad day of Autumn.",
				600,
				ChatColor.GRAY + "Ready for another night ?",
				ChatColor.DARK_GRAY + "It is Autumn, end of the beach and the sea...",
				ChatColor.DARK_GRAY + "We are in the sad month of /month/.",
				false
		));
		configs.add(new SeasonConfig(
				new File(seasonsDir, "winter.yml"),
				"Winter",
				"Spring",
				ChatColor.GRAY + "WINTER",
				ChatColor.WHITE + "We're in Winter !",
				winter,
				true,
				false,
				false,
				500,
				ChatColor.WHITE + "Brrrr... Winter days are so rude !",
				700,
				ChatColor.WHITE + "Nights are so cold in Winter...",
				ChatColor.WHITE + "It is Winter, say welcome to the snow !",
				ChatColor.WHITE + "The cold month of /month/ is here...",
				true
		));
		return configs.toArray(new SeasonConfig[configs.size()]);
	}
	
}
