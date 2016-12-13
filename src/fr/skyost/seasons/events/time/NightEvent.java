package fr.skyost.seasons.events.time;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.SkyoseasonsEvent;

public class NightEvent extends SkyoseasonsEvent {

	private String message;

	public NightEvent(final SeasonWorld world, final String message) {
		super(world);
		this.message = message;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(final String message) {
		this.message = message;
	}

}