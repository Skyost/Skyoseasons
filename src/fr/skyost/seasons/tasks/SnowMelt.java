package fr.skyost.seasons.tasks;

import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import fr.skyost.seasons.SeasonWorld;

public class SnowMelt extends BukkitRunnable {
	
	private final SeasonWorld seasonWorld;
	private final List<Location> snowBlocks;
	
	public SnowMelt(final SeasonWorld seasonWorld, final List<Location> snowBlocks) {
		this.seasonWorld = seasonWorld;
		this.snowBlocks = snowBlocks;
		Collections.shuffle(snowBlocks);
	}
	
	@Override
	public final void run() {
		if(snowBlocks.size() == 0) {
			seasonWorld.tasks.remove(1);
			this.cancel();
			return;
		}
		final Location location = snowBlocks.get(0);
		final Block block = seasonWorld.world.getBlockAt(location);
		final Material type = block.getType();
		if(type == Material.SNOW) {
			block.setType(Material.AIR);
		}
		else if(type == Material.ICE) {
			block.setType(Material.STATIONARY_WATER);
		}
		snowBlocks.remove(0);
	}
	
	public final void addBlocks(final List<Location> locations) {
		snowBlocks.addAll(locations);
		Collections.shuffle(snowBlocks);
	}
	
}
