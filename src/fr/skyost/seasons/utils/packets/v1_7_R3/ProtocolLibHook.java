package fr.skyost.seasons.utils.packets.v1_7_R3;

import java.util.Collections;
import java.util.Set;
import java.util.zip.Deflater;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.MapMaker;

import fr.skyost.seasons.Season;
import fr.skyost.seasons.utils.packets.AbstractProtocolLibHook;

/**
 * @author Comphenix.
 */

public class ProtocolLibHook extends AbstractProtocolLibHook {
	
	private final Set<Object> changed = Collections.newSetFromMap(new MapMaker().weakKeys().<Object, Boolean>makeMap());

	public ProtocolLibHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		super(plugin);
		final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.MAP_CHUNK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				finalizeMapChunk(event.getPacket());
			}

		});
	}

	@Override
	protected final void translateMapChunk(final PacketContainer packet, final Player player, final Season season) {
		final StructureModifier<Integer> ints = packet.getIntegers();
		final byte[] data = packet.getByteArrays().read(1);
		if(data != null) {
			final ChunkInfo info = new ChunkInfo(player, ints.read(2), ints.read(3), getOrDefault(packet.getBooleans().readSafely(0), true), data, 0);
			if(this.translateChunkInfo(info, season)) {
				changed.add(packet);
			}
		}
	}

	@Override
	protected final void translateMapChunkBulk(final PacketContainer packet, final Player player, final Season season) {
		final StructureModifier<int[]> intArrays = packet.getIntegerArrays();
		final StructureModifier<byte[]> byteArrays = packet.getSpecificModifier(byte[].class);
		int dataStartIndex = 0;
		final int[] chunkMask = intArrays.read(2);
		final int[] extraMask = intArrays.read(3);
		for(int chunkNum = 0; chunkNum < chunkMask.length; chunkNum++) {
			final ChunkInfo info = new ChunkInfo(player, chunkMask[chunkNum], extraMask[chunkNum], true, byteArrays.read(1), 0);
			if(info.data == null || info.data.length == 0) {
				info.data = packet.getSpecificModifier(byte[][].class).read(0)[chunkNum];
			}
			else {
				info.startIndex = dataStartIndex;
			}
			this.translateChunkInfo(info, season);
			dataStartIndex += info.size;
		}
	}
	
	@Override
	protected final boolean translateChunkInfo(final ChunkInfo info, final Season season) {
		if(info.hasContinous) {
			for(int i = 0; i < CHUNK_SEGMENTS; i++) {
				if((info.chunkMask & (1 << i)) > 0) {
					info.chunkSectionNumber++;
				}
				if((info.extraMask & (1 << i)) > 0) {
					info.extraSectionNumber++;
				}
			}
			info.size = BYTES_PER_NIBBLE_PART * ((NIBBLES_REQUIRED + (info.player.getWorld().getEnvironment() == Environment.NORMAL ? 1 : 0)) * info.chunkSectionNumber + info.extraSectionNumber) + (info.hasContinous ? BIOME_ARRAY_LENGTH : 0);
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
	
}