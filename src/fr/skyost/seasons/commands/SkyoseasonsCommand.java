package fr.skyost.seasons.commands;

import java.util.Arrays;

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
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;
import fr.skyost.seasons.utils.Utils;

public class SkyoseasonsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		if(args.length < 1) {
			int maxWidth = player == null ? ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH : 80;
			final PluginDescriptionFile description = SkyoseasonsAPI.getPlugin().getDescription();
			sender.sendMessage(Utils.centerText(ChatColor.DARK_PURPLE + "SKY" + ChatColor.YELLOW + "OS" + ChatColor.GOLD + "EAS" + ChatColor.GRAY + "ONS", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.DARK_RED + "v" + description.getVersion(), maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.AQUA + "By " + Joiner.on(' ').join(description.getAuthors()) + ".", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.DARK_RED + description.getWebsite(), maxWidth));
			sender.sendMessage("");
			sender.sendMessage(Utils.centerText(ChatColor.BOLD + "Commands :", maxWidth));
			sender.sendMessage(ChatColor.GOLD + "[1]" + Utils.centerText("/calendar", --maxWidth - 2));
			sender.sendMessage(ChatColor.GOLD + "[2]" + Utils.centerText("/skyoseasons [day [new-day]|month [month]|", --maxWidth - 1));
			sender.sendMessage(ChatColor.GOLD + "[2]" + Utils.centerText("season [season]|season-month [season-month]|year [year]]", maxWidth));
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "[1]" + Utils.centerText("View the calendar.", maxWidth));
			sender.sendMessage(ChatColor.AQUA + "[2]" + Utils.centerText("Main command of the plugin.", maxWidth));
			return true;
		}
		if(player == null) {
			sender.sendMessage(ChatColor.RED + "You must perform this command from the game, sorry !");
			sender.sendMessage(ChatColor.RED + "But you can use /calendar from the console.");
			return true;
		}
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(player.getWorld());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
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
				sender.sendMessage(SkyoseasonsAPI.callDayChange(world, newDay, ModificationCause.PLAYER).isCancelled() ? ChatColor.RED + "Cancelled by a plugin !" : ChatColor.GOLD + "Done !");
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
				final Month month = CharMatcher.DIGIT.matchesAllOf(args[1]) ? SkyoseasonsAPI.getMonth(Integer.parseInt(args[1])) : SkyoseasonsAPI.getMonth(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
				if(month == null) {
					sender.sendMessage(ChatColor.RED + "Months :\n" + getMonths());
					return true;
				}
				if(SkyoseasonsAPI.callMonthChange(world, month, ModificationCause.PLAYER).isCancelled()) {
					sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
				}
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
				final Season season = SkyoseasonsAPI.getSeason(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
				if(season == null) {
					sender.sendMessage(ChatColor.RED + "Seasons :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonsNames()));
					return true;
				}
				if(SkyoseasonsAPI.callSeasonChange(world, season, ModificationCause.PLAYER).isCancelled()) {
					sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
				}
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
				if(SkyoseasonsAPI.callYearChange(world, Integer.parseInt(args[1]), ModificationCause.PLAYER).isCancelled()) {
					sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
				}
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
	private final String getMonths() {
		final StringBuilder builder = new StringBuilder();
		final Month[] months = SkyoseasonsAPI.getMonths();
		for(int i = 1; i <= months.length; i++) {
			builder.append("\n[" + i + "] " + SkyoseasonsAPI.getMonth(i).name);
		}
		return builder.toString();
	}
	
}
