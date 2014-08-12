package fr.skyost.seasons.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;

public class CalendarCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		if(!sender.hasPermission("skyoseasons.calendar")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			return true;
		}
		if(args.length > 1) {
			final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(Joiner.on(' ').join(args));
			if(world == null) {
				sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
				return true;
			}
			if(player == null) {
				sender.sendMessage(ChatColor.AQUA + world.calendar.getName() + " :");
				for(final ItemStack item : world.calendar.getContents()) {
					if(item != null) {
						sender.sendMessage(item.getItemMeta().getDisplayName());
					}
				}
			}
			else {
				player.openInventory(world.calendar);
			}
		}
		else {
			if(player == null) {
				return false;
			}
			final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(player.getWorld());
			if(world != null) {
				player.openInventory(world.calendar);
			}
			else {
				sender.sendMessage(ChatColor.RED + "Enabled worlds :\n" + Joiner.on('\n').join(SkyoseasonsAPI.getSeasonWorldsNames()));
			}
		}
		return true;
	}
	
}
