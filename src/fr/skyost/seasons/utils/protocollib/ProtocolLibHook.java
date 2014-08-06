package fr.skyost.seasons.utils.protocollib;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.Deflater;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.MapMaker;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;
import fr.skyost.seasons.utils.Utils;

/**
 * @author Comphenix.
 */

public class ProtocolLibHook {

	private static final int BYTES_PER_NIBBLE_PART = 2048;
	private static final int CHUNK_SEGMENTS = 16;
	private static final int NIBBLES_REQUIRED = 4;
	private static final int BIOME_ARRAY_LENGTH = 256;

	private final Set<Object> changed = Collections.newSetFromMap(new MapMaker().weakKeys().<Object, Boolean>makeMap());
	public static final HashMap<Biome, Byte> biomes = new HashMap<Biome, Byte>();

	public ProtocolLibHook() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
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
		final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(Skyoseasons.instance, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				final SeasonWorld world = Skyoseasons.worlds.get(player.getWorld().getName());
				if(world == null) {
					return;
				}
				final PacketType type = event.getPacketType();
				if(type == PacketType.Play.Server.MAP_CHUNK) {
					translateMapChunk(event.getPacket(), player, world);

				}
				else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
					translateMapChunkBulk(event.getPacket(), player, world);
				}
			}
			
		});
		manager.addPacketListener(new PacketAdapter(Skyoseasons.instance, ListenerPriority.MONITOR, PacketType.Play.Server.MAP_CHUNK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				finalizeMapChunk(event.getPacket());
			}

		});
	}

	public static final Biome getBiomeByID(final byte id) {
		for(final Entry<Biome, Byte> entry : ProtocolLibHook.biomes.entrySet()) {
			if(entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private final void finalizeMapChunk(final PacketContainer packet) {
		if(changed.remove(packet.getHandle())) {
			final StructureModifier<byte[]> byteArray = packet.getByteArrays();
			final Deflater localDeflater = new Deflater(-1);
			final byte[] data = byteArray.read(1);
			if(data == null) {
				return;
			}
			try {
				localDeflater.setInput(data, 0, data.length);
				localDeflater.finish();
				packet.getIntegers().write(4, localDeflater.deflate(byteArray.read(0)));
				byteArray.write(1, null);
			}
			finally {
				localDeflater.end();
			}
		}
	}

	private final void translateMapChunk(final PacketContainer packet, final Player player, final SeasonWorld world) throws FieldAccessException {
		final StructureModifier<Integer> ints = packet.getIntegers();
		final byte[] data = packet.getByteArrays().read(1);
		if(data != null) {
			final ChunkInfo info = new ChunkInfo(player, ints.read(0), ints.read(1), ints.read(2), ints.read(3), getOrDefault(packet.getBooleans().readSafely(0), true), data, 0);
			if(translateChunkInfo(info, info.data, world.season)) {
				changed.add(packet.getHandle());
			}
		}
	}

	private final <T> T getOrDefault(final T value, final T defaultIfNull) {
		return value != null ? value : defaultIfNull;
	}

	private final void translateMapChunkBulk(final PacketContainer packet, final Player player, final SeasonWorld world) throws FieldAccessException {
		final StructureModifier<int[]> intArrays = packet.getIntegerArrays();
		final StructureModifier<byte[]> byteArrays = packet.getSpecificModifier(byte[].class);
		final int[] x = intArrays.read(0);
		final int[] z = intArrays.read(1);
		int dataStartIndex = 0;
		final int[] chunkMask = intArrays.read(2);
		final int[] extraMask = intArrays.read(3);
		for(int chunkNum = 0; chunkNum < x.length; chunkNum++) {
			final ChunkInfo info = new ChunkInfo(player, x[chunkNum], z[chunkNum], chunkMask[chunkNum], extraMask[chunkNum], true, byteArrays.read(1), 0);
			if(info.data == null || info.data.length == 0) {
				info.data = packet.getSpecificModifier(byte[][].class).read(0)[chunkNum];
			}
			else {
				info.startIndex = dataStartIndex;
			}
			translateChunkInfo(info, info.data, world.season);
			dataStartIndex += info.size;
		}
	}

	private final boolean translateChunkInfo(final ChunkInfo info, final byte[] returnData, final Season season) {
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
				final Biome biome = ProtocolLibHook.getBiomeByID(info.data[i]);
				if(biome == null) {
					info.data[i] = ProtocolLibHook.biomes.get(season.defaultBiome);
					continue;
				}
				final Biome replacement = season.replacements.get(biome);
				info.data[i] = ProtocolLibHook.biomes.get(replacement == null ? season.defaultBiome : replacement);
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

}
