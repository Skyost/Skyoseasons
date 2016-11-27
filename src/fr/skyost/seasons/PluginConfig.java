package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import fr.skyost.seasons.utils.Skyoconfig;

public class PluginConfig extends Skyoconfig {
	
	@ConfigOptions(name = "seasons-directory")
	public String seasonsDir;
	@ConfigOptions(name = "worlds-saves-directory")
	public String worldsDir;
	@ConfigOptions(name = "refresh-time")
	public int refreshTime = 20;
	@ConfigOptions(name = "worlds")
	public List<String> worlds = Arrays.asList("WorldA", "WorldB", "WorldC", "You can add (or remove) any world you want here !");
	
	@ConfigOptions(name = "backups.enable")
	public boolean backupsEnable = true;
	@ConfigOptions(name = "backups.directory")
	public String backupsDir;
	
	@ConfigOptions(name = "snow.eternal-y")
	public int snowEternalY = 100;
	@ConfigOptions(name = "snow.melt.max-delay")
	public int snowMeltMaxDelay = 60;
	@ConfigOptions(name = "snow.melt.multiplicator")
	public int snowMeltMultiplicator = 10;
	@ConfigOptions(name = "snow.placer.forbidden-blocks")
	public List<String> snowPlacerForbiddenTypes = Arrays.asList(Material.ICE.name());
	@ConfigOptions(name = "snow.placer.forbidden-biomes")
	public List<String> snowPlacerForbiddenBiomes = Arrays.asList(Biome.DESERT.name());
	
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
