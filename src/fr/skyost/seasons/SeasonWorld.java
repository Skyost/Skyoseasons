package fr.skyost.seasons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
import fr.skyost.seasons.utils.Title;
import fr.skyost.seasons.utils.Utils;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

public class SeasonWorld {
	
	/**
	 * The corresponding world.
	 */
	
	public final World world;
	
	/**
	 * The current day.
	 */
	
	public int day;
	
	/**
	 * The current month.
	 */
	
	public Month month;
	
	/**
	 * The current season.
	 */
	
	public Season season;
	
	/**
	 * The current month of the season.
	 */
	
	public int seasonMonth;
	
	/**
	 * The current year.
	 */
	
	public int year;
	
	/**
	 * The calendar inventory.
	 */
	
	public Inventory calendar;
	
	/**
	 * The tasks (snow placer, snow melt, ...).
	 */
	
	public final HashMap<Short, BukkitRunnable> tasks = new HashMap<Short, BukkitRunnable>();
	
	/**
	 * The time control task.
	 */
	
	public static final short TASK_TIME_CONTROL = 0;
	
	/**
	 * The snow melt task.
	 */
	
	public static final short TASK_SNOW_MELT = 1;
	
	/**
	 * The snow placer task.
	 */
	
	public static final short TASK_SNOW_PLACER = 2;
	
	/**
	 * Creates a new SeasonWorld instance.
	 * 
	 * @param world The corresponding world.
	 * @param config The configuration.
	 */
	
	public SeasonWorld(final World world, final WorldConfig config) {
		this(world, config.day, SkyoseasonsAPI.getMonth(config.month), SkyoseasonsAPI.getSeason(config.season), config.seasonMonth, config.year);
	}
	
	/**
	 * Creates a new SeasonWorld instance.
	 * 
	 * @param world The corresponding world.
	 */
	
	public SeasonWorld(final World world) {
		this(world, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), SkyoseasonsAPI.getMonth(1), null, 1, Calendar.getInstance().get(Calendar.YEAR));
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
	
	/**
	 * Updates the calendar's items.
	 * 
	 * @param prevDay The "previous current" day.
	 * @param newDay The "new current" day.
	 */
	
