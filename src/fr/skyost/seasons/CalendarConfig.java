package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import fr.skyost.seasons.utils.Config;
import fr.skyost.seasons.utils.Utils;

public class CalendarConfig extends Config {
	
	public HashMap<String, String> Months = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("1", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "January");
					put("Days", 31);
				}
			}));
			put("2", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "February");
					put("Days", 28);
				}
			}));
			put("3", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "March");
					put("Days", 31);
				}
			}));
			put("4", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "April");
					put("Days", 30);
				}
			}));
			put("5", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "May");
					put("Days", 31);
				}
			}));
			put("6", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "June");
					put("Days", 30);
				}
			}));
			put("7", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "July");
					put("Days", 31);
				}
			}));
			put("8", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "August");
					put("Days", 31);
				}
			}));
			put("9", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "September");
					put("Days", 30);
				}
			}));
			put("10", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "October");
					put("Days", 31);
				}
			}));
			put("11", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "November");
					put("Days", 30);
				}
			}));
			put("12", Utils.toJson(new HashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "December");
					put("Days", 31);
				}
			}));
		}
	};
	public List<String> OrdinalSuffixes = Arrays.asList("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th");
	
	public Material Calendar_Today_Item = Material.NAME_TAG;
	public String Calendar_Today_Name = "§l§6TODAY : /month/ /day-number//ordinal/ /year/";
	public Material Calendar_Days_Item = Material.PAPER;
	public String Calendar_Days_Name = "/month/ /day-number//ordinal/ /year/";
	
	public String Messages_Year = "§4Happy new year ! We are in /year/ :D";
	
	public CalendarConfig(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "calendar.yml");
		CONFIG_HEADER = "######################################################### #";
		CONFIG_HEADER += "\n              Skyoseasons Configuration                 #";
		CONFIG_HEADER += "\n Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #";
		CONFIG_HEADER += "\n               for more informations.                   #";
		CONFIG_HEADER += "\n####################################################### #";
	}
	
}
