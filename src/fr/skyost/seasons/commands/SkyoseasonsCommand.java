package fr.skyost.seasons.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.Joiner;

import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.utils.Utils;

public class SkyoseasonsCommand extends SubCommandsExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
		if(args.length < 1) {
			int maxWidth = sender instanceof Player ? ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH : 80;
			final PluginDescriptionFile description = SkyoseasonsAPI.getPlugin().getDescription();
			sender.sendMessage(Utils.centerText(ChatColor.DARK_PURPLE + "SKY" + ChatColor.YELLOW + "OS" + ChatColor.GOLD + "EAS" + ChatColor.GRAY + "ONS", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.DARK_RED + "v" + description.getVersion(), maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.AQUA + "By " + Joiner.on(' ').join(description.getAuthors()) + ".", maxWidth));
			sender.sendMessage(Utils.centerText(ChatColor.RED + " " + description.getWebsite(), maxWidth));
			sender.sendMessage("");
			sender.sendMessage(Utils.centerText(ChatColor.BOLD + "Commands :", maxWidth));
			sender.sendMessage(ChatColor.GOLD + "[1] /calendar");
			sender.sendMessage(ChatColor.GOLD + "[2] /skyoseasons day <new-day>");
			sender.sendMessage(ChatColor.GOLD + "[3] /skyoseasons month <new-month>");
			sender.sendMessage(ChatColor.GOLD + "[4] /skyoseasons season <new-season>");
			sender.sendMessage(ChatColor.GOLD + "[5] /skyoseasons season-month <new-season-month>");
			sender.sendMessage(ChatColor.GOLD + "[6] /skyoseasons year <new-year>");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "[1] Opens the calendar.");
			sender.sendMessage(ChatColor.AQUA + "[2] Allows you to check / set the current day of month.");
			sender.sendMessage(ChatColor.AQUA + "[3] Allows you to check / set the current month.");
			sender.sendMessage(ChatColor.AQUA + "[4] Allows you to check / set the current season.");
			sender.sendMessage(ChatColor.AQUA + "[5] Allows you to check / set the current month of season.");
			sender.sendMessage(ChatColor.AQUA + "[6] Allows you to check / set the current year.");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN + "The above list is scrollable.");
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}
	
}