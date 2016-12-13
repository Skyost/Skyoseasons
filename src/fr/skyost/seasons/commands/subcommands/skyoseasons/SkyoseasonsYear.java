package fr.skyost.seasons.commands.subcommands.skyoseasons;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;

public class SkyoseasonsYear implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"year"};
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
		return "<new-year>";
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(((Player)sender).getWorld());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.GOLD + String.valueOf(world.year) + ".");
		}
		else {
			if(!sender.hasPermission("skyoseasons.calendar.setyear")) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
				return true;
			}
			if(!CharMatcher.DIGIT.matchesAllOf(args[0])) {
				sender.sendMessage(ChatColor.RED + "/skyoseasons year <new-year>.");
				return true;
			}
			if(SkyoseasonsAPI.callYearChange(world, Integer.parseInt(args[0]), ModificationCause.PLAYER).isCancelled()) {
				sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
			}
		}
		return true;
	}

}