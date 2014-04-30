package fr.skyost.seasons.events.calendar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.utils.Utils.ModificationCause;

public class DayChangeEvent extends Event implements Cancellable {
	
	private SeasonWorld world;
    private final int prevDay;
    private int newDay;
    private final ModificationCause cause;

    private boolean cancel;
    
    private final static HandlerList handlers = new HandlerList();
    
    public DayChangeEvent(final SeasonWorld world, final int prevDay, final int newDay, final ModificationCause cause) {
    	this.world = world;
        this.prevDay = prevDay;
        this.newDay = newDay;
        this.cause = cause;
        this.cancel = false;
    }
    
    public final SeasonWorld getWorld() {
    	return world;
    }

    public final int getPreviousDay() {
        return prevDay;
    }
    
    public final int getNewDay() {
        return newDay;
    }
    
    public final void setNewDay(final int newDay) {
    	this.newDay = newDay;
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
