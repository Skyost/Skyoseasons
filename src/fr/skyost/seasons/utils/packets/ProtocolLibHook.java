package fr.skyost.seasons.utils.packets;

import java.util.Collections;
import java.util.Set;
import java.util.zip.Deflater;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;

/**
 * @author Comphenix.
 */

public class ProtocolLibHook extends PacketPluginHook {

	private final Set<Object> changed = Collections.newSetFromMap(new MapMaker().weakKeys().<Object, Boolean>makeMap());

	public ProtocolLibHook(final Plugin plugin) throws PacketPluginHookInitializationException {
		final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				final SeasonWorld world = SkyoseasonsAPI.getSeasonWorldExact(player.getWorld());
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
		manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.MAP_CHUNK) {

			@Override
			public final void onPacketSending(final PacketEvent event) {
				finalizeMapChunk(event.getPacket());
			}

		});
	}

	private final void translateMapChunk(final PacketContainer packet, final Player player, final SeasonWorld world) throws FieldAccessException {
		final byte[] data = packet.getByteArrays().read(1);
		if(data != null) {
			final StructureModifier<Integer> ints = packet.getIntegers();
			final ChunkInfo info = new ChunkInfo(player, ints.read(0), ints.read(1), ints.read(2), ints.read(3), getOrDefault(packet.getBooleans().readSafely(0), true), data, 0);
			if(this.translateChunkInfo(info, world.season)) {
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
			this.translateChunkInfo(info, world.season);
			dataStartIndex += info.size;
		}
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
