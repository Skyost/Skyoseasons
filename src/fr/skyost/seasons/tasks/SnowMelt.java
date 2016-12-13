package fr.skyost.seasons.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;

public class SnowMelt extends BukkitRunnable {
	
	private final SeasonWorld world;
	private final HashMap<Chunk, Location[]> chunks = new HashMap<Chunk, Location[]>();
	
	private final Random random = new Random();
	private boolean isCancelled = false;
	
	public SnowMelt(final SeasonWorld world, final Chunk... chunks) {
		this.world = world;
		addChunks(chunks);
	}

	@Override
	public final void run() {
		final Location blank = new Location(world.world, 0, 0, 0);
		for(final Chunk chunk : new HashSet<Chunk>(chunks.keySet())) {
			final List<Location> locations = new ArrayList<Location>(Arrays.asList(chunks.get(chunk)));
			final Location randomBlock = blank.clone();
			do {
				randomBlock.setX(random.nextInt(16));
				randomBlock.setZ(random.nextInt(16));
			}
			while(locations.contains(randomBlock));
			locations.add(randomBlock);
			chunks.put(chunk, locations.toArray(new Location[locations.size()]));
			
			if(locations.size() >= 256) {
				chunks.remove(chunk);
			}
			
			Block block = world.world.getHighestBlockAt(chunk.getBlock(randomBlock.getBlockX(), 0, randomBlock.getBlockZ()).getLocation());
			if(block.getY() < SkyoseasonsAPI.getConfig().snowEternalY) {
				final Material type = block.getType();
				if(type == Material.SNOW) {
					block.setType(Material.AIR);
				}
				else if(type == Material.AIR) {
					block = block.getRelative(0, -1, 0);
					if(block.getType() == Material.ICE) {
						block.setType(Material.STATIONARY_WATER);
					}
				}
			}
		}
		if(!isCancelled) {
			Bukkit.getScheduler().runTaskLater(SkyoseasonsAPI.getPlugin(), this, random.nextInt(SkyoseasonsAPI.getConfig().snowMeltMaxDelay) + 1L);
		}
	}
	
	public final void addChunks(final Chunk... chunks) {
		for(final Chunk chunk : chunks) {
			if(this.chunks.containsKey(chunk)) {
				continue;
			}
			this.chunks.put(chunk, new Location[]{});
		}
	}
	
	@Override
	public final void cancel() {
		isCancelled = true;
		super.cancel();
	}
	
}