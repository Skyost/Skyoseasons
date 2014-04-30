package fr.skyost.seasons.events.calendar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.Month;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class MonthChangeEvent extends Event implements Cancellable {
	
	private SeasonWorld world;
    private final Month prevMonth;
    private Month newMonth;
    private String message;
    private final ModificationCause cause;

    private boolean cancel;
    
    private final static HandlerList handlers = new HandlerList();
    
    public MonthChangeEvent(final SeasonWorld world, final Month prevMonth, final Month newMonth, final String message, final ModificationCause cause) {
    	this.world = world;
        this.prevMonth = prevMonth;
        this.newMonth = newMonth;
        this.message = message;
        this.cause = cause;
        this.cancel = false;
    }
    
    public final SeasonWorld getWorld() {
    	return world;
    }

    public final Month getPreviousMonth() {
        return prevMonth;
    }
    
    public final Month getNewMonth() {
        return newMonth;
    }
    
    public final void setNewMonth(final Month newMonth) {
    	this.newMonth = newMonth;
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
