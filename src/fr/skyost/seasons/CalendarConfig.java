package fr.skyost.seasons;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.skyost.seasons.utils.Skyoconfig;
import fr.skyost.seasons.utils.Utils;

public class CalendarConfig extends Skyoconfig {
	
	@ConfigOptions(name = "months")
	public LinkedHashMap<String, String> months = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("1", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "January");
					put("Days", 31);
				}
			}));
			put("2", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "February");
					put("Days", 28);
				}
			}));
			put("3", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "March");
					put("Days", 31);
				}
			}));
			put("4", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "April");
					put("Days", 30);
				}
			}));
			put("5", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "May");
					put("Days", 31);
				}
			}));
			put("6", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "June");
					put("Days", 30);
				}
			}));
			put("7", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "July");
					put("Days", 31);
				}
			}));
			put("8", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "August");
					put("Days", 31);
				}
			}));
			put("9", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "September");
					put("Days", 30);
				}
			}));
			put("10", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "October");
					put("Days", 31);
				}
			}));
			put("11", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "November");
					put("Days", 30);
				}
			}));
			put("12", Utils.toJson(new LinkedHashMap<Object, Object>() {
				private static final long serialVersionUID = 1L; {
					put("Name", "December");
					put("Days", 31);
				}
			}));
		}
	};
	@ConfigOptions(name = "ordinal-suffixes")
	public List<String> ordinalSuffixes = Arrays.asList("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th");
	
	@ConfigOptions(name = "calendar.today.item")
	public Material calendarTodayItem = Material.NAME_TAG;
	@ConfigOptions(name = "calendar.today.name")
	public String calendarTodayName = ChatColor.BOLD + "" + ChatColor.GOLD + "TODAY : /month/ /day-number//ordinal/ /year/";
	@ConfigOptions(name = "calendar.days.item")
	public Material calendarDaysItem = Material.PAPER;
	@ConfigOptions(name = "calendar.days.name")
	public String calendarDaysName = "/month/ /day-number//ordinal/ /year/";
	
	@ConfigOptions(name = "messages.year")
	public String messagesYear = ChatColor.GOLD + "Happy new year !" + ChatColor.LIGHT_PURPLE + " We are in /year/ :D";
	
	public CalendarConfig(final File dataFolder) {
		super(new File(dataFolder, "calendar.yml"), Arrays.asList("####################################################### #", "              Skyoseasons Configuration                 #", " Check http://dev.bukkit.org/bukkit-plugins/skyoseasons #", "               for more informations.                   #", "####################################################### #"));
	}
	
}
