package com.skyost.seasons.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SeasonChangeEvent extends Event {

    private final static HandlerList HANDLERS_LIST = new HandlerList();
    private final Season season;

    public SeasonChangeEvent(final Season newSeason) {
        this.season = newSeason;
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

}
