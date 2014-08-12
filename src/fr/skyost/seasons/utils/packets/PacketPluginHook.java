package fr.skyost.seasons.utils.packets;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.utils.Utils;

public abstract class PacketPluginHook {
	
	public static final int BYTES_PER_NIBBLE_PART = 2048;
	public static final int CHUNK_SEGMENTS = 16;
	public static final int NIBBLES_REQUIRED = 4;
	public static final int BIOME_ARRAY_LENGTH = 256;
	
	private final HashMap<Biome, Byte> biomes = new HashMap<Biome, Byte>();
	
	public PacketPluginHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		try {
			Bukkit.getPluginManager().registerEvents(new PacketPluginHookEvents(), plugin);
			final Class<?> biomeBase = Utils.getMCClass("BiomeBase");
			for(final Field field : biomeBase.getFields()) {
				for(final Biome biome : Biome.values()) {
					final String biomeName = biome.name();
					final String fieldName = field.getName();
					if(biomeName.equals(fieldName) || biomeName.replace("FOREST", "F").equals(fieldName)) {
						final Object biomeObject = field.get(biomeBase);
						biomes.put(biome, Byte.valueOf(String.valueOf(biomeObject.getClass().getField("id").get(field.get(biomeBase)))));
					}
				}
			}
		}
		catch(final Exception ex) {
			throw new PacketPluginHookInitializationException("Failed to load biomes ids.");
		}
	}
	
	public final Biome getBiomeByID(final byte id) {
		for(final Entry<Biome, Byte> entry : biomes.entrySet()) {
			if(entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public final Byte getBiomeID(final Biome biome) {
		return biomes.get(biome);
	}
	
	protected final boolean translateChunkInfo(final ChunkInfo info, final Season season) {
		for(int i = 0; i < CHUNK_SEGMENTS; i++) {
			if((info.chunkMask & (1 << i)) > 0) {
				info.chunkSectionNumber++;
			}
			if((info.extraMask & (1 << i)) > 0) {
				info.extraSectionNumber++;
			}
		}
		final int skylightCount = info.player.getWorld().getEnvironment() == Environment.NORMAL ? 1 : 0;
		info.size = BYTES_PER_NIBBLE_PART * ((NIBBLES_REQUIRED + skylightCount) * info.chunkSectionNumber + info.extraSectionNumber) + (info.hasContinous ? BIOME_ARRAY_LENGTH : 0);
		if(info.hasContinous) {
			final int biomeStart = info.startIndex + info.size - BIOME_ARRAY_LENGTH;
			for(int i = biomeStart; i < BIOME_ARRAY_LENGTH + biomeStart; i++) {
				final Biome biome = this.getBiomeByID(info.data[i]);
				if(biome == null) {
					info.data[i] = this.getBiomeID(season.defaultBiome);
					continue;
				}
				final Biome replacement = season.replacements.get(biome);
				info.data[i] = this.getBiomeID(replacement == null ? season.defaultBiome : replacement);
			}
			return true;
		}
		return false;
	}
	
	public static class ChunkInfo {

		public final Player player;
		public final int x;
		public final int z;
		public final int chunkMask;
		public final int extraMask;
		public final boolean hasContinous;
		public byte[] data;
		public int startIndex;
		public int chunkSectionNumber;
		public int extraSectionNumber;
		public int size;

		protected ChunkInfo(final Player player, final int x, final int z, final int chunkMask, final int extraMask, final boolean hasContinous, final byte[] data, final int startIndex) {
			this.player = player;
			this.x = x;
			this.z = z;
			this.chunkMask = chunkMask;
			this.extraMask = extraMask;
			this.hasContinous = hasContinous;
			this.data = data;
			this.startIndex = startIndex;
		}

	}
	
	public class PacketPluginHookInitializationException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public PacketPluginHookInitializationException(final String message) {
			super(message);
		}
		
	}
	
	public class PacketPluginHookEvents implements Listener {
		
		@EventHandler
		private final void onWeatherChange(final WeatherChangeEvent event) {
			final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(event.getWorld());
			if(world == null) {
				return;
			}
			if(event.toWeatherState()) {
				if(world.season.snowPlacerEnabled) {
					final SnowPlacer task = new SnowPlacer(world);
					task.runTaskLater(SkyoseasonsAPI.getPlugin(), 20L);
					world.tasks.put(2, task);
				}
			}
			else if(world.tasks.containsKey(2)) {
				world.tasks.get(2).get(0).cancel();
				world.tasks.removeAll(2);
			}
		}
		
	}

}
