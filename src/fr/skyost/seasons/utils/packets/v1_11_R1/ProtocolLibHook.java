package fr.skyost.seasons.utils.packets.v1_11_R1;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.events.PacketContainer;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;
import net.minecraft.server.v1_11_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_11_R1.PacketPlayOutUnloadChunk;
import net.minecraft.server.v1_11_R1.PlayerConnection;

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
		final PacketPlayOutUnloadChunk packetUnload = new PacketPlayOutUnloadChunk(chunk.getX(), chunk.getZ());
		final PacketPlayOutMapChunk packetLoad = new PacketPlayOutMapChunk(((CraftChunk)chunk).getHandle(), 0xffff);
		for(final Player player : chunk.getWorld().getPlayers()) {
			final PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
			
			connection.sendPacket(packetUnload);
			connection.sendPacket(packetLoad);
		}
	}

}