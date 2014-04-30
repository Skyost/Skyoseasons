package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.skyost.seasons.utils.Config;

public class PluginConfig extends Config {
	
	public String SeasonsFolder;
	
	public boolean Backups_Enable = true;
	public String Backups_Directory;
	
	public int Snow_EternalY = 100;
	public int Snow_MeltMultiplicator = 10;
	
	public List<String> Worlds = Arrays.asList("WorldA", "WorldB", "WorldC", "You can add (or remove) any world you want here !");
	
	public boolean Logs_Console_Enable = true;
	public boolean Logs_File_Enable = false;
	public String Logs_File_Directory;
	
	public HashMap<String, List<String>> SavedData = new HashMap<String, List<String>>();
	
	public int RefreshTime = 20;
	
	public boolean Enable_Spout = false;
	public boolean Enable_ProtocolLib = false;
	public boolean Enable_Metrics = true;
	public boolean Enable_Skyupdater = true;
	
	public PluginConfig(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "config.yml");
		CONFIG_HEADER = "######################################################### #";
		CONFIG_HEADER += "\n              Skyoseasons Configuration                 #";
		CONFIG_HEADER += "\n Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #";
		CONFIG_HEADER += "\n               for more informations.                   #";
		CONFIG_HEADER += "\n####################################################### #";
		
		SeasonsFolder = new File(dataFolder + File.separator + "seasons").getPath();
		Logs_File_Directory = new File(dataFolder + File.separator + "logs").getPath();
		Backups_Directory = new File(dataFolder + File.separator + "backups").getPath();
	}
	
}
