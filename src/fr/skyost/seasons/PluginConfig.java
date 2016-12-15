package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fr.skyost.seasons.utils.Skyoconfig;

public class PluginConfig extends Skyoconfig {
	
	@ConfigOptions(name = "seasons-directory")
	public String seasonsDir;
	@ConfigOptions(name = "worlds-saves-directory")
	public String worldsDir;
	@ConfigOptions(name = "worlds")
	public List<String> worlds = Arrays.asList("WorldA", "WorldB", "WorldC", "You can add (or remove) any world you want here !");
	
	@ConfigOptions(name = "time-control.refresh-time")
	public int timeControlRefreshTime = 20;
	
	@ConfigOptions(name = "backups.enable")
	public boolean backupsEnable = true;
	@ConfigOptions(name = "backups.directory")
	public String backupsDir;
	
	@ConfigOptions(name = "logs.console.enable")
	public boolean logsConsoleEnable = true;
	@ConfigOptions(name = "logs.file.enable")
	public boolean logsFileEnable = false;
	@ConfigOptions(name = "logs.file.directory")
	public String logsFileDir;
	
	@ConfigOptions(name = "enable.protocollib")
	public boolean enableProtocolLib = true;
	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	@ConfigOptions(name = "enable.skyupdater")
	public boolean enableSkyupdater = true;
	
	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
		seasonsDir = new File(dataFolder + File.separator + "seasons").getPath();
		worldsDir = new File(dataFolder + File.separator + "worlds").getPath();
		logsFileDir = new File(dataFolder + File.separator + "logs").getPath();
		backupsDir = new File(dataFolder + File.separator + "backups").getPath();
	}
	
}