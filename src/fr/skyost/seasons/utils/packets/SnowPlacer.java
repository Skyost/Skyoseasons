package fr.skyost.seasons.utils.packets;

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

public class SnowPlacer extends BukkitRunnable {
	
	private final SeasonWorld world;
	private final HashMap<Chunk, Location[]> chunks = new HashMap<Chunk, Location[]>();
	
	private final Random random = new Random();
	private boolean isCancelled = false;
	
	public SnowPlacer(final SeasonWorld world, final Chunk... chunks) {
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
			
			removeChunkIfUseless(chunk);
			
			final Block block = world.world.getHighestBlockAt(chunk.getBlock(randomBlock.getBlockX(), 0, randomBlock.getBlockZ()).getLocation());
			/*if(block.getLightLevel() >= 12) {
				continue;
			}*/
			if(world.season.snowPlacerForbiddenBiomes.contains(block.getBiome())) {
				continue;
			}
			final Material type = block.getType();
			if((type != Material.AIR && !type.isOccluding()) || (type == Material.SNOW && !world.season.snowPlacerAllowStacks)) {
				continue;
			}
			final Block relative = block.getRelative(0, -1, 0);
			if(relative.getLightLevel() >= 12) {
				continue;
			}
			final Material relativeType = relative.getType();
			if(relativeType == Material.STATIONARY_WATER) {
				relative.setType(Material.ICE);
				continue;
			}
			if(world.season.snowPlacerForbiddenTypes.contains(relativeType) || !relativeType.isOccluding()) {
				continue;
			}
			block.setType(Material.SNOW);
		}
		if(!isCancelled) {
			Bukkit.getScheduler().runTaskLater(SkyoseasonsAPI.getPlugin(), this, random.nextInt(world.season.snowPlacerDelay) + 1L);
		}
	}
	
	private final void removeChunkIfUseless(final Chunk chunk) {
		boolean snowEverywhere = true;
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				final Block block = chunk.getBlock(x, 0, z);
				final Material highest = block.getLocation().getWorld().getHighestBlockAt(block.getX(), block.getZ()).getType();
				if(highest != Material.SNOW && highest != Material.SNOW_BLOCK && highest != Material.ICE) {
					snowEverywhere = false;
					break;
				}
			}
			if(!snowEverywhere) {
				break;
			}
		}
		if(snowEverywhere) {
			chunks.remove(chunk);
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