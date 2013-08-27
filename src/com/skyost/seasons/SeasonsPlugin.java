package com.skyost.seasons;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.skyost.seasons.api.Skyoseasons;
import com.skyost.seasons.api.Skyoseasons.Season;
import com.skyost.seasons.util.PluginConfig;
import com.skyost.seasons.util.Metrics;
import com.skyost.seasons.util.Metrics.Graph;

public class SeasonsPlugin extends JavaPlugin implements Listener {
	
	public static PluginConfig config;
	public static Skyoseasons api;
	
	@Override
	public void onEnable() {
		try {
			config = new PluginConfig(this);
			config.init();
			api = new Skyoseasons(config, this);
			getServer().getPluginManager().registerEvents(api, this);
			startMetrics();
			if(!(api.checkConfig())) {
				getServer().getPluginManager().disablePlugin(this);
			}
			config.PluginFile = getFile().toString();
			config.save();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		try {
			config.CurrentSeason = api.getCurrentSeason();
			config.save();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void startMetrics() {
		try {
		    Metrics metrics = new Metrics(this);
    		Graph checkUpdatesGraph = metrics.createGraph("checkUpdatesGraph");
    		checkUpdatesGraph.addPlotter(new Metrics.Plotter("Checking for Updates") {	
    			@Override
    			public int getValue() {	
    				return 1;
    			}
    			
    			@Override
    			public String getColumnName() {
    				if(config.CheckForUpdates) {
    					return "Yes";
    				}
    				else if(config.CheckForUpdates == false) {
    					return "No";
    				}
    				else {
    					return "Maybe";
    				}
    			}
    		});
    		Graph worldsNumbersGraph = metrics.createGraph("worldsNumbersGraph");
    		worldsNumbersGraph.addPlotter(new Metrics.Plotter("Worlds number") {	
    			@Override
    			public int getValue() {	
    				return config.Worlds.split(",").length;
    			}
    		});
		    metrics.start();
		}
		catch (IOException ex) {
			getLogger().log(Level.SEVERE, "[ERROR] " + ex);
		}
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		
		if(sender instanceof Player) {
	    	player = (Player) sender;
	 	}
		
		if(cmd.getName().equalsIgnoreCase("seasons")) {
			if(sender instanceof Player) {
				if(args.length == 0) {
					if(api.isWorldEnabled(player.getWorld().getName())) {
						sender.sendMessage("We are in " + api.getCurrentSeason().toString().toLowerCase() + ".");
					}
					else {
						sender.sendMessage(ChatColor.RED + "This plugin is disabled in this world !");
					}
				}
				else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("set")) {
						if(player.hasPermission("skyoseasons.set")) {
							if(api.isWorldEnabled(player.getWorld().getName())) {
								switch(args[1].toUpperCase()) {
									case "SPRING":
										api.setCurrentSeason(player.getWorld(), Season.SPRING);
										break;
									case "SUMMER":
										api.setCurrentSeason(player.getWorld(), Season.SUMMER);
										break;
									case "AUTUMN":
										api.setCurrentSeason(player.getWorld(), Season.AUTUMN);
										break;
									case "WINTER":
										api.setCurrentSeason(player.getWorld(), Season.WINTER);
										break;
									default:
										return false;
								}
							}
							else {
								sender.sendMessage(ChatColor.RED + "This plugin is disabled in this world !");
							}
						}
						else {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this !");
						}
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "[Skyoseasons] Please do this from the game !");
			}
		}
		return true;
	}
}
