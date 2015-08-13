package fr.skyost.seasons.utils.packets.v1_8_R3;

import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.SkyoseasonsAPI;
import fr.skyost.seasons.utils.LogsManager;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

public class ProtocolLibHook extends AbstractProtocolLibHook {

	private static final int NIBBLES_REQUIRED = 2;

	public ProtocolLibHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		super(plugin);
	}

	@Override
	protected final void translateMapChunk(final PacketContainer packet, final Player player, final Season season) {
		final StructureModifier<Integer> ints = packet.getIntegers();
		final ChunkMap chunk = (ChunkMap)packet.getModifier().read(2);
		if(chunk.a != null) {
			translateChunkInfo(new ChunkInfo(player, ints.read(0), ints.read(1), chunk.b, 0, getOrDefault(packet.getBooleans().readSafely(0), true), chunk.a, 0), season);
		}
	}

	@Override
	protected final void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season) {
		final StructureModifier<int[]> intArrays = packet.getIntegerArrays();
		final int[] x = intArrays.read(0);
		final int[] z = intArrays.read(1);
		int dataStartIndex = 0;
		final ChunkMap[] chunks = (ChunkMap[])packet.getModifier().read(2);
		for(int chunkNum = 0; chunkNum < x.length; chunkNum++) {
			final ChunkInfo info = new ChunkInfo(player, x[chunkNum], z[chunkNum], chunks[chunkNum].b, 0, true, chunks[chunkNum].a, 0);
			if(info.data == null || info.data.length == 0) {
				info.data = chunks[chunkNum].a;
			}
			else {
				info.startIndex = dataStartIndex;
			}
			translateChunkInfo(info, season);
			dataStartIndex += info.size;
		}
	}

	@Override
	protected final boolean translateChunkInfo(final ChunkInfo info, final Season season) {
		for(int i = 0; i < CHUNK_SEGMENTS; i++) {
			if((info.chunkMask & (1 << i)) > 0) {
				info.chunkSectionNumber++;
			}
		}
		info.size = BYTES_PER_NIBBLE_PART * ((NIBBLES_REQUIRED + (info.player.getWorld().getEnvironment() == Environment.NORMAL ? 1 : 0)) * info.chunkSectionNumber) + (info.hasContinous ? BIOME_ARRAY_LENGTH : 0);
		if(info.hasContinous) {
			int biomeStart = info.startIndex + info.size - BIOME_ARRAY_LENGTH;
			switch(info.chunkSectionNumber) {
			case 4:
				biomeStart = 49152;
				break;
			case 5:
				biomeStart = 61440;
				break;
			case 6:
				biomeStart = 73728;
				break;
			case 7:
				biomeStart = 86016;
				break;
			case 8:
				biomeStart = 98304;
				break;
			}
			try {
				for(int i = biomeStart; i < biomeStart + BIOME_ARRAY_LENGTH; i++) {
					final Biome biome = this.getBiomeByID(info.data[i]);
					if(biome == null) {
						info.data[i] = this.getBiomeID(season.defaultBiome);
						continue;
					}
					final Biome replacement = season.replacements.get(biome);
					info.data[i] = this.getBiomeID(replacement == null ? season.defaultBiome : replacement);
				}
			}
			catch(final ArrayIndexOutOfBoundsException ex) {
				final LogsManager logsManager = SkyoseasonsAPI.getLogsManager();
				logsManager.log("Exception occured \"ArrayIndexOutOfBoundsException\" :");
				logsManager.log("Biome start : " + biomeStart);
				logsManager.log("Start index : " + info.startIndex);
				logsManager.log("Size : " + info.size);
				logsManager.log("Chunk section number : " + info.chunkSectionNumber);
				for(int i = 0; i != info.data.length - 1; i++) {
	                  final Biome biome = getBiomeByID(info.data[i]);
	                  if(biome != Biome.OCEAN && biome != null && info.data[i] == info.data[i + 1]) {
	                	  logsManager.log("[" + i + "] : " + biome);
	                  }
	             }
			}
			return true;
		}
		return false;
	}

}