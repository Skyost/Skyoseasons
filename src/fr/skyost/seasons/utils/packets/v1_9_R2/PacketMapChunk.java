package fr.skyost.seasons.utils.packets.v1_9_R2;

import net.minecraft.server.v1_9_R2.PacketPlayOutMapChunk;

import org.bukkit.craftbukkit.v1_9_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketMapChunk {
	
	private final net.minecraft.server.v1_9_R2.Chunk chunk;
	
	public PacketMapChunk(final org.bukkit.Chunk chunk) {
		this.chunk = ((CraftChunk)chunk).getHandle();
	}
	
	public final void send(final Player player) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 0));
	}

}