package com.skyost.seasons.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SeasonChangeEvent extends Event implements Cancellable {

    private final static HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancel;
    private final Season season;

	/**
	 * Called when a season change.
	 * You can use a cancel task.
	 * @param newSeason The new season.
	 */
    public SeasonChangeEvent(final Season newSeason) {
        this.season = newSeason;
        this.cancel = false;
    }

    public Season getSeason() {
        return season;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
