package fr.skyost.seasons.events.calendar;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.SkyoseasonsCalendarEvent;

public class DayChangeEvent extends SkyoseasonsCalendarEvent {

	private int newDay;

	public DayChangeEvent(final SeasonWorld world, final int newDay, final ModificationCause cause) {
		super(world, cause);
		this.newDay = newDay;
	}
	
	/**
	 * Gets the previous day.
	 * 
	 * @return The previous day.
	 */

	public final int getCurrentDay() {
		return this.getWorld().day;
	}
	
	/**
	 * Gets the new day.
	 * 
	 * @return The new day.
	 */

	public final int getNewDay() {
		return newDay;
	}
	
	/**
	 * Sets the new day.
	 * 
	 * @param newDay The new day.
	 */

	public final void setNewDay(final int newDay) {
		this.newDay = newDay;
	}

}
