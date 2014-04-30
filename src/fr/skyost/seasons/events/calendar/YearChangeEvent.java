package fr.skyost.seasons.events.calendar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class YearChangeEvent extends Event implements Cancellable {
	
	private SeasonWorld world;
    private final int prevYear;
    private int newYear;
    private String message;
    private final ModificationCause cause;

    private boolean cancel;
    
    private final static HandlerList handlers = new HandlerList();
    
    public YearChangeEvent(final SeasonWorld world, final int prevYear, final int newYear, final String message, final ModificationCause cause) {
    	this.world = world;
        this.prevYear = prevYear;
        this.newYear = newYear;
        this.message = message;
        this.cause = cause;
        this.cancel = false;
    }
    
    public final SeasonWorld getWorld() {
    	return world;
    }

    public final int getPreviousYear() {
        return prevYear;
    }
    
    public final int getNewYear() {
        return newYear;
    }
    
    public final void setNewYear(final int newYear) {
    	this.newYear = newYear;
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