	public final void updateCalendar(final int prevDay, final int newDay) {
		final CalendarConfig calendar = SkyoseasonsAPI.getPlugin().calendar;
		
		ItemStack item = this.calendar.getItem(prevDay - 1);
		item.setType(calendar.calendarDaysItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(calendar.calendarDaysName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day - 1)).replace("/ordinal/", Utils.getOrdinalSuffix(day - 1)).replace("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
		
		item = this.calendar.getItem(newDay - 1);
		item.setType(calendar.calendarTodayItem);
		meta = item.getItemMeta();
		meta.setDisplayName(calendar.calendarTodayName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day)).replace("/ordinal/", Utils.getOrdinalSuffix(day)).replace("/year/", String.valueOf(year)));
		item.setItemMeta(meta);
	}
	
	/**
	 * Updates the calendar for viewers.
	 */
	
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
	
	/**
	 * Builds the calendar for the specified month.
	 * 
	 * @param month The month.
	 * 
	 * @return The calendar.
	 */
	
	public final Inventory buildCalendar(final Month month) {
		final CalendarConfig calendar = SkyoseasonsAPI.getPlugin().calendar;
		final Inventory menu = Bukkit.createInventory(null, Utils.round(month.days, 9), month.name + " " + year);
		for(int i = 1; i <= month.days; i++) {
			final ItemStack item;
			final ItemMeta meta;
			if(i == day) {
				item = new ItemStack(calendar.calendarTodayItem);
				meta = item.getItemMeta();
				meta.setDisplayName(calendar.calendarTodayName.replace("/month/", month.name).replace("/day-number/", String.valueOf(day)).replace("/ordinal/", Utils.getOrdinalSuffix(day)).replace("/year/", String.valueOf(year)));
			}
			else {
				item = new ItemStack(calendar.calendarDaysItem);
				meta = item.getItemMeta();
				meta.setDisplayName(calendar.calendarDaysName.replace("/month/", month.name).replace("/day-number/", String.valueOf(i)).replace("/ordinal/", Utils.getOrdinalSuffix(i)).replace("/year/", String.valueOf(year)));
			}
			item.setItemMeta(meta);
			menu.addItem(item);
		}
		return menu;
	}
	
	/**
	 * Sets the current season.
	 * 
	 * @param season The season.
	 * @param message The message.
	 */
	
	public final void setCurrentSeason(final Season season, final String message) {
		setCurrentSeason(season, message, 1);
	}
	
	/**
	 * Sets the current season.
	 * 
	 * @param season The season.
	 * @param message The message.
	 * @param seasonMonth The current month of the season.
	 */
	
	public final void setCurrentSeason(final Season season, final String message, final int seasonMonth) {
		final TimeControl timeControl = (TimeControl)tasks.get(TASK_TIME_CONTROL);
		if(timeControl != null) {
			timeControl.cancel();
			tasks.remove(TASK_TIME_CONTROL);
		}
		this.season = season;
		this.seasonMonth = seasonMonth;
		final Chunk[] chunks = world.getLoadedChunks();
		final AbstractProtocolLibHook protocolLibHook = SkyoseasonsAPI.getProtocolLibHook();
		if(protocolLibHook == null) {
			changeBiome(chunks);
		}
		else {
			protocolLibHook.setDefaultBiome(season.defaultBiome);
		}
		refreshChunks(chunks);
		SnowMelt snowMelt = (SnowMelt)tasks.get(TASK_SNOW_MELT);
		if(season.snowMeltEnabled) {
			if(snowMelt == null) {
				snowMelt = new SnowMelt(this, chunks);
				snowMelt.runTaskLater(SkyoseasonsAPI.getPlugin(), 20L);
				tasks.put(TASK_SNOW_MELT, snowMelt);
			}
			else {
				snowMelt.addChunks(chunks);
			}
		}
		else {
			if(snowMelt != null) {
				snowMelt.cancel();
				tasks.remove(TASK_SNOW_MELT);
			}
		}
		world.setStorm(season.alwaysRain);
		if(message != null || season.resourcePackUrl != null || season.titleEnabled) {
			for(final Player player : world.getPlayers()) {
				if(message != null) {
					player.sendMessage(season.message);
				}
				if(season.resourcePackUrl != null) {
					player.setResourcePack(season.resourcePackUrl);
				}
				if(season.titleEnabled) {
					player.sendTitle(season.titleMessage, season.titleSubtitle/* TODO: temporaly removed : , season.titleFadeIn, season.titleStay, season.titleFadeOut*/);
				}
			}
		}
		SkyoseasonsAPI.getLogsManager().log(season.message, Level.INFO, world);
		
		final PluginConfig config = SkyoseasonsAPI.getConfig();
		
		final TimeControl task = new TimeControl(this, season.daylength, season.nightLength, config.timeControlRefreshTime);
		task.runTaskTimer(SkyoseasonsAPI.getPlugin(), config.timeControlRefreshTime, config.timeControlRefreshTime);
		tasks.put(TASK_TIME_CONTROL, task);
	}
	
	/**
	 * Changes the biome for the selected chunks.
	 * 
	 * @param chunks The chunks.
	 */
	
	public final void changeBiome(final Chunk... chunks) {
		new BukkitRunnable() {

			@Override
			public void run() {
				for(final Chunk chunk : chunks) {
					for(int x = 0; x < 16; x++) {
						for(int z = 0; z < 16; z++) {
							final Block block = chunk.getBlock(x, 0, z);
							final Biome biome = season.replacements.get(block.getBiome());
							block.setBiome(biome == null ? season.defaultBiome : biome);
						}
					}
				}
			}
			
		}.runTask(SkyoseasonsAPI.getPlugin());
	}
	
	/**
	 * Refreshes the specified chunks.
	 * 
	 * @param chunks The chunks.
	 */
	
	public final void refreshChunks(final Chunk... chunks) {
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
	
}