package fr.skyost.seasons.utils.packets.v1_9_R2;

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

	public ProtocolLibHook(final Plugin protocolLib, final Plugin skyoseasons) throws PacketPluginHookInitializationException {
		super(protocolLib, skyoseasons);
	}

	@Override
	protected final void translateMapChunk(final PacketContainer packet, final Player player, final Season season) {
		final byte[] d = packet.getByteArrays().read(0);
		if(d != null) {
			translateChunkInfo(new ChunkInfo(player, packet.getIntegers().read(2), 0, getOrDefault(packet.getBooleans().readSafely(0), true), d, 0), season);
		}
	}

	@Override
	protected final void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season) {}

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
		world.refreshChunk(chunk.getX(), chunk.getZ());
	}

}