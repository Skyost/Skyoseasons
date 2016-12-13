package fr.skyost.seasons.utils.packets;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
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
	
	public AbstractProtocolLibHook(final Plugin protocolLib, final Plugin skyoseasons) throws PacketPluginHookInitializationException {
		Bukkit.getPluginManager().registerEvents(new PacketPluginHookEvents(), skyoseasons);
		try {
			final Class<?> biomeBaseClass = Utils.getMCClass("BiomeBase");
			if(Utils.getMCClass("Biomes") != null) { // Checks for 1.9
				final Class<?> registryMaterialsClass = Utils.getMCClass("RegistryMaterials");
				final Class<?> minecraftKeyClass = Utils.getMCClass("MinecraftKey");
				final Object registryId = registryMaterialsClass.cast(biomeBaseClass.getField("REGISTRY_ID").get(null));
				final Iterator<?> iterator = (Iterator<?>)registryMaterialsClass.getMethod("iterator").invoke(registryId);
				// Initialization :
				final Method a = biomeBaseClass.getMethod("a", biomeBaseClass);
				final Field b = minecraftKeyClass.getDeclaredField("b");
				b.setAccessible(true);
				final Method bMethod = Utils.getMethodWithUnknownType(registryMaterialsClass, "b", 1);
				while(iterator.hasNext()) {
					final Object biomeBase = biomeBaseClass.cast(iterator.next());
					final Byte id = (byte)((int)a.invoke(null, biomeBase));
					for(final Biome biome : Biome.values()) {
						if(biomes.containsKey(biome)) {
							continue;
						}
						final String biomeName = biome.name();
						final Object minecraftKey = minecraftKeyClass.cast(bMethod.invoke(registryId, biomeBase));
						final String reflectionName = b.get(minecraftKey).toString().toUpperCase();
						if(biomeName.equals(reflectionName)) {
							biomes.put(biome, id);
						}
					}
				}
			}
			else {
				for(final Field field : biomeBaseClass.getFields()) {
					for(final Biome biome : Biome.values()) {
						final String biomeName = biome.name();
						final String fieldName = field.getName();
						if(biomeName.equals(fieldName) || biomeName.replace("FOREST", "F").equals(fieldName)) {
							biomes.put(biome, Byte.valueOf(String.valueOf(field.get(biomeBaseClass).getClass().getField("id").get(field.get(biomeBaseClass)))));
						}
					}
				}
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			throw new PacketPluginHookInitializationException("Failed to load biomes.");
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(skyoseasons, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(player.getWorld());
				if(world == null) {
					return;
				}
				final PacketContainer packet = event.getPacket();
				final PacketType type = packet.getType();
				if(type == PacketType.Play.Server.MAP_CHUNK) {
					translateMapChunk(packet, player, world.season);
				}
				else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
					translateMapChunkBulk(packet, player, world.season);
				}
			}

		});
	}
	
	protected abstract void translateMapChunk(final PacketContainer packet, final Player player, final Season season);
	protected abstract void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season);
	protected abstract boolean translateChunkInfo(final ChunkInfo info, final Season season);
	public abstract void refreshChunk(final World world, final Chunk chunk);
	
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
		
		@EventHandler(priority = EventPriority.LOWEST)
		private final void onWeatherChange(final WeatherChangeEvent event) {
			if(event.isCancelled()) {
				return;
			}
			final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(event.getWorld());
			if(world == null) {
				return;
			}
			if(!world.season.snowPlacerEnabled) {
				return;
			}
			SnowPlacer task = (SnowPlacer)world.tasks.get(2);
			if(event.toWeatherState()) {
				if(task == null) {
					task = new SnowPlacer(world, world.world.getLoadedChunks());
					task.runTaskLater(SkyoseasonsAPI.getPlugin(), 20L);
					world.tasks.put(2, task);
				}
			}
			else {
				if(task != null) {
					task.cancel();
					world.tasks.remove(2);
				}
			}
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		private final void onChunkLoad(final ChunkLoadEvent event) {
			final SeasonWorld world = SkyoseasonsAPI.getSeasonWorld(event.getWorld());
			if(world == null) {
				return;
			}
			if(!world.season.snowPlacerEnabled) {
				return;
			}
			if(world.world.hasStorm()) {
				SnowPlacer task = (SnowPlacer)world.tasks.get(2);
				if(task == null) {
					task = new SnowPlacer(world, event.getChunk());
					world.tasks.put(2, task);
					task.runTaskLater(SkyoseasonsAPI.getPlugin(), 20L);
				}
				else {
					task.addChunks(event.getChunk());
				}
			}
		}
		
	}

}