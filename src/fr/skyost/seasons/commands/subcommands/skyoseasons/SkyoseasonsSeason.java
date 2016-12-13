package fr.skyost.seasons.commands.subcommands.skyoseasons;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;

public class SkyoseasonsSeason implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"season"};
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
		return "<new-season>";
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(((Player)sender).getWorld());
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.GOLD + world.season.name + ".");
		}
		else {
			if(!sender.hasPermission("skyoseasons.calendar.setseason")) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			final Season season = SkyoseasonsAPI.getSeason(Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length)));
			if(season == null) {
				sender.sendMessage(ChatColor.RED + "Seasons :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonsNames()));
				return true;
			}
			if(SkyoseasonsAPI.callSeasonChange(world, season, ModificationCause.PLAYER).isCancelled()) {
				sender.sendMessage(ChatColor.RED + "Cancelled by a plugin.");
			}
		}
		return true;
	}

}