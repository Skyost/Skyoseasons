package fr.skyost.seasons.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;
import fr.skyost.seasons.events.calendar.DayChangeEvent;
import fr.skyost.seasons.events.calendar.MonthChangeEvent;
import fr.skyost.seasons.events.calendar.SeasonChangeEvent;
import fr.skyost.seasons.events.calendar.YearChangeEvent;
import fr.skyost.seasons.utils.Utils;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class SkyoseasonsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		if(args.length < 1) {
			final int maxWidth = player == null ? ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH : 80;
			final PluginDescriptionFile description = Skyoseasons.instance.getDescription();
			sender.sendMessage(Utils.centerText(ChatColor.DARK_PURPLE + "SKY" + ChatColor.YELLOW + "OS" + ChatColor.GOLD + "EAS" + ChatColor.GRAY + "ONS", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.DARK_RED + "v" + description.getVersion(), maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.AQUA + "By " + Joiner.on(' ').join(description.getAuthors()) + ".", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.DARK_RED + description.getWebsite(), maxWidth));
			sender.sendMessage("");
			sender.sendMessage(Utils.centerText(ChatColor.BOLD + "Commands :", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.GOLD + "[1] /calendar", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.GOLD + "[2] /skyoseasons [day|month|season|season-month|year]", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.GOLD + "[2] <new day/month/season/season-month/year>", maxWidth));
			sender.sendMessage("");
			sender.sendMessage(Utils.centerText(ChatColor.AQUA + "[1] View the calendar.", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.AQUA + "[2] Main command of the plugin.", maxWidth));
			return true;
		}
		if(player == null) {
			sender.sendMessage(ChatColor.DARK_RED + "You must perform this command from the game, sorry !");
			sender.sendMessage(ChatColor.RED + "But you can use /calendar from the console.");
			return true;
		}
		final SeasonWorld world = Skyoseasons.worlds.get(player.getWorld().getName());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(Skyoseasons.worlds.keySet()));
		}
		switch(args[0].toLowerCase()) {
		case "day":
			if(args.length < 2) {
				if(!sender.hasPermission("skyoseasons.calendar")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + String.valueOf(world.day) + ".");
			}
			else {
				if(!sender.hasPermission("skyoseasons.setday")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				if(!CharMatcher.DIGIT.matchesAllOf(args[1])) {
					sender.sendMessage(ChatColor.RED + "/skyoseasons day <new-day>.");
					return true;
				}
				final int newDay = Integer.parseInt(args[1]);
				if(newDay < 1 || newDay > world.month.days) {
					sender.sendMessage(ChatColor.RED + "1 -> " + world.month.days);
					return true;
				}
				Bukkit.getPluginManager().callEvent(new DayChangeEvent(world, world.day, newDay, ModificationCause.PLAYER));
				sender.sendMessage(ChatColor.GOLD + "Done !");
			}
			break;
		case "month":
			if(args.length < 2) {
				if(!sender.hasPermission("skyoseasons.calendar")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + "[" + world.month.number + "] " + world.month.name + ".");
			}
			else {
				if(!sender.hasPermission("skyoseasons.setmonth")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				final String monthName;
				if(!CharMatcher.DIGIT.matchesAllOf(args[1])) {
					monthName = (String)Utils.fromJson(Skyoseasons.calendar.Months.get(args[1])).get("Name");
					if(monthName == null) {
						final StringBuilder builder = new StringBuilder();
						for(int i = 1; !(i > Skyoseasons.calendar.Months.size()); i++) {
							builder.append("\n[" + i + "] " + Utils.fromJson(Skyoseasons.calendar.Months.get(String.valueOf(i))).get("Name"));
						}
						sender.sendMessage(ChatColor.RED + "Months :\n" + builder.toString());
						return true;
					}
				}
				else {
					monthName = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
				}
				final Month month = Skyoseasons.months.get(monthName);
				if(month == null) {
					final StringBuilder builder = new StringBuilder();
					for(int i = 1; !(i > Skyoseasons.calendar.Months.size()); i++) {
						builder.append("\n[" + i + "] " + Utils.fromJson(Skyoseasons.calendar.Months.get(String.valueOf(i))).get("Name"));
					}
					sender.sendMessage(ChatColor.RED + "Months :\n" + builder.toString());
					return true;
				}
				Bukkit.getPluginManager().callEvent(new MonthChangeEvent(world, world.month, month, world.season.monthsMessage.replaceAll("/month/", month.name), ModificationCause.PLAYER));
			}
			break;
		case "season":
			if(args.length < 2) {
				if(!sender.hasPermission("skyoseasons.calendar")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + world.season.name + ".");
			}
			else {
				if(!sender.hasPermission("skyoseasons.setseason")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				final Season season = Skyoseasons.seasons.get(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
				if(season == null) {
					sender.sendMessage(ChatColor.RED + "Seasons :\n" + Joiner.on('\n').join(Skyoseasons.seasons.keySet()));
					return true;
				}
				Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(world, world.season, season, season.message, ModificationCause.PLAYER));
			}
			break;
		case "season-month":
			if(args.length < 2) {
				if(!sender.hasPermission("skyoseasons.calendar")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + String.valueOf(world.seasonMonth) + ".");
			}
			else {
				if(!sender.hasPermission("skyoseasons.setseasonmonth")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				if(!CharMatcher.DIGIT.matchesAllOf(args[1])) {
					sender.sendMessage(ChatColor.RED + "/skyoseasons season-month <new-season-month>.");
					return true;
				}
				final int newMonth = Integer.parseInt(args[1]);
				if(newMonth < 1 || newMonth > world.season.months) {
					sender.sendMessage(ChatColor.RED + "1 -> " + world.season.months);
					return true;
				}
				world.seasonMonth = newMonth;
				sender.sendMessage(ChatColor.GOLD + "Done !");
			}
			break;
		case "year":
			if(args.length < 2) {
				if(!sender.hasPermission("skyoseasons.calendar")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + String.valueOf(world.year) + ".");
			}
			else {
				if(!sender.hasPermission("skyoseasons.setyear")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					return true;
				}
				if(!CharMatcher.DIGIT.matchesAllOf(args[1])) {
					sender.sendMessage(ChatColor.RED + "/skyoseasons year <new-year>.");
					return true;
				}
				Bukkit.getPluginManager().callEvent(new YearChangeEvent(world, world.year, Integer.parseInt(args[1]), Skyoseasons.calendar.Messages_Year.replaceAll("/year/", args[1]), ModificationCause.PLAYER));
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
}
