package fr.skyost.seasons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.seasons.tasks.SnowMelt;
import fr.skyost.seasons.tasks.TimeControl;
import fr.skyost.seasons.utils.Utils;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

public class SeasonWorld {
	
	public final World world;
	
	public int day;
	public Month month;
	public Season season;
	public int seasonMonth;
	public int year;
	
	public Inventory calendar;
	
	public final HashMap<Integer, BukkitRunnable> tasks = new HashMap<Integer, BukkitRunnable>();
	
	public SeasonWorld(final World world, final WorldConfig config) {
		this(world, config.day, Skyoseasons.months.getByIndex(config.month - 1), Skyoseasons.seasons.get(config.season), config.seasonMonth, config.year);
	}
	
	public SeasonWorld(final World world) {
		this(world, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Skyoseasons.months.getByIndex(0), null, 1, Calendar.getInstance().get(Calendar.YEAR));
	}
	
	private SeasonWorld(final World world, final int day, final Month month, final Season season, final int seasonMonth, final int year) {
		this.world = world;
		this.day = day;
		this.month = month;
		this.season = season;
		this.seasonMonth = seasonMonth;
		this.year = year;
		world.setTime(0L);
		calendar = buildCalendar(month);
	}
	
	public final void updateCalendar(final int prevDay, final int newDay) {
		ItemStack item = calendar.getItem(prevDay - 1);
		item.setType(Skyoseasons.calendar.calendarDaysItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Skyoseasons.calendar.calendarDaysName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day - 1)).replace("/ordinal/", Utils.getOrdinalSuffix(day - 1)).replace("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
		item = calendar.getItem(newDay - 1);
		item.setType(Skyoseasons.calendar.calendarTodayItem);
		meta = item.getItemMeta();
		meta.setDisplayName(Skyoseasons.calendar.calendarTodayName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day)).replace("/ordinal/", Utils.getOrdinalSuffix(day)).replace("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
	}
	
	public final void updateCalendarForViewers() {
		final List<HumanEntity> viewers = new ArrayList<HumanEntity>(calendar.getViewers());
		for(final HumanEntity viewer : viewers) {
			viewer.closeInventory();
		}
		calendar = buildCalendar(month);
		for(final HumanEntity viewer : viewers) {
			viewer.openInventory(calendar);
		}
	}
	
	public final Inventory buildCalendar(final Month month) {
		final Inventory menu = Bukkit.createInventory(null, Utils.round(month.days, 9), month.name + " " + year);
		for(int i = 1; i <= month.days; i++) {
			final ItemStack item;
			final ItemMeta meta;
			if(i == day) {
				item = new ItemStack(Skyoseasons.calendar.calendarTodayItem);
				meta = item.getItemMeta();
				meta.setDisplayName(Skyoseasons.calendar.calendarTodayName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day)).replace("/ordinal/", Utils.getOrdinalSuffix(day)).replace("/year/", String.valueOf(year)));
			}
			else {
				item = new ItemStack(Skyoseasons.calendar.calendarDaysItem);
				meta = item.getItemMeta();
				meta.setDisplayName(Skyoseasons.calendar.calendarDaysName.replace("/month/", month.name).replace("/day-number/", String.valueOf(i)).replace("/ordinal/", Utils.getOrdinalSuffix(i)).replace("/year/", String.valueOf(year)));
			}
			item.setItemMeta(meta);
			menu.addItem(item);
		}
		return menu;
	}
	
	public final void setCurrentSeason(final Season season, final String message) {
		setCurrentSeason(season, message, 1);
	}
	
	public final void setCurrentSeason(final Season season, final String message, final int seasonMonth) {
		final TimeControl timeControl = (TimeControl)tasks.get(0);
		if(timeControl != null) {
			timeControl.cancel();
			tasks.remove(0);
		}
		this.season = season;
		SnowMelt snowMelt = (SnowMelt)tasks.get(1);
		if(!season.snowMelt && snowMelt != null) {
			snowMelt.cancel();
			tasks.remove(1);
		}
		this.seasonMonth = seasonMonth;
		final AbstractProtocolLibHook protocolLibHook = SkyoseasonsAPI.getProtocolLibHook();
		if(protocolLibHook != null) {
			protocolLibHook.setDefaultBiome(season.defaultBiome);
		}
		final List<Location> snowBlocks = handleBlocks(world.getLoadedChunks());
		if(!season.snowMelt) {
			return;
		}
		if(snowBlocks.size() != 0) {
			snowMelt = (SnowMelt)tasks.get(1);
			if(snowMelt == null) {
				snowMelt = new SnowMelt(this, snowBlocks);
				snowMelt.runTaskTimer(SkyoseasonsAPI.getPlugin(), 20L, new Random().nextInt(SkyoseasonsAPI.getConfig().snowMeltMaxDelay) + 1);
				tasks.put(1, snowMelt);
				return;
			}
			snowMelt.addBlocks(snowBlocks);
		}
		world.setStorm(season.alwaysRain);
		for(final Player player : world.getPlayers()) {
			if(message != null) {
				player.sendMessage(season.message);
			}
			if(season.resourcePackUrl != null) {
				player.setResourcePack(season.resourcePackUrl);
			}
		}
		Skyoseasons.logsManager.log(season.message, Level.INFO, world);
		final TimeControl task = new TimeControl(this, season.daylength, season.nightLength, Skyoseasons.config.refreshTime);
		task.runTaskTimer(Skyoseasons.instance, Skyoseasons.config.refreshTime, Skyoseasons.config.refreshTime);
		tasks.put(0, task);
	}
	
	public final List<Location> handleBlocks(final Chunk... chunks) {
		boolean needToRefresh = false;
		final List<Location> snowBlocks = new ArrayList<Location>();
		for(final Chunk chunk : chunks) {
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					final Block block = chunk.getBlock(x, 0, z);
					if(Skyoseasons.protocolLib == null) {
						final Biome biome = season.replacements.get(block.getBiome());
						block.setBiome(biome == null ? season.defaultBiome : biome);
						needToRefresh = true;
					}
					if(season.snowMelt) {
						Block highestBlock = world.getHighestBlockAt(block.getLocation());
						if(block.getY() < Skyoseasons.config.snowEternalY) {
							final Material type = highestBlock.getType();
							if(type == Material.SNOW) {
								snowBlocks.add(highestBlock.getLocation());
							}
							else if(type == Material.AIR) {
								highestBlock = highestBlock.getRelative(0, -1, 0);
								if(highestBlock.getType() == Material.ICE) {
									snowBlocks.add(highestBlock.getLocation());
								}
							}
						}
					}
				}
			}
		}
		if(needToRefresh) {
			new Thread() {
				
				@Override
				public final void run() {
					final AbstractProtocolLibHook hook = SkyoseasonsAPI.getProtocolLibHook();
					if(hook == null) {
						for(final Chunk chunk : chunks) {
							world.refreshChunk(chunk.getX(), chunk.getZ());
						}
					}
					else {
						for(final Chunk chunk : chunks) {
							hook.refreshChunk(world, chunk);
						}
					}
				}
				
			}.start();
		}
		return snowBlocks;
	}
	
}