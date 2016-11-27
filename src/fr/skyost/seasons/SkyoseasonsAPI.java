package fr.skyost.seasons;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.skyost.seasons.events.SkyoseasonsCalendarEvent.ModificationCause;
import fr.skyost.seasons.events.calendar.DayChangeEvent;
import fr.skyost.seasons.events.calendar.MonthChangeEvent;
import fr.skyost.seasons.events.calendar.SeasonChangeEvent;
import fr.skyost.seasons.events.calendar.YearChangeEvent;
import fr.skyost.seasons.utils.LogsManager;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

/**
 * The API of Skyoseasons.
 * 
 * @author Skyost.
 * 
 */

public class SkyoseasonsAPI {
	
	/**
	 * Gets the plugin's config (= config.yml).
	 * 
	 * @return The plugin's config.
	 */
	
	public static final PluginConfig getConfig() {
		return Skyoseasons.config;
	}
	
	/**
	 * Gets the calendar's config (= calendar.yml).
	 * 
	 * @return The calendar's config.
	 */
	
	public static final CalendarConfig getCalendarConfig() {
		return Skyoseasons.calendar;
	}
	
	/**
	 * Gets the Skyoseasons's logs manager.
	 * 
	 * @return The logs manager.
	 */
	
	public static final LogsManager getLogsManager() {
		return Skyoseasons.logsManager;
	}
	
	/**
	 * Gets the plugin.
	 * 
	 * @return The plugin.
	 */
	
	public static final Skyoseasons getPlugin() {
		return Skyoseasons.instance;
	}
	
	/**
	 * Gets the Spout hook.
	 * 
	 * @return The Spout hook or null if it is disabled.
	 * 
	 * @deprecated Spout has been removed.
	 */
	
	@Deprecated
	public static final Object getSpoutHook() {
		return null;
	}
	
	/**
	 * Gets the ProtocolLib hook.
	 * 
	 * @return The ProtocolLib hook or null if it is disabled.
	 */
	
	public static final AbstractProtocolLibHook getProtocolLibHook() {
		return Skyoseasons.protocolLib;
	}
	
	/**
	 * Gets a season by its name (case insensitive).
	 * 
	 * @param seasonName The season's name.
	 * 
	 * @return The season.
	 */
	
