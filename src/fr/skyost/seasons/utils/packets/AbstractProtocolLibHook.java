package fr.skyost.seasons.utils.packets;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.utils.Utils;

public abstract class AbstractProtocolLibHook {
	
	public static final int BYTES_PER_NIBBLE_PART = 2048;
	public static final int CHUNK_SEGMENTS = 16;
	public static final int BIOME_ARRAY_LENGTH = 256;
	public static final int NIBBLES_REQUIRED = 4;
	
	private final HashMap<Biome, Byte> biomes = new HashMap<Biome, Byte>();
	protected byte defaultBiomeId = 0;
	
	public AbstractProtocolLibHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		Bukkit.getPluginManager().registerEvents(new PacketPluginHookEvents(), plugin);
		try {
			final Class<?> biomeBase = Utils.getMCClass("BiomeBase");
			for(final Field field : biomeBase.getFields()) {
				for(final Biome biome : Biome.values()) {
					final String biomeName = biome.name();
					final String fieldName = field.getName();
					if(biomeName.equals(fieldName) || biomeName.replace("FOREST", "F").equals(fieldName)) {
						biomes.put(biome, Byte.valueOf(String.valueOf(field.get(biomeBase).getClass().getField("id").get(field.get(biomeBase)))));
					}
				}
			}
		}
		catch(final Exception ex) {
			throw new PacketPluginHookInitializationException("Failed to load biomes.");
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(player.getWorld());
				if(world == null) {
					return;
				}
				final PacketType type = event.getPacketType();
				if(type == PacketType.Play.Server.MAP_CHUNK) {
					translateMapChunk(event.getPacket(), event.getPlayer(), world.season);
				}
				else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
					translateMapChunkBulk(event.getPacket(), event.getPlayer(), world.season);
				}
			}

		});
	}
	
	protected abstract void translateMapChunk(final PacketContainer packet, final Player player, final Season season);
	protected abstract void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season);
	protected abstract boolean translateChunkInfo(final ChunkInfo info, final Season season);
	
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
	
	public final void setDefaultBiome(final Biome biome) {
		defaultBiomeId = biomes.get(biome);
	}
	
	protected final <T> T getOrDefault(final T value, final T defaultIfNull) {
		return value != null ? value : defaultIfNull;
	}
	
	protected static class ChunkInfo {

		public Player player;
		public int chunkMask;
		public int extraMask;
		public boolean hasContinous;
		public byte[] data;
		public int startIndex;
		public int chunkSectionNumber;
		public int extraSectionNumber;
		public int size;
		
		public ChunkInfo(final Player player, final int chunkMask, final int extraMask, final boolean hasContinous, final byte[] data, final int startIndex) {
			this.player = player;
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