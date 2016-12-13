package fr.skyost.seasons.events;

import org.bukkit.event.Cancellable;

import fr.skyost.seasons.SeasonWorld;

public class SkyoseasonsCalendarEvent extends SkyoseasonsEvent implements Cancellable {
	
	private boolean cancel = false;
	
	private final ModificationCause cause;
	
	protected SkyoseasonsCalendarEvent(final SeasonWorld world, final ModificationCause cause) {
		super(world);
		this.cause = cause;
	}
	
	/**
	 * Gets the calendar's modification cause.
	 * 
	 * @return The modification cause.
	 */
	
	public final ModificationCause getModificationCause() {
		return cause;
	}
	
	@Override
	public final boolean isCancelled() {
		return cancel;
	}

	@Override
	public final void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
	public enum ModificationCause {
		PLUGIN,
		PLAYER;
	}

}