package com.skyost.seasons.api;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

import com.skyost.seasons.config.AutumnConfig;
import com.skyost.seasons.config.SpringConfig;
import com.skyost.seasons.config.SummerConfig;
import com.skyost.seasons.config.WinterConfig;
import com.skyost.seasons.util.PluginConfig;
import com.skyost.seasons.util.Updater;


/**
 * Skyoseasons API, thanks to Likaos for his TimeManager - http://bit.ly/1dLJjz7.
 * 
 * @author Skyost.
 * @version 0.1
 */

@SuppressWarnings("static-access")
public class Skyoseasons implements Listener {
	
	private static Plugin plugin;
	private static SpringConfig spring;
	private static SummerConfig summer;
	private static AutumnConfig autumn;
	private static WinterConfig winter;
	private boolean updaterStarted = false;
	private Integer taskId;
	private String worlds = " ";
	private Timer seasonsTime;
	private Season currentSeason;
	private PluginConfig config;
	
	public Skyoseasons(PluginConfig config, Plugin plugin) {
		try {
			this.config = config;
			this.plugin = plugin;
			currentSeason = config.CurrentSeason;
			spring = new SpringConfig(plugin);
			spring.init();
			summer = new SummerConfig(plugin);
			summer.init();
			autumn = new AutumnConfig(plugin);
			autumn.init();
			winter = new WinterConfig(plugin);
			winter.init();
			String[] s = config.Worlds.split(",");
			seasonsTime = new Timer();
			for(int i = 0; i != s.length; i++) {
				if(Bukkit.getServer().getWorld(s[i]) != null) {
					World world = Bukkit.getServer().getWorld(s[i]);
					worlds = worlds + "," + s[i];
					setCurrentSeason(world, currentSeason);
					refreshCurrentTime(world);
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Check if nothing was wrong with config.
	 * 
	 * @return true If all is correct, false if an error occured.
	 */
	
	public boolean checkConfig() {
		if(config.SnowmeltMultiplicator <= 0) {
			System.out.println("The snowmelt multiplicator must be greater than zero !");
			return false;
		}
		if(spring.SeasonLength <= 0 || summer.SeasonLength <= 0 || autumn.SeasonLength <= 0 || winter.SeasonLength <= 0) {
			System.out.println("The seasons length must be greater than zero !");
			return false;
		}
		if(spring.DayLength <= 0 || spring.NightLength <= 0 || summer.DayLength <= 0 || summer.NightLength <= 0 || autumn.DayLength <= 0 || autumn.NightLength <= 0 || winter.DayLength <= 0 || winter.NightLength <= 0) {
			System.out.println("The days / nights length must be greater than zero !");
			return false;
		}
		return true;
	}
	
	@EventHandler
	private void onChunkLoad(ChunkLoadEvent e) {
		if(worlds.toUpperCase().contains(e.getWorld().getName().toUpperCase())) {
			Chunk chunk = e.getChunk();
			switch(currentSeason) {
			case SPRING:
				setChunkBiome(chunk, Biome.PLAINS, true);
				break;
			case SUMMER:
				setChunkBiome(chunk, Biome.PLAINS, true);
				break;
			case AUTUMN:
				setChunkBiome(chunk, Biome.DESERT, true);
				break;
			case WINTER:
				setChunkBiome(chunk, Biome.ICE_PLAINS, false);
				break;
			}
		}
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent e) {
		if(updaterStarted == false) {
			if(config.CheckForUpdates) {
				Timer update = new Timer();
				update.schedule(new checkForUpdates(plugin), 0, 8640000);
				updaterStarted = true;
			}
		}
		Chunk[] chunks = e.getPlayer().getWorld().getLoadedChunks();
		if(worlds.toUpperCase().contains(e.getPlayer().getWorld().getName().toUpperCase())) {
			for(int i = 0; i != chunks.length; i++)  {
				switch(currentSeason) {
				case SPRING:
					setChunkBiome(chunks[i], Biome.PLAINS, true);
					break;
				case SUMMER:
					setChunkBiome(chunks[i], Biome.PLAINS, true);
					break;
				case AUTUMN:
					setChunkBiome(chunks[i], Biome.DESERT, true);
					break;
				case WINTER:
					setChunkBiome(chunks[i], Biome.ICE_PLAINS, false);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if(worlds.toUpperCase().contains(e.getWorld().getName().toUpperCase())) {
			if(currentSeason.equals(Season.SUMMER)) {
				if(e.toWeatherState() == true) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	/**
	 * Get the current season.
	 * 
	 * @return SPRING If we are in spring, SUMMER If we are in summer, AUTUMN If we are in autumn or WINTER If we are in winter.
	 */
	
	public Season getCurrentSeason() {
		return currentSeason;
	}
	
	/**
	 * Check if the specified world is enabled.
	 * 
	 * @param world The world name.
	 * 
	 * @return true If the world is enabled, false If the world is disabled.
	 */
	
	public boolean isWorldEnabled(String world) {
		String[] s = worlds.split(",");
		for(int i = 0; i != s.length; i++) {
			if(s[i].toUpperCase().equals(world.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Set the season to a new season.
	 * 
	 * @param world The world to set the new season.
	 * @param season The new season.
	 */
	
	public void setCurrentSeason(World world, Season season) {
		if(worlds.toUpperCase().contains(world.getName().toUpperCase())) {
			seasonsTime.cancel();
			seasonsTime = new Timer();
			Chunk[] chunks = world.getLoadedChunks();
			switch(season) {
			case SPRING:
				//Event call 
	            SeasonChangeEvent springEvent = new SeasonChangeEvent(season.SPRING);
	            if(springEvent.isCancelled())
	            	return;
	            Bukkit.getPluginManager().callEvent(springEvent);
	            //Event call end
	            
				if(currentSeason.equals(season.WINTER)) {
					for(int i = 0; i < chunks.length; i++) {
					    setChunkBiome(chunks[i], Biome.PLAINS, true);
					    world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				else {
					for(int i = 0; i < chunks.length; i++) {
						setChunkBiome(chunks[i], Biome.PLAINS, false);
						world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				world.setStorm(false);
				currentSeason = season.SPRING;
				Bukkit.broadcastMessage(spring.Message);
				String s1 = spring.SeasonLength + "000";
				seasonsTime.schedule(new SeasonManager(world), Integer.parseInt(s1));
				break;
			case SUMMER:
				//Event call 
	            SeasonChangeEvent summerEvent = new SeasonChangeEvent(season.SUMMER);
	            if(summerEvent.isCancelled())
	            	return;
	            Bukkit.getPluginManager().callEvent(summerEvent);
	            //Event call end
				
				if(currentSeason.equals(season.WINTER)) {
					for(int i = 0; i < chunks.length; i++) {
					    setChunkBiome(chunks[i], Biome.PLAINS, true);
					    world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				else {
					for(int i = 0; i < chunks.length; i++) {
					    setChunkBiome(chunks[i], Biome.PLAINS, false);
					    world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				world.setStorm(false);
				currentSeason = season.SUMMER;
				Bukkit.broadcastMessage(summer.Message);
				String s2 = summer.SeasonLength + "000";
				seasonsTime.schedule(new SeasonManager(world), Integer.parseInt(s2));
				break;
			case AUTUMN:
				//Event call 
	            SeasonChangeEvent autumnEvent = new SeasonChangeEvent(season.AUTUMN);
	            if(autumnEvent.isCancelled())
	            	return;
	            Bukkit.getPluginManager().callEvent(autumnEvent);
	            //Event call end
				if(currentSeason.equals(season.WINTER)) {
					for(int i = 0; i < chunks.length; i++) {
					    setChunkBiome(chunks[i], Biome.DESERT, true);
					    world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				else {
					for(int i = 0; i < chunks.length; i++) {
					    setChunkBiome(chunks[i], Biome.DESERT, false);
					    world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
					}
				}
				world.setStorm(true);
				currentSeason = season.AUTUMN;
				Bukkit.broadcastMessage(autumn.Message);
				String s3 = autumn.SeasonLength + "000";
				seasonsTime.schedule(new SeasonManager(world), Integer.parseInt(s3));
				break;
			case WINTER:
				//Event call 
	            SeasonChangeEvent winterEvent = new SeasonChangeEvent(season.WINTER);
	            if(winterEvent.isCancelled())
	            	return;
	            Bukkit.getPluginManager().callEvent(winterEvent);
	            //Event call end
				
				for(int i = 0; i < chunks.length; i++) {
					setChunkBiome(chunks[i], Biome.ICE_PLAINS, false);
					world.refreshChunk(chunks[i].getX(), chunks[i].getZ());
				}
				world.setStorm(true);
				currentSeason = season.WINTER;
				Bukkit.broadcastMessage(winter.Message);
				String s4 = winter.SeasonLength + "000";
				seasonsTime.schedule(new SeasonManager(world), Integer.parseInt(s4));
				break;
			}
            SeasonChangeEvent seasonChangeEvent = new SeasonChangeEvent(currentSeason);
            Bukkit.getPluginManager().callEvent(seasonChangeEvent);
        	refreshCurrentTime(world);
		}
	}
	
	/**
	 * Refresh the custom time for the world.
	 * 
	 * @param world The world where you want to refresh the time.
	 */
	
	public void refreshCurrentTime(World world) {
		if(taskId != null) {
			plugin.getServer().getScheduler().cancelTask(taskId);
		}
        switch(currentSeason) {
        case SPRING:
        	taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TimeManager(world, spring.DayLength, spring.NightLength, config.TimeRefreshRate), 0, config.TimeRefreshRate);
        	break;
        case SUMMER:
        	taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TimeManager(world, summer.DayLength, summer.NightLength, config.TimeRefreshRate), 0, config.TimeRefreshRate);
        	break;
       	case AUTUMN:
       		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TimeManager(world, autumn.DayLength, autumn.NightLength, config.TimeRefreshRate), 0, config.TimeRefreshRate);
        	break;
        case WINTER:
        	taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TimeManager(world, winter.DayLength, winter.NightLength, config.TimeRefreshRate), 0, config.TimeRefreshRate);
        	break;
        }
	}
	
	/**
	 * Set the biome for a chunk.
	 * 
	 * @param chunk The chunk.
	 * @param biome The biome to set.
	 * @param removeIce If you want to remove ice and snow.
	 */
	
	public void setChunkBiome(Chunk chunk, Biome biome, boolean removeIce) {
        for(int x = 0 ; x < 16; x++) {
            for(int z = 0 ; z < 16; z++) {
                final Block block = chunk.getBlock(x, 0, z);
                block.setBiome(biome);
                if(removeIce) {
                    int lower = 60;
                    int higher = 180;
                    int fonte = new Random().nextInt(higher) + lower;
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        World world = block.getWorld();
                        Block highestBlock = world.getHighestBlockAt(block.getLocation().add(0, -1, 0));
                        if(highestBlock.getType() == Material.SNOW) {
                            highestBlock.setType(Material.AIR);
                        }
                        else if(highestBlock.getType() == Material.AIR) {
                            highestBlock = highestBlock.getRelative(0, -1, 0);
                            if(highestBlock.getType() == Material.ICE) {
                                highestBlock.setType(Material.STATIONARY_WATER);
                            }
                        }
                    }}, config.SnowmeltMultiplicator * fonte);
                }
            }
        }
    }
	
	/**
	 * Replace the biome for a chunk.
	 * 
	 * @param chunk The chunk.
	 * @param oldBiome The old biome.
	 * @param newBiome The new biome.
	 * @param removeIce If you want to remove ice and snow.
	 */
	
	public void replaceChunkBiome(Chunk chunk, Biome oldBiome, Biome newBiome, boolean removeIce) {
        for(int x = 0 ; x < 16; x++) {
            for(int z = 0 ; z < 16; z++) {
                if(chunk.getBlock(x, 0, z).getBiome().equals(oldBiome)) {
                	final Block block = chunk.getBlock(x, 0, z);
                	block.setBiome(newBiome);
                	if(removeIce) {
                        int lower = 60;
                        int higher = 180;
                        int fonte = new Random().nextInt(higher) + lower;
                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            World world = block.getWorld();
                            Block highestBlock = world.getHighestBlockAt(block.getLocation().add(0,-1,0));
                            if(highestBlock.getType() == Material.SNOW) {
                                highestBlock.setType(Material.AIR);
                            }
                            else if(highestBlock.getType() == Material.AIR) {
                                highestBlock = highestBlock.getRelative(0, -1, 0);
                                if (highestBlock.getType() == Material.ICE) {
                                    highestBlock.setType(Material.STATIONARY_WATER);
                                }
                            }
                        }}, config.SnowmeltMultiplicator * fonte);
                    }
                }
            }
        }
	}
	
	/**
	 * Get the previous season of the current season.
	 * 
	 * @return SPRING If the previous season is spring, SUMMER If the previous season is summer, AUTUMN If the previous season is autumn or WINTER If the previous season is winter.
	 */
	
	public Season getPreviousSeason() {
		switch(currentSeason) {
		case SPRING:
			return Season.WINTER;
		case SUMMER:
			return Season.SPRING;
		case AUTUMN:
			return Season.SUMMER;
		case WINTER:
			return Season.AUTUMN;
		default:
			return null;
		}
	}
	
	/**
	 * Get the next season of the current season.
	 * 
	 * @return SPRING If the next season is spring, SUMMER If the next season is summer, AUTUMN If the next season is autumn or WINTER If the next season is winter.
	 */
	
	public Season getNextSeason() {
		switch(currentSeason) {
		case SPRING:
			return Season.SUMMER;
		case SUMMER:
			return Season.AUTUMN;
		case AUTUMN:
			return Season.WINTER;
		case WINTER:
			return Season.SPRING;
		default:
			return null;
		}
	}
	
	private class SeasonManager extends TimerTask {
    	
    	private final World world;
    	
    	private SeasonManager(World world) {
    		this.world = world;
    	}
     
        public void run() {
	        switch(currentSeason) {
	        case SPRING:
	        	setCurrentSeason(world, Season.SUMMER);
	        	break;
	        case SUMMER:
	        	setCurrentSeason(world, Season.AUTUMN);
	        	break;
	        case AUTUMN:
	        	setCurrentSeason(world, Season.WINTER);
	        	break;
	        case WINTER:
	        	setCurrentSeason(world, Season.SPRING);
	        	break;
	        }
        }
    }
	
	public class TimeManager implements Runnable {

		protected World world = null;
		protected float dayLenght;
		protected float nightLenght;
		protected float oldRealTime;
		protected int refreshRate;
		protected float timeBalance;
		protected float newRealTime;
		protected long estimatedRealTime;

		public TimeManager(World targetWorld, float dayLenght, float nightLenght, int refreshRate) {
			this.world = targetWorld;
			this.oldRealTime = world.getTime();
			this.dayLenght = dayLenght;
			this.nightLenght = nightLenght;
			this.refreshRate = refreshRate;
		}

		@Override
		public void run() {
			if(world.getTime() != estimatedRealTime) {
				newRealTime = world.getTime();
			}
			if(newRealTime > 12000) {
				timeBalance = refreshRate * (12000 / (nightLenght * 20));
			}
			else {
				timeBalance = refreshRate * (12000 / (dayLenght * 20));
			}
			newRealTime += timeBalance;
			world.setTime((long) newRealTime);
			oldRealTime = newRealTime;
			estimatedRealTime = (long) newRealTime + refreshRate;
		}
	}
	
	private class checkForUpdates extends TimerTask {
		
		Plugin plugin;
		
		private checkForUpdates(Plugin plugin) {
			this.plugin = plugin;
		}
     
        @SuppressWarnings("incomplete-switch")
		public void run() {
			Updater updater = new Updater(plugin, "skyoseasons", new File(config.PluginFile), Updater.UpdateType.DEFAULT, true);
    		Updater.UpdateResult result = updater.getResult();
           	Player[] ops = Bukkit.getServer().getOnlinePlayers();
    	    switch(result) {
    	    case SUCCESS:
    	    	for(int i = 0; i < ops.length; i++) {
					if(ops[i].isOp()) {
						ops[i].sendMessage(ChatColor.GREEN + "[Skyoseasons] Update found: The update " + updater.getLatestVersionString() + " has been downloaded, so you just have to do a simple reload.");
					}
				}
    	       	break;
    	    case FAIL_DBO:
    	        for(int i = 0; i < ops.length; i++) {
					if(ops[i].isOp()) {
							ops[i].sendMessage(ChatColor.RED + "[Skyoseasons] Download Failed: The updater found an update, but was unable to download it.");
					}
				}
    	        break;
    	    case FAIL_DOWNLOAD:
    	    	for(int i = 0; i < ops.length; i++) {
    	    		if(ops[i].isOp()) {
						ops[i].sendMessage(ChatColor.GREEN + "[Skyoseasons] Update found: There was an update found : " + updater.getLatestVersionString() + ".");
					}
				}
    	        break;
    	    }
        }
	}
}
