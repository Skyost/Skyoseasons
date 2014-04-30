package fr.skyost.seasons.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;
import fr.skyost.seasons.events.calendar.DayChangeEvent;
import fr.skyost.seasons.events.calendar.MonthChangeEvent;
import fr.skyost.seasons.events.calendar.SeasonChangeEvent;
import fr.skyost.seasons.events.calendar.YearChangeEvent;
import fr.skyost.seasons.events.time.DayEvent;
import fr.skyost.seasons.events.time.NightEvent;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class EventsListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onDay(final DayEvent event) {
		final SeasonWorld seasonWorld = event.getWorld();
		if(seasonWorld.season.dayMessageEnabled) {
			final String message = event.getMessage();
			if(message != null) {
				for(final Player player : seasonWorld.world.getPlayers()) {
					player.sendMessage(message);
				}
				Skyoseasons.logsManager.log(message, Level.INFO, seasonWorld.world);
			}
		}
		Bukkit.getPluginManager().callEvent(new DayChangeEvent(seasonWorld, seasonWorld.day, seasonWorld.day + 1 > seasonWorld.month.days ? 1 : seasonWorld.day + 1, ModificationCause.PLUGIN));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onNight(final NightEvent event) {
		final SeasonWorld seasonWorld = event.getWorld();
		if(seasonWorld.season.nightMessageEnabled) {
			final String message = event.getMessage();
			if(message != null) {
				for(final Player player : seasonWorld.world.getPlayers()) {
					player.sendMessage(message);
				}
				Skyoseasons.logsManager.log(message, Level.INFO, seasonWorld.world);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onDayChange(final DayChangeEvent event) {
		if(!event.isCancelled()) {
			final SeasonWorld seasonWorld = event.getWorld();
			final int newDay = event.getNewDay();
			seasonWorld.day = newDay;
			if(event.getCause() == ModificationCause.PLUGIN) {
				if(event.getPreviousDay() > newDay) {
					final Month next = Skyoseasons.months.get(seasonWorld.month.next);
					Bukkit.getPluginManager().callEvent(new MonthChangeEvent(seasonWorld, seasonWorld.month, next, seasonWorld.season.monthsMessage.replaceAll("/month/", next.name), ModificationCause.PLUGIN));
				}
				else {
					seasonWorld.updateCalendar(event.getPreviousDay(), newDay);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onMonthChange(final MonthChangeEvent event) {
		if(!event.isCancelled()) {
			final SeasonWorld seasonWorld = event.getWorld();
			final Month newMonth = event.getNewMonth();
			seasonWorld.month = newMonth;
			final String message = event.getMessage();
			if(message != null) {
				for(final Player player : seasonWorld.world.getPlayers()) {
					player.sendMessage(message);
				}
				Skyoseasons.logsManager.log(message, Level.INFO, seasonWorld.world);
			}
			if(event.getCause() == ModificationCause.PLUGIN) {
				seasonWorld.seasonMonth++;
				if(seasonWorld.seasonMonth >= seasonWorld.season.months) {
					final Season next = Skyoseasons.seasons.get(seasonWorld.season.next);
					if(next != null) {
						Bukkit.getPluginManager().callEvent(new SeasonChangeEvent(seasonWorld, seasonWorld.season, next, next.message, ModificationCause.PLAYER));
					}
					else {
						Skyoseasons.logsManager.log("Sorry but the next season of : '" + seasonWorld.season.name + "' was not found.", Level.SEVERE);
					}
				}
				if(event.getPreviousMonth().number > newMonth.number) {
					Bukkit.getPluginManager().callEvent(new YearChangeEvent(seasonWorld, seasonWorld.year, seasonWorld.year + 1, Skyoseasons.calendar.Messages_Year.replaceAll("/year/", String.valueOf(seasonWorld.year + 1)), ModificationCause.PLAYER));
				}
			}
			seasonWorld.buildCalendar();
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onSeasonChange(final SeasonChangeEvent event) {
		if(!event.isCancelled()) {
			event.getWorld().setCurrentSeason(event.getNewSeason(), event.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onYearChange(final YearChangeEvent event) {
		if(!event.isCancelled()) {
			final SeasonWorld seasonWorld = event.getWorld();
			seasonWorld.year = event.getNewYear();
			final String message = event.getMessage();
			for(final Player player : seasonWorld.world.getPlayers()) {
				player.sendMessage(message);
			}
			Skyoseasons.logsManager.log(message, Level.INFO, seasonWorld.world);
		}
	}
	
}
