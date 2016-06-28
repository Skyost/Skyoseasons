package fr.skyost.seasons.utils.packets;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.SkyoseasonsAPI;

public class Listener3 {
	
	public static final void registerEvent(final Plugin plugin, final AbstractProtocolLibHook hook) {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

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
					hook.translateMapChunk(packet, event.getPlayer(), world.season);
				}
				else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
					hook.translateMapChunkBulk(packet, event.getPlayer(), world.season);
				}
			}

		});
	}

}
