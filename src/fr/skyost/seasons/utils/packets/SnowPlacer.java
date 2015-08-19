package fr.skyost.seasons.utils.packets;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;

public class SnowPlacer extends BukkitRunnable {
	
	public static final HashSet<Biome> forbiddenBiomes = new HashSet<Biome>();
	public static final HashSet<Material> forbiddenTypes = new HashSet<Material>();
	
	private final Random random = new Random();
	
	private final SeasonWorld world;
	private final BukkitScheduler scheduler;
	
	private boolean isCancelled = false;
	
	public SnowPlacer(final SeasonWorld world) {
		this.world = world;
		this.scheduler = Bukkit.getScheduler();
	}

	@Override
	public final void run() {
		for(final Chunk chunk : world.world.getLoadedChunks()) {
			final Block block = world.world.getHighestBlockAt(chunk.getBlock(random.nextInt(16), 0, random.nextInt(16)).getLocation());
			/*if(block.getLightLevel() >= 12) {
				continue;
			}*/
			if(forbiddenBiomes.contains(block.getBiome())) {
				continue;
			}
			final Material type = block.getType();
			if((type != Material.AIR && !type.isOccluding()) || type == Material.SNOW && !world.season.snowPlacerAllowStacks) {
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
			if(forbiddenTypes.contains(relativeType) || !relativeType.isOccluding()) {
				continue;
			}
			block.setType(Material.SNOW);
		}
		if(!isCancelled) {
			scheduler.scheduleSyncDelayedTask(SkyoseasonsAPI.getPlugin(), this, random.nextInt(world.season.snowPlacerDelay) + 1L);
		}
	}
	
	@Override
	public final void cancel() {
		isCancelled = true;
		super.cancel();
	}

}
