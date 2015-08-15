package fr.skyost.seasons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.skyost.seasons.CalendarConfig;
import fr.skyost.seasons.SkyoseasonsAPI;

public class Utils {
	
	/**
	 * Gets the MC server version.
	 * 
	 * @return The NMS version.
	 */
	
	public static final String MC_SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

	/**
	 * Gets the ordinal suffix of a number.
	 * 
	 * @param i The number.
	 * @return The ordinal suffix.
	 * 
	 * @author Bohemian.
	 */

	public static final String getOrdinalSuffix(final int i) {
		final CalendarConfig calendar = SkyoseasonsAPI.getCalendarConfig();
		switch(i % 100) {
		case 11:
		case 12:
		case 13:
			return calendar.ordinalSuffixes.get(0);
		default:
			return calendar.ordinalSuffixes.get(i % 10);
		}
	}

	/**
	 * Rounds a number to the next specified multiple.
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
	
	/**
	 * Serializes a map into a JSON String.
	 * 
	 * @param map The map.
	 * 
	 * @return The serialized map.
	 */

	@SuppressWarnings("unchecked")
	public static final String toJson(final Map<?, ?> map) {
		final JSONObject object = new JSONObject();
		for(final Entry<?, ?> entry : map.entrySet()) {
			object.put(entry.getKey(), entry.getValue());
		}
		return object.toJSONString();
	}

	
	/**
	 * Deserializes a map from a JSON String.
	 * 
	 * @param json
	 * 
	 * @return The deserialized map.
	 */
	
	@SuppressWarnings("unchecked")
	public static final HashMap<Object, Object> fromJson(final String json) {
		return new HashMap<Object, Object>((JSONObject)JSONValue.parse(json));
	}
	
	/**
	 * Center a text.
	 * 
	 * @param text The text.
	 * @param maxWidth The max line width.
	 * 
	 * @return The centered text.
	 */

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
	 * Gets a NMS class (without any import).
	 * 
	 * @param name The class name.
	 * @throws ClassNotFoundException If it cannot fiend the required class.
	 * @return The required class.
	 * 
	 * @author BigTeddy98.
	 */

	public static final Class<?> getMCClass(final String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + MC_SERVER_VERSION + "." + name);
	}
	
	/**
	 * Splits a list.
	 * 
	 * @param list The list.
	 * @param length The length of each list.
	 * 
	 * @return A chopped list.
	 */

	public static final <T> List<List<T>> splitList(final List<T> list, final int length) {
		final List<List<T>> lists = new ArrayList<List<T>>();
		final int size = list.size();
		final int listsSize = list.size() / length;
		for(int i = 0, index = 0; i < length; i++) {
			lists.add(new ArrayList<T>(list.subList(index, Math.min(index + listsSize, size))));
			index += listsSize;
		}
		return lists;
	}
	
	/**
	 * Used to avoid memory leaks.
	 * @param <T> The instance's type.
	 * 
	 * @param The class.
	 * 
	 * @throws IllegalArgumentException Can never happen.
	 * @throws IllegalAccessException Same here.
	 */
	
	public static final <T> void clearFields(final T instance) throws IllegalArgumentException, IllegalAccessException {
		for(final Field field : instance.getClass().getDeclaredFields()) {
			if(Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			field.set(Modifier.isStatic(field.getModifiers()) ? null : instance, null);
		}
	}

}