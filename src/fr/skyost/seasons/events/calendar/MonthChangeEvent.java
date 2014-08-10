package fr.skyost.seasons.events.calendar;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent;

public class MonthChangeEvent extends SkyoseasonsCalendarEvent {

	private Month newMonth;
	private String message;

	public MonthChangeEvent(final SeasonWorld world, final Month newMonth, final String message, final ModificationCause cause) {
		super(world, cause);
		this.newMonth = newMonth;
		this.message = message;
	}
	
	/**
	 * Gets the current month.
	 * 
	 * @return The current month.
	 */

	public final Month getCurrentMonth() {
		return this.getWorld().month;
	}
	
	/**
	 * Gets the new month.
	 * 
	 * @return The new month.
	 */

	public final Month getNewMonth() {
		return newMonth;
	}
	
	/**
	 * Sets the new month.
	 * 
	 * @param newMonth The new month.
	 */

	public final void setNewMonth(final Month newMonth) {
		this.newMonth = newMonth;
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
