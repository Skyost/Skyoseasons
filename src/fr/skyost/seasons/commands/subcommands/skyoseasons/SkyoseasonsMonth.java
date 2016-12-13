package fr.skyost.seasons.commands.subcommands.skyoseasons;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;

public class SkyoseasonsMonth implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"month"};
	}

	@Override
	public final boolean mustBePlayer() {
		return true;
	}

	@Override
	public final String getPermission() {
		return "skyoseasons.calendar";
	}

	@Override
	public final int getMinArgsLength() {
		return 0;
	}

	@Override
	public final String getUsage() {
		return "<new-month>";
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(((Player)sender).getWorld());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.GOLD + "[" + world.month.number + "] " + world.month.name + ".");
		}
		else {
			if(!sender.hasPermission("skyoseasons.calendar.setmonth")) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			final Month month = CharMatcher.DIGIT.matchesAllOf(args[0]) ? SkyoseasonsAPI.getMonth(Integer.parseInt(args[0])) : SkyoseasonsAPI.getMonth(Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length)));
			if(month == null) {
				sender.sendMessage(ChatColor.RED + "Months :\n" + getMonths());
				return true;
			}
			if(SkyoseasonsAPI.callMonthChange(world, month, ModificationCause.PLAYER).isCancelled()) {
				sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
			}
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