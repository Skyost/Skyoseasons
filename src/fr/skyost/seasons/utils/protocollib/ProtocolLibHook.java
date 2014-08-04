package fr.skyost.seasons.utils.protocollib;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;

import fr.skyost.seasons.utils.Utils;

public class ProtocolLibHook {
	
	public static final HashMap<Biome, Byte> biomes = new HashMap<Biome, Byte>();

	public ProtocolLibHook() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final Class<?> biomeBase = Utils.getMCClass("BiomeBase");
		for(final Field field : biomeBase.getFields()) {
			for(final Biome biome : Biome.values()) {
				final String biomeName = biome.name();
				final String fieldName = field.getName();
				if(biomeName.equals(fieldName) || biomeName.replace("FOREST", "F").equals(fieldName)) {
					final Object biomeObject = field.get(biomeBase);
					biomes.put(biome, Byte.valueOf(String.valueOf(biomeObject.getClass().getField("id").get(field.get(biomeBase)))));
				}
			}
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new SeasonsPacketListener());
	}

	public static final Biome getBiomeByID(final byte id) {
		for(final Entry<Biome, Byte> entry : ProtocolLibHook.biomes.entrySet()) {
			if(entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static class ChunkInfo {

		public final Player player;
		public final int x;
		public final int z;
		public final int chunkMask;
		public final int extraMask;
		public final boolean hasContinous;
		public byte[] data;
		public int startIndex;
		public int chunkSectionNumber;
		public int extraSectionNumber;
		public int size;

		protected ChunkInfo(final Player player, final int x, final int z, final int chunkMask, final int extraMask, final boolean hasContinous, final byte[] data, final int startIndex) {
			this.player = player;
			this.x = x;
			this.z = z;
			this.chunkMask = chunkMask;
			this.extraMask = extraMask;
			this.hasContinous = hasContinous;
			this.data = data;
			this.startIndex = startIndex;
		}

	}

}
