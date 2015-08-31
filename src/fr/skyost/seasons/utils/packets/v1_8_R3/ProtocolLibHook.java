package fr.skyost.seasons.utils.packets.v1_8_R3;

import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.events.PacketContainer;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

public class ProtocolLibHook extends AbstractProtocolLibHook {

	public ProtocolLibHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		super(plugin);
	}

	@Override
	protected final void translateMapChunk(final PacketContainer packet, final Player player, final Season season) {
		final ChunkMap chunk = (ChunkMap)packet.getModifier().read(2);
		if(chunk.a != null) {
			translateChunkInfo(new ChunkInfo(null, chunk.b, 0, getOrDefault(packet.getBooleans().readSafely(0), true), chunk.a, 0), season);
		}
	}

	@Override
	protected final void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season) {
		int dataStartIndex = 0;
		final ChunkMap[] chunks = (ChunkMap[])packet.getModifier().read(2);
		for(int chunkNum = 0; chunkNum < chunks.length; chunkNum++) {
			final ChunkInfo info = new ChunkInfo(player, chunks[chunkNum].b, 0, true, chunks[chunkNum].a, 0);
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
		if(info.hasContinous) {
			final int biomeStart = info.data.length - BIOME_ARRAY_LENGTH;
			for(int i = biomeStart; i < info.data.length; i++) {
				final Biome biome = this.getBiomeByID(info.data[i]);
				if(biome == null) {
					info.data[i] = defaultBiomeId;
					continue;
				}
				final Biome replacement = season.replacements.get(biome);
				info.data[i] = replacement == null ? defaultBiomeId : this.getBiomeID(replacement);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public final void refreshChunk(final World world, final Chunk chunk) {
		for(final Player player : Bukkit.getOnlinePlayers()) {
			new PacketMapChunk(chunk).send(player);
		}
		world.refreshChunk(chunk.getX(), chunk.getZ());
	}

}