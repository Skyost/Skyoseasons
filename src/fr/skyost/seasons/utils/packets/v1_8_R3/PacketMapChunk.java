package fr.skyost.seasons.utils.packets.v1_8_R3;

import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;

import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketMapChunk {
	
	private final PacketPlayOutMapChunk packet;
	
	public PacketMapChunk(final org.bukkit.Chunk chunk) {
		this.packet = new PacketPlayOutMapChunk(((CraftChunk)chunk).getHandle(), true, 20);
	}
	
	public final void send(final Player player) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

}