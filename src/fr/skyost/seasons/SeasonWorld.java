package fr.skyost.seasons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
import org.bukkit.scheduler.BukkitScheduler;

import fr.skyost.seasons.tasks.CancelTasks;
import fr.skyost.seasons.tasks.SnowMelt;
import fr.skyost.seasons.tasks.TimeControl;
import fr.skyost.seasons.utils.Utils;

public class SeasonWorld {
	
	public final World world;
	public Season season;
	public int seasonMonth;
	
	public int day;
	public Month month;
	public int year;
	
	public Inventory calendar;
	
	public final int[] tasks = new int[2];
	public final List<BukkitRunnable> snowMelt = new ArrayList<BukkitRunnable>();
	
	public SeasonWorld(final World world) {
		this.world = world;
		this.day = 1;
		this.month = Skyoseasons.months.entrySet().iterator().next().getValue();
		this.year = 2000;
		world.setTime(0L);
		final List<String> seasons = new ArrayList<String>(Skyoseasons.seasons.keySet());
		setCurrentSeason(Skyoseasons.seasons.get(seasons.get(new Random().nextInt(seasons.size()))), null);
		calendar = buildCalendar(month);
	}
	
	public SeasonWorld(final World world, final Season season, final int seasonMonth, final int day, final Month month, final int year) {
		this.world = world;
		this.day = day;
		this.month = month;
		this.year = year;
		world.setTime(0L);
		setCurrentSeason(season, null, seasonMonth);
		calendar = buildCalendar(month);
	}
	
	public final void updateCalendar(final int prevDay, final int newDay) {
		ItemStack item = calendar.getItem(prevDay - 1);
		item.setType(Skyoseasons.calendar.calendarDaysItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Skyoseasons.calendar.calendarDaysName.replaceAll("/month/", month.name).replaceAll("/day-number/", String.valueOf(day - 1)).replaceAll("/ordinal/", Utils.getOrdinalSuffix(day - 1)).replaceAll("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
		item = calendar.getItem(newDay - 1);
		item.setType(Skyoseasons.calendar.calendarTodayItem);
		meta = item.getItemMeta();
		meta.setDisplayName(Skyoseasons.calendar.calendarTodayName.replaceAll("/month/", month.name).replaceAll("/day-number/", String.valueOf(day)).replaceAll("/ordinal/", Utils.getOrdinalSuffix(day)).replaceAll("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
	}
	
	public final Inventory buildCalendar(final Month month) {
		final Inventory menu = Bukkit.createInventory(null, Utils.round(month.days, 9), month.name + " " + year);
		for(int i = 1; !(i > month.days); i++) {
			final ItemStack item;
			final ItemMeta meta;
			if(i == day) {
				item = new ItemStack(Skyoseasons.calendar.calendarTodayItem);
				meta = item.getItemMeta();
				meta.setDisplayName(Skyoseasons.calendar.calendarTodayName.replaceAll("/month/", month.name).replaceAll("/day-number/", String.valueOf(day)).replaceAll("/ordinal/", Utils.getOrdinalSuffix(day)).replaceAll("/year/", String.valueOf(year)));
			}
			else {
				item = new ItemStack(Skyoseasons.calendar.calendarDaysItem);
				meta = item.getItemMeta();
				meta.setDisplayName(Skyoseasons.calendar.calendarDaysName.replaceAll("/month/", month.name).replaceAll("/day-number/", String.valueOf(i)).replaceAll("/ordinal/", Utils.getOrdinalSuffix(i)).replaceAll("/year/", String.valueOf(year)));
			}
			item.setItemMeta(meta);
			menu.addItem(item);
		}
		return menu;
	}
	
	public final void buildCalendar() {
		final List<HumanEntity> viewers = new ArrayList<HumanEntity>(calendar.getViewers());
		for(final HumanEntity viewer : viewers) {
			viewer.closeInventory();
		}
		calendar = buildCalendar(month);
		for(final HumanEntity viewer : viewers) {
			viewer.openInventory(calendar);
		}
	}
	
	public final void setCurrentSeason(final Season season, final String message) {
		setCurrentSeason(season, message, 1);
	}
	
	public final void setCurrentSeason(final Season season, final String message, final int seasonMonth) {
		try {
			final BukkitScheduler scheduler = Bukkit.getScheduler();
			if(tasks[0] != -1) {
				scheduler.cancelTask(tasks[0]);
			}
			this.season = season;
			if(!season.snowMelt && snowMelt.size() != 0) {
				tasks[1] = scheduler.scheduleSyncDelayedTask(Skyoseasons.instance, new CancelTasks(this));
			}
			this.seasonMonth = seasonMonth;
			final Random random = new Random();
			for(final Chunk chunk : world.getLoadedChunks()) {
				for(int x = 0; x < 16; x++) {
					for(int z = 0; z < 16; z++) {
						final Block block = chunk.getBlock(x, 0, z);
						if(Skyoseasons.protocolLib == null) {
							final Biome biome = season.replacements.get(block.getBiome());
							block.setBiome(biome == null ? season.defaultBiome : biome);
						}
						if(season.snowMelt) {
							Block highestBlock = block.getWorld().getHighestBlockAt(block.getLocation().add(0, -1, 0));
							if(block.getY() < Skyoseasons.config.snowEternalY) {
								if(highestBlock.getType() == Material.SNOW) {
									final BukkitRunnable snowMelt = new SnowMelt(this, highestBlock, Material.AIR);
									this.snowMelt.add(snowMelt);
									scheduler.scheduleSyncDelayedTask(Skyoseasons.instance, snowMelt, Skyoseasons.config.snowMeltMultiplicator * random.nextInt(60) + 180);
								}
								else if(highestBlock.getType() == Material.AIR) {
									highestBlock = highestBlock.getRelative(0, -1, 0);
									if(highestBlock.getType() == Material.ICE) {
										final BukkitRunnable snowMelt = new SnowMelt(this, highestBlock, Material.STATIONARY_WATER);
										this.snowMelt.add(snowMelt);
										scheduler.scheduleSyncDelayedTask(Skyoseasons.instance, snowMelt, Skyoseasons.config.snowMeltMultiplicator * random.nextInt(60) + 180);
									}
								}
							}
						}
					}
				}
				world.refreshChunk(chunk.getX(), chunk.getZ());
			}
			world.setStorm(season.alwaysRain);
			for(final Player player : world.getPlayers()) {
				if(message != null) {
					player.sendMessage(season.message);
				}
				if(Skyoseasons.spout != null && Skyoseasons.spout.isSpoutPlayer(player)) {
					Skyoseasons.spout.sendEffects(player, season.effects);
				}
			}
			Skyoseasons.logsManager.log(season.message, Level.INFO, world);
			tasks[0] = scheduler.scheduleSyncRepeatingTask(Skyoseasons.instance, new TimeControl(this, season.daylength, season.nightLength, Skyoseasons.config.refreshTime), Skyoseasons.config.refreshTime, Skyoseasons.config.refreshTime);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
