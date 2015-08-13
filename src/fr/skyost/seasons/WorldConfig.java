package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;

import fr.skyost.seasons.utils.Skyoconfig;

public class WorldConfig extends Skyoconfig {
	
	public int day = 1;
	public int month = 1;
	public String season = "Summer";
	@ConfigOptions(name = "season-month")
	public int seasonMonth = 1;
	public int year = 2000;
	
	public WorldConfig(final File file) {
		super(file, Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
	}
	
	public WorldConfig(final File file, final SeasonWorld world) {
		this(file);
		day = world.day;
		month = world.month.number;
		season = world.season.name;
		seasonMonth = world.seasonMonth;
		year = world.year;
	}
	
}