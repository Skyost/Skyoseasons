package fr.skyost.seasons.utils.protocollib;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;
import fr.skyost.seasons.utils.protocollib.ProtocolLibHook.ChunkInfo;

public class SeasonsPacketListener extends PacketAdapter {

	private static final int BYTES_PER_NIBBLE_PART = 2048;
	private static final int CHUNK_SEGMENTS = 16;
	private static final int NIBBLES_REQUIRED = 4;
	private static final int BIOME_ARRAY_LENGTH = 256;

	public SeasonsPacketListener() {
		super(Skyoseasons.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK, PacketType.Play.Server.UPDATE_SIGN, PacketType.Play.Server.UPDATE_SIGN, PacketType.Play.Server.TILE_ENTITY_DATA);
	}

	@Override
	public final void onPacketSending(final PacketEvent event) {
		final Player player = event.getPlayer();
		final SeasonWorld world = Skyoseasons.worlds.get(player.getWorld().getName());
		if(world == null) {
			return;
		}
		final PacketType type = event.getPacketType();
		if(type == PacketType.Play.Server.MAP_CHUNK) {
			computeMapChunk(event.getPacket(), player, world);
		}
		else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
			computeMapChunkBulk(event.getPacket(), player, world);
		}
	}

	protected static final void computeMapChunk(final PacketContainer packet, final Player player, final SeasonWorld world) {
		final StructureModifier<Integer> ints = packet.getIntegers();
		final byte[] data = packet.getByteArrays().read(1);
		if(data != null) {
			translateChunkInfo(new ChunkInfo(player, ints.read(0), ints.read(1), ints.read(2), ints.read(3), getOrDefault(packet.getBooleans().readSafely(0), true), data, 0), world.season);
		}
	}

	protected static final void computeMapChunkBulk(final PacketContainer packet, final Player player, final SeasonWorld world) {
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
			translateChunkInfo(info, world.season);
			dataStartIndex += info.size;
		}
	}

	private static final <T> T getOrDefault(T value, T defaultIfNull) {
		return value != null ? value : defaultIfNull;
	}

	protected static final void translateChunkInfo(final ChunkInfo info, final Season season) {
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
		}
		System.out.println(info.x + " " + info.z);
	}

}