package fr.skyost.seasons.events.calendar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class SeasonChangeEvent extends Event implements Cancellable {
	
	private SeasonWorld world;
	private final Season oldSeason;
    private Season newSseason;
    private String message;
    private final ModificationCause cause;

    private boolean cancel;
    
    private final static HandlerList handlers = new HandlerList();
    
    public SeasonChangeEvent(final SeasonWorld world, final Season oldSeason, final Season newSseason, final String message, final ModificationCause cause) {
    	this.world = world;
    	this.oldSeason = oldSeason;
        this.newSseason = newSseason;
        this.message = message;
        this.cause = cause;
        this.cancel = false;
    }
    
    public final SeasonWorld getWorld() {
    	return world;
    }
    
    public final Season getPreviousSeason() {
    	return oldSeason;
    }

    public final Season getNewSeason() {
        return newSseason;
    }
    
    public final void setNewSeason(final Season newSseason) {
    	this.newSseason = newSseason;
    }
    
    public final String getMessage() {
    	return message;
    }
    
    public final void setMessage(final String message) {
    	this.message = message;
    }
    
    public final ModificationCause getCause() {
    	return cause;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }
    
    public static final HandlerList getHandlerList() {       
    	return handlers;   
    }

	@Override
	public final boolean isCancelled() {
		return cancel;
	}

	@Override
	public final void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
}
