package fr.skyost.seasons.events.calendar;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent;

public class SeasonChangeEvent extends SkyoseasonsCalendarEvent {

	private Season newSseason;
	private String message;

	public SeasonChangeEvent(final SeasonWorld world, final Season newSseason, final String message, final ModificationCause cause) {
		super(world, cause);
		this.newSseason = newSseason;
		this.message = message;
	}
	
	/**
	 * Gets the current season.
	 * 
	 * @return The current season.
	 */

	public final Season getCurrentSeason() {
		return this.getWorld().season;
	}
	
	/**
	 * Gets the new season.
	 * 
	 * @return The new season.
	 */

	public final Season getNewSeason() {
		return newSseason;
	}
	
	/**
	 * Sets the new season.
	 * 
	 * @param newSseason The new season.
	 */

	public final void setNewSeason(final Season newSseason) {
		this.newSseason = newSseason;
	}
	
	/**
	 * Gets the new month's message (will be broadcasted).
	 * 
	 * @return The new month's message.
	 */

	public final String getMessage() {
		return message;
	}
	
	/**
	 * Sets the new month's message (will be broadcasted).
	 * 
	 * @param message The new month's message.
	 */

	public final void setMessage(final String message) {
		this.message = message;
	}

}