	public static final Season getSeason(final String seasonName) {
		for(final Entry<String, Season> entry : Skyoseasons.seasons.entrySet()) {
			if(entry.getKey().equalsIgnoreCase(seasonName)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Gets a season by its name (case sensitive).
	 * 
	 * @param seasonName The season's name.
	 * 
	 * @return The season.
	 */
	
	public static final Season getSeasonExact(final String seasonName) {
		return Skyoseasons.seasons.get(seasonName);
	}
	
	/**
	 * Gets all seasons (unordered).
	 * 
	 * @return All seasons.
	 */
	
	public static final Season[] getSeasons() {
		final Collection<Season> seasons = Skyoseasons.seasons.values();
		return seasons.toArray(new Season[seasons.size()]);
	}
	
	/**
	 * Gets all seasons' names (unordered).
	 * 
	 * @return All seasons' names.
	 */
	
	public static final String[] getSeasonsNames() {
		final Set<String> seasonsNames = Skyoseasons.seasons.keySet();
		return seasonsNames.toArray(new String[seasonsNames.size()]);
	}
	
	/**
	 * Gets a season world by its corresponding world (case insensitive).
	 * 
	 * @param world The world.
	 * 
	 * @return The season world.
	 */
	
	public static final SeasonWorld getSeasonWorld(final World world) {
		return getSeasonWorld(world.getName());
	}
	
	/**
	 * Gets a season world by its name (case insensitive).
	 * 
	 * @param worldName The world's name.
	 * 
	 * @return The season world.
	 */

	public static final SeasonWorld getSeasonWorld(final String worldName) {
		for(final Entry<String, SeasonWorld> entry : Skyoseasons.worlds.entrySet()) {
			if(entry.getKey().equalsIgnoreCase(worldName)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Gets a season world by its corresponding world (case sensitive).
	 * 
	 * @param world The world.
	 * 
	 * @return The season world.
	 */
	
	public static final SeasonWorld getSeasonWorldExact(final World world) {
		return getSeasonWorldExact(world.getName());
	}
	
	/**
	 * Gets a season world by its name (case sensitive).
	 * 
	 * @param worldName The world's name.
	 * 
	 * @return The season world.
	 */
	
	public static final SeasonWorld getSeasonWorldExact(final String worldName) {
		return Skyoseasons.worlds.get(worldName);
	}
	
	/**
	 * Gets all season worlds (unordered).
	 * 
	 * @return All season worlds.
	 */
	
	public static final SeasonWorld[] getSeasonWorlds() {
		final Collection<SeasonWorld> seasonWorlds = Skyoseasons.worlds.values();
		return seasonWorlds.toArray(new SeasonWorld[seasonWorlds.size()]);
	}
	
	/**
	 * Gets all season worlds' names (unordered).
	 * 
	 * @return All season worlds' names.
	 */
	
	public static final String[] getSeasonWorldsNames() {
		final Set<String> seasonWorldsNames = Skyoseasons.worlds.keySet();
		return seasonWorldsNames.toArray(new String[seasonWorldsNames.size()]);
	}
	
	/**
	 * Gets a month by its number.
	 * 
	 * @param number The month's number (begins at 0).
	 * 
	 * @return The month.
	 */
	
	public static final Month getMonth(final int number) {
		return Skyoseasons.months.getByIndex(number - 1);
	}
	
	/**
	 * Gets a month by its name (case insensitive).
	 * 
	 * @param monthName The month's name.
	 * 
	 * @return The month.
	 */
	
	public static final Month getMonth(final String monthName) {
		for(final Entry<String, Month> entry : Skyoseasons.months.entrySet()) {
			if(entry.getKey().equalsIgnoreCase(monthName)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Gets a month by its name (case sensitive).
	 * 
	 * @param monthName The month's name.
	 * 
	 * @return The month.
	 */
	
	public static final Month getMonthExact(final String monthName) {
		return Skyoseasons.months.get(monthName);
	}
	
	/**
	 * Gets all months (unordered).
	 * 
	 * @return All months.
	 */
	
	public static final Month[] getMonths() {
		final Collection<Month> months = Skyoseasons.months.values();
		return months.toArray(new Month[months.size()]);
	}
	
	/**
	 * Gets all months' names (unordered).
	 * 
	 * @return All months' names.
	 */
	
	public static final String[] getMonthsNames() {
		final Set<String> monthsNames = Skyoseasons.months.keySet();
		return monthsNames.toArray(new String[monthsNames.size()]);
	}
	
	/**
	 * Calls a day change event (which modify the current day of a world).
	 * 
	 * @param world The world.
	 * @param newDay The new day.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final DayChangeEvent callDayChange(final SeasonWorld world, final int newDay, final ModificationCause cause) {
		final DayChangeEvent event = new DayChangeEvent(world, newDay, cause);
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}
	
	/**
	 * Calls a month change event (which modify the current month of a world) and broadcasts the default message.
	 * 
	 * @param world The world.
	 * @param newMonth The new month.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final MonthChangeEvent callMonthChange(final SeasonWorld world, final Month newMonth, final ModificationCause cause) {
		return callMonthChange(world, newMonth, cause, world.season.monthsMessage.replace("/month/", newMonth.name));
	}
	
	/**
	 * Calls a month change event (which modify the current month of a world).
	 * 
	 * @param world The world.
	 * @param newMonth The new month.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * @param message The message which will be broadcasted.
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final MonthChangeEvent callMonthChange(final SeasonWorld world, final Month newMonth, final ModificationCause cause, final String message) {
		final MonthChangeEvent event = new MonthChangeEvent(world, newMonth, message, cause);
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}
	
	/**
	 * Calls a season change event (which modify the current season of a world) and broadcasts the default message.
	 * 
	 * @param world The world.
	 * @param season The new season.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final SeasonChangeEvent callSeasonChange(final SeasonWorld world, final Season season, final ModificationCause cause) {
		return callSeasonChange(world, season, cause, season.message);
	}
	
	/**
	 * Calls a month change event (which modify the current season of a world).
	 * 
	 * @param world The world.
	 * @param season The new season.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * @param message The message which will be broadcasted.
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final SeasonChangeEvent callSeasonChange(final SeasonWorld world, final Season season, final ModificationCause cause, final String message) {
		final SeasonChangeEvent event = new SeasonChangeEvent(world, season, message, cause);
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}
	
	/**
	 * Calls a year change event (which modify the current year of a world) and broadcasts the default message.
	 * 
	 * @param world The world.
	 * @param year The new year.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final YearChangeEvent callYearChange(final SeasonWorld world, final int year, final ModificationCause cause) {
		return callYearChange(world, year, cause, getCalendarConfig().messagesYear.replace("/year/", String.valueOf(year)));
	}
	
	/**
	 * Calls a year change event (which modify the current year of a world).
	 * 
	 * @param world The world.
	 * @param year The new year.
	 * @param cause The modification cause (if it is a player who calls this event or no).
	 * @param message The message which will be broadcasted.
	 * 
	 * @return The called event (may be altered by others plugins).
	 */
	
	public static final YearChangeEvent callYearChange(final SeasonWorld world, final int year, final ModificationCause cause, final String message) {
		final YearChangeEvent event = new YearChangeEvent(world, year, message, cause);
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}

}
