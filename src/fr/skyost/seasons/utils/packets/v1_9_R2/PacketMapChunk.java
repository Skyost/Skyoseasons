package fr.skyost.seasons.utils.packets.v1_9_R2;

import net.minecraft.server.v1_9_R2.PacketPlayOutMapChunk;

import org.bukkit.craftbukkit.v1_9_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketMapChunk {
	
private final PacketPlayOutMapChunk packet;
	
	public PacketMapChunk(final org.bukkit.Chunk chunk) {
		this.packet = new PacketPlayOutMapChunk(((CraftChunk)chunk).getHandle(), 0);
	}
	
	public final void send(final Player player) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

}