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

public class SkyoseasonsDay implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"day"};
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
		return "<new-day>";
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(((Player)sender).getWorld());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.GOLD + String.valueOf(world.day) + ".");
		}
		else {
			if(!sender.hasPermission("skyoseasons.calendar.setday")) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			if(!CharMatcher.DIGIT.matchesAllOf(args[0])) {
				sender.sendMessage(ChatColor.RED + "/skyoseasons day <new-day>.");
				return true;
			}
			final int newDay = Integer.parseInt(args[0]);
			if(newDay < 1 || newDay > world.month.days) {
				sender.sendMessage(ChatColor.RED + "1 -> " + world.month.days);
				return true;
			}
			sender.sendMessage(SkyoseasonsAPI.callDayChange(world, newDay, ModificationCause.PLAYER).isCancelled() ? ChatColor.RED + "Cancelled by a plugin !" : ChatColor.GOLD + "Done !");
		}
		return true;
	}

}