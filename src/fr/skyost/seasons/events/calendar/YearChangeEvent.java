package fr.skyost.seasons.events.calendar;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent;

public class YearChangeEvent extends SkyoseasonsCalendarEvent {

	private int newYear;
	private String message;

	public YearChangeEvent(final SeasonWorld world, final int newYear, final String message, final ModificationCause cause) {
		super(world, cause);
		this.newYear = newYear;
		this.message = message;
	}
	
	/**
	 * Gets the current year.
	 * 
	 * @return The current year.
	 */

	public final int getCurrentYear() {
		return this.getWorld().year;
	}
	
	/**
	 * Gets the new year.
	 * 
	 * @return The new year.
	 */

	public final int getNewYear() {
		return newYear;
	}
	
	/**
	 * Sets the new year.
	 * 
	 * @param newYear The new year.
	 */

	public final void setNewYear(final int newYear) {
		this.newYear = newYear;
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