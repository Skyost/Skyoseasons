package fr.skyost.seasons.events.time;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.SeasonWorld;

public class DayEvent extends Event {
	
	private SeasonWorld world;
    private String message;
    
    private final static HandlerList handlers = new HandlerList();
    
    public DayEvent(final SeasonWorld world, final String message) {
    	this.world = world;
    	this.message = message;
    }
    
    public final SeasonWorld getWorld() {
    	return world;
    }
    
    public final String getMessage() {
    	return message;
    }
    
    public final void setMessage(final String message) {
    	this.message = message;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }
    
    public static final HandlerList getHandlerList() {       
    	return handlers;   
    }
	
}