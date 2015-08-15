package fr.skyost.seasons.listeners;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;
import fr.skyost.seasons.events.calendar.DayChangeEvent;
import fr.skyost.seasons.events.calendar.MonthChangeEvent;
import fr.skyost.seasons.events.calendar.SeasonChangeEvent;
import fr.skyost.seasons.events.calendar.YearChangeEvent;
import fr.skyost.seasons.events.time.DayEvent;
import fr.skyost.seasons.events.time.NightEvent;
import fr.skyost.seasons.tasks.SnowMelt;
import fr.skyost.seasons.utils.LogsManager;
import fr.skyost.seasons.utils.Utils;

public class EventsListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onDay(final DayEvent event) {
		final SeasonWorld seasonWorld = event.getWorld();
		if(seasonWorld.season.dayMessageEnabled) {
			final String message = event.getMessage();
			if(message != null) {
				for(final Player player : seasonWorld.world.getPlayers()) {
					player.sendMessage(message);
				}
				SkyoseasonsAPI.getLogsManager().log(message, Level.INFO, seasonWorld.world);
			}
		}
		Bukkit.getPluginManager().callEvent(new DayChangeEvent(seasonWorld, seasonWorld.day + 1 > seasonWorld.month.days ? 1 : seasonWorld.day + 1, ModificationCause.PLUGIN));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onNight(final NightEvent event) {
		final SeasonWorld seasonWorld = event.getWorld();
		if(seasonWorld.season.nightMessageEnabled) {
			final String message = event.getMessage();
			if(message != null) {
				for(final Player player : seasonWorld.world.getPlayers()) {
					player.sendMessage(message);
				}
				SkyoseasonsAPI.getLogsManager().log(message, Level.INFO, seasonWorld.world);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onDayChange(final DayChangeEvent event) {
		if(event.isCancelled()) {
			return;	
		}
		final SeasonWorld seasonWorld = event.getWorld();
		final int newDay = event.getNewDay();
		final int curDay = event.getCurrentDay(); // We store it because it is updated below.
		seasonWorld.day = newDay;
		seasonWorld.updateCalendar(curDay, newDay);
		if(event.getModificationCause() == ModificationCause.PLUGIN && curDay > newDay) {
			final Month next = SkyoseasonsAPI.getMonth(seasonWorld.month.next);
			Bukkit.getPluginManager().callEvent(new MonthChangeEvent(seasonWorld, next, seasonWorld.season.monthsMessage.replace("/month/", next.name), ModificationCause.PLUGIN));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onMonthChange(final MonthChangeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		final LogsManager logs = SkyoseasonsAPI.getLogsManager();
		final SeasonWorld seasonWorld = event.getWorld();
		final Month newMonth = event.getNewMonth();
		final Month curMonth = event.getCurrentMonth(); // Same here.
		seasonWorld.month = newMonth;
		final String message = event.getMessage();
		if(message != null) {
			for(final Player player : seasonWorld.world.getPlayers()) {
				player.sendMessage(message);
			}
			logs.log(message, Level.INFO, seasonWorld.world);
		}
		if(event.getModificationCause() == ModificationCause.PLUGIN) {
			seasonWorld.seasonMonth++;
			if(seasonWorld.seasonMonth >= seasonWorld.season.months) {
			final Season next = SkyoseasonsAPI.getSeason(seasonWorld.season.next);
				if(next != null) {
					Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(seasonWorld, next, next.message, ModificationCause.PLAYER));
				}
				else {
					logs.log("Sorry but the next season of : '" + seasonWorld.season.name + "' was not found.", Level.SEVERE);
				}
			}
			if(curMonth.number > newMonth.number) {
				Bukkit.getPluginManager().callEvent(new YearChangeEvent(seasonWorld, seasonWorld.year + 1, SkyoseasonsAPI.getCalendarConfig().messagesYear.replace("/year/", String.valueOf(seasonWorld.year + 1)), ModificationCause.PLAYER));
			}
		}
		seasonWorld.buildCalendar();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onSeasonChange(final SeasonChangeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		event.getWorld().setCurrentSeason(event.getNewSeason(), event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onYearChange(final YearChangeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		final SeasonWorld seasonWorld = event.getWorld();
		seasonWorld.year = event.getNewYear();
		final String message = event.getMessage();
		for(final Player player : seasonWorld.world.getPlayers()) {
			player.sendMessage(message);
		}
		SkyoseasonsAPI.getLogsManager().log(message, Level.INFO, seasonWorld.world);
	}
	
	@EventHandler
	private final void onChunkLoad(final ChunkLoadEvent event) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(event.getWorld());
		if(world != null) {
			final Chunk chunk = event.getChunk();
			if(event.isNewChunk()) {
				chunk.load(true);
			}
			final List<Location> snowBlocks = world.handleBlocks(chunk);
			if(!world.season.snowMelt) {
				return;
			}
			snowBlocks.removeAll(world.globalSnowBlocks);
			if(snowBlocks.size() != 0) {
				final List<BukkitRunnable> tasks = world.tasks.get(1);
				if(tasks == null || tasks.size() == 0) {
					final SnowMelt task = new SnowMelt(world, snowBlocks);
					task.runTaskTimer(SkyoseasonsAPI.getPlugin(), 20L, new Random().nextInt(SkyoseasonsAPI.getConfig().snowMeltMaxDelay) + 1);
					world.tasks.put(1, task);
					return;
				}
				final List<List<Location>> snowBlocksSplitted = Utils.splitList(snowBlocks, tasks.size());
				for(int i = 0; i != snowBlocksSplitted.size(); i++) {
					((SnowMelt)tasks.get(i)).addBlocks(snowBlocksSplitted.get(i));
				}
			}
		}
	}
	
	@EventHandler
	private final void onWeatherChange(final WeatherChangeEvent event) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(event.getWorld());
		if(world != null) {
			if(!world.season.canRain) {
				if(event.toWeatherState()) {
					event.setCancelled(true);
				}
			}
			else if(world.season.alwaysRain) {
				if(!event.toWeatherState()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private final void onInventoryClick(final InventoryClickEvent event) {
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(event.getWhoClicked().getWorld());
		if(world != null && event.getInventory().equals(world.calendar)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(player.getWorld());
		if(world != null && world.season.resourcePackUrl != null) {
			player.setResourcePack(world.season.resourcePackUrl);
		}
	}
	
}
