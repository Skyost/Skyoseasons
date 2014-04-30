package fr.skyost.seasons.utils.protocollib;

import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK;
import static com.comphenix.protocol.PacketType.Play.Server.MAP_CHUNK_BULK;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

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

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;
import fr.skyost.seasons.utils.Utils;

public class ProtocolLibHook {
	
	public final HashMap<Biome, Byte> biomes = new HashMap<Biome, Byte>();
	
	private static final int BYTES_PER_NIBBLE_PART = 2048;
	private static final int CHUNK_SEGMENTS = 16;
	private static final int NIBBLES_REQUIRED = 4;
	private static final int BIOME_ARRAY_LENGTH = 256;
	
	public ProtocolLibHook() throws SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		final Class<?> biomeBase = Utils.getMCClass("BiomeBase");
		for(final Field field : biomeBase.getFields()) {
			for(final Biome biome : Biome.values()) {
				final String biomeName = biome.name();
				final String fieldName = field.getName();
				if(biomeName.equals(fieldName) || biomeName.replaceAll("FOREST", "F").equals(fieldName)) {
					final Object biomeObject = field.get(biomeBase);
					biomes.put(biome, Byte.valueOf(String.valueOf(biomeObject.getClass().getField("id").get(field.get(biomeBase)))));
				}
			}
		}
		//for(final Biome biome : Biome.values()) {
		//	if(biomes.get(biome) == null) {
		//		System.out.println(biome.name());
		//	}
		//}
		// Modify chunk packets asynchronously
		final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		if(manager != null) {
			manager.getAsynchronousManager().registerAsyncHandler(new PacketAdapter(Skyoseasons.instance, ListenerPriority.HIGHEST, MAP_CHUNK, MAP_CHUNK_BULK) {
				
				@Override
				public void onPacketSending(final PacketEvent event) {
					final Player player = event.getPlayer();
					final SeasonWorld world = Skyoseasons.worlds.get(player.getWorld().getName());
					if(world != null) {
						final PacketType type = event.getPacketType();
						if(type == MAP_CHUNK) {
							translateMapChunk(event.getPacket(), player, world);
						}
						else if(type == MAP_CHUNK_BULK) {
							translateMapChunkBulk(event.getPacket(), player, world);
						}
					}
				}
				
			}).start(2);
		}
	}
	
	public final void translateMapChunk(final PacketContainer packet, final Player player, final SeasonWorld world) throws FieldAccessException {
		final StructureModifier<Integer> ints = packet.getSpecificModifier(int.class);
		final StructureModifier<byte[]> byteArray = packet.getSpecificModifier(byte[].class);
		
		// Create an info objects
		final ChunkInfo info = new ChunkInfo();
		info.player = player;
		info.chunkX = ints.read(0); // packet.a;
		info.chunkZ = ints.read(1); // packet.b;
		info.chunkMask = ints.read(2); // packet.c;
		info.extraMask = ints.read(3); // packet.d;
		info.data = byteArray.read(1); // packet.inflatedBuffer;
		info.hasContinous = getOrDefault(packet.getBooleans().readSafely(0), true);
		info.startIndex = 0;
		
		if(info.data != null) {
			translateChunkInfo(info, info.data, world);
		}
	}
	
	// Mimic the ?? operator in C#
	private final <T> T getOrDefault(final T value, final T defaultIfNull) {
		return value != null ? value : defaultIfNull;
	}
	
	public final void translateMapChunkBulk(final PacketContainer packet, final Player player, final SeasonWorld world) throws FieldAccessException {
		StructureModifier<int[]> intArrays = packet.getSpecificModifier(int[].class);
		StructureModifier<byte[]> byteArrays = packet.getSpecificModifier(byte[].class);
		
		int[] x = intArrays.read(0); // getPrivateField(packet, "c");
		int[] z = intArrays.read(1); // getPrivateField(packet, "d");
		
		final ChunkInfo[] infos = new ChunkInfo[x.length];
		
		int dataStartIndex = 0;
		int[] chunkMask = intArrays.read(2); // packet.a;
		int[] extraMask = intArrays.read(3); // packet.b;
		
		for(int chunkNum = 0; chunkNum < infos.length; chunkNum++) {
			// Create an info objects
			final ChunkInfo info = new ChunkInfo();
			infos[chunkNum] = info;
			info.player = player;
			info.chunkX = x[chunkNum];
			info.chunkZ = z[chunkNum];
			info.chunkMask = chunkMask[chunkNum];
			info.extraMask = extraMask[chunkNum];
			info.hasContinous = true; // Always true
			info.data = byteArrays.read(1); //packet.buildBuffer;
			
			// Check for Spigot
			if(info.data == null || info.data.length == 0) {
				info.data = packet.getSpecificModifier(byte[][].class).read(0)[chunkNum];
			}
			else {
				info.startIndex = dataStartIndex;
			}
			
			translateChunkInfo(info, info.data, world);
			dataStartIndex += info.size;
		}
	}
	
	private final void translateChunkInfo(final ChunkInfo info, final byte[] returnData, final SeasonWorld world) {
		// Compute chunk number
		for(int i = 0; i < CHUNK_SEGMENTS; i++) {
			if((info.chunkMask & (1 << i)) > 0) {
				info.chunkSectionNumber++;
			}
			if((info.extraMask & (1 << i)) > 0) {
				info.extraSectionNumber++;
			}
		}
		
		// There's no sun/moon in the end or in the nether, so Minecraft doesn't sent any skylight information
		// This optimization was added in 1.4.6. Note that ideally you should get this from the "f" (skylight) field.
		final int skylightCount = info.player.getWorld().getEnvironment() == Environment.NORMAL ? 1 : 0;
		
		// To calculate the size of each chunk, we need to take into account the number of segments (out of 16)
		// that have been sent. Each segment sent is encoded in the chunkMask bit field, where every binary 1
		// indicates that a segment is present and every 0 indicates that it's not.
		
		// The total size of a chunk is the number of blocks sent (depends on the number of sections) multiplied by the 
		// amount of bytes per block. This last figure can be calculated by adding together all the data parts:
		//   For any block:
		//    * Block ID          -   8 bits per block (byte)
		//    * Block metadata    -   4 bits per block (nibble)
		//    * Block light array -   4 bits per block
		//   If 'worldProvider.skylight' is TRUE
		//    * Sky light array   -   4 bits per block
		//   If the segment has extra data:
		//    * Add array         -   4 bits per block
		//   Biome array - only if the entire chunk (has continous) is sent:
		//    * Biome array       -   256 bytes
		// 
		// A section has 16 * 16 * 16 = 4096 blocks. 
		info.size = BYTES_PER_NIBBLE_PART * ((NIBBLES_REQUIRED + skylightCount) * info.chunkSectionNumber + info.extraSectionNumber) + (info.hasContinous ? BIOME_ARRAY_LENGTH : 0);
		
		if(info.hasContinous) {
			int biomeStart = info.startIndex + info.size - BIOME_ARRAY_LENGTH;
			
			for(int i = 0; i < BIOME_ARRAY_LENGTH; i++) {
				final int currentCount = biomeStart + i;
				final Biome biome = getBiomeByID(info.data[currentCount]);
				if(biome != null) {
					final Biome replacement = world.season.replacements.get(biome);
					info.data[currentCount] = replacement == null ? biomes.get(world.season.defaultBiome) : biomes.get(replacement);
				}
				else {
					info.data[currentCount] = biomes.get(world.season.defaultBiome);
				}
			}
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
	
	// Used to pass around detailed information about chunks
	public static class ChunkInfo {
		public int chunkX;
		public int chunkZ;
		public int chunkMask;
		public int extraMask;
		public int chunkSectionNumber;
		public int extraSectionNumber;
		public boolean hasContinous;
		public byte[] data;
		public Player player;
		public int startIndex;
		public int size;
	}
	
}
