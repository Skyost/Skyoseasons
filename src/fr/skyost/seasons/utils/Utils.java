package fr.skyost.seasons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.skyost.seasons.Skyoseasons;

public class Utils {
	
	public enum ModificationCause {
		PLUGIN,
		PLAYER;
	}
	
	/**
	 * Get the ordinal suffix of a number.
	 * 
	 * @param i The number.
	 * @return The ordinal suffix.
	 * 
	 * @author Bohemian.
	 */
	
	public static final String getOrdinalSuffix(final int i) {
		switch(i % 100) {
		case 11:
		case 12:
		case 13:
			return Skyoseasons.calendar.ordinalSuffixes.get(0);
		default:
			return Skyoseasons.calendar.ordinalSuffixes.get(i % 10);
		}
	}
	
	/**
	 * Round a number to the next specified multiple.
	 * 
	 * @param number The number.
	 * @param value The multiple.
	 * @return The rounded int.
	 * 
	 * @author Arkia.
	 */
	
	public static final int round(final double number, final int value) {
		return (int)(Math.ceil(number / value) * value);
	}
	
	@SuppressWarnings("unchecked")
	public static final String toJson(final HashMap<Object, Object> map) {
		final JSONObject object = new JSONObject();
		for(final Entry<Object, Object> entry : map.entrySet()) {
			object.put(entry.getKey(), entry.getValue());
		}
		return object.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static final HashMap<Object, Object> fromJson(final String json) {
		return new HashMap<Object, Object>((JSONObject)JSONValue.parse(json));
	}
	
	public static final String centerText(final String text, final int maxWidth) {
		return StringUtils.repeat(" ", (int)Math.round((maxWidth - 1.4 * ChatColor.stripColor(text).length()) / 2)) + text;
	}
	
	/**
	 * Copy a file or a folder.
	 * 
	 * @param sourceLocation The path of the file (or the folder).
	 * @param targetLocation The target.
	 * 
	 * @author Mkyong.
	 */
	
	public static final void copy(final File sourceLocation, final File targetLocation) throws IOException {
		if(sourceLocation.isDirectory()) {
			if(!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			final String[] children = sourceLocation.list();
			for(int i = 0; i < children.length; i++) {
				copy(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		}
		else {
			final InputStream in = new FileInputStream(sourceLocation);
			final OutputStream out = new FileOutputStream(targetLocation);
			final byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	/**
	 * Get a NMS class (without any import).
	 * 
	 * @param name The class name.
	 * @throws ClassNotFoundException If it cannot fiend the required class.
	 * @return The required class.
	 * 
	 * @author BigTeddy98.
	 */
	
	public static final Class<?> getMCClass(final String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + name);
	}
	
}
