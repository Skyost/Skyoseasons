package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.skyost.seasons.utils.Skyoconfig;

public class PluginConfig extends Skyoconfig {
	
	@ConfigOptions(name = "seasons-directory")
	public String seasonsDir;
	@ConfigOptions(name = "refresh-time")
	public int refreshTime = 20;
	@ConfigOptions(name = "worlds")
	public List<String> worlds = Arrays.asList("WorldA", "WorldB", "WorldC", "You can add (or remove) any world you want here !");
	@ConfigOptions(name = "saved-data")
	public HashMap<String, List<String>> savedData = new HashMap<String, List<String>>();
	
	@ConfigOptions(name = "backups.enable")
	public boolean backupsEnable = true;
	@ConfigOptions(name = "backups.directory")
	public String backupsDir;
	
	@ConfigOptions(name = "snow.eternal-y")
	public int snowEternalY = 100;
	@ConfigOptions(name = "snow.melt-multiplicator")
	public int snowMeltMultiplicator = 10;
	
	@ConfigOptions(name = "logs.console.enable")
	public boolean logsConsoleEnable = true;
	@ConfigOptions(name = "logs.file.enable")
	public boolean logsFileEnable = false;
	@ConfigOptions(name = "logs.file.directory")
	public String logsFileDir;
	
	@ConfigOptions(name = "enable.spout")
	public boolean enableSpout = false;
	@ConfigOptions(name = "enable.protocollib")
	public boolean enableProtocolLib = false;
	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	@ConfigOptions(name = "enable.skyupdater")
	public boolean enableSkyupdater = true;
	
	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("####################################################### #", "\n              Skyoseasons Configuration                 #", "\n Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "\n               for more informations.                   #", "\n####################################################### #"));
		seasonsDir = new File(dataFolder + File.separator + "seasons").getPath();
		logsFileDir = new File(dataFolder + File.separator + "logs").getPath();
		backupsDir = new File(dataFolder + File.separator + "backups").getPath();
	}
	
}
