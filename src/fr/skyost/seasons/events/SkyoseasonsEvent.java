package fr.skyost.seasons.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.SeasonWorld;

public class SkyoseasonsEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final SeasonWorld world;
	
	protected SkyoseasonsEvent(final SeasonWorld world) {
		this.world = world;
	}
	
    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }
    
    public static final HandlerList getHandlerList() {       
    	return handlers;   
    }
    
    /**
     * Gets the world involved by this event.
     * 
     * @return The world.
     */
    
    public final SeasonWorld getWorld() {
    	return world;
    }

}
