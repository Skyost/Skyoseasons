package fr.skyost.seasons.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.seasons.SeasonWorld;

public class SnowMelt extends BukkitRunnable {
	
	private final SeasonWorld seasonWorld;
	private final Block block;
	private final Material type;
	
	public SnowMelt(final SeasonWorld seasonWorld, final Block block, final Material type) {
		this.seasonWorld = seasonWorld;
		this.block = block;
		this.type = type;
	}
	
	@Override
	public void run() {
		block.setType(type);
		seasonWorld.snowMelt.remove(this);
	}
	
}
