package fr.skyost.seasons.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import fr.skyost.seasons.SeasonWorld;

public class CancelTasks implements Runnable {
	
	private final SeasonWorld seasonWorld;
	
	public CancelTasks(final SeasonWorld seasonWorld) {
		this.seasonWorld = seasonWorld;
	}

	@Override
	public void run() {
		for(final BukkitRunnable task : seasonWorld.snowMelt) {
			try {
				task.cancel();
			}
			catch(IllegalStateException ex) {
				continue;
			}
		}
		seasonWorld.snowMelt.clear();
		seasonWorld.tasks[1] = -1;
	}
	
}
