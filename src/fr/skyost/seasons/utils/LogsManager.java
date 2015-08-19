package fr.skyost.seasons.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class LogsManager {
	
	private Logger logger;
	private File logsDirectory;
	
	private static final String DATE_HOUR = "HH:mm:ss";
	private static final String DATE_DAY = "yyyy-MM-dd";
	
	public enum DateType {
		HOUR,
		DAY;
	}
	
	public LogsManager(final Plugin plugin) {
		this(plugin.getLogger(), new File(plugin.getDataFolder() + File.separator + "logs"));
	}
	
	public LogsManager(final Logger logger, final File logsDirectory) {
		this.logger = logger;
		this.logsDirectory = logsDirectory;
		if(logsDirectory != null && !logsDirectory.exists()) {
			logsDirectory.mkdir();
		}
	}
	
	public final void setLogger(final Logger logger) {
		this.logger = logger;
	}
	
	public final void setLogsDirectory(final File logsDirectory) {
		this.logsDirectory = logsDirectory;
		if(logsDirectory != null && !logsDirectory.exists()) {
			logsDirectory.mkdir();
		}
	}
	
	private final String date(final DateType date) {
		return new SimpleDateFormat(date == DateType.HOUR ? DATE_HOUR : DATE_DAY).format(new Date());
	}
	
	public final void log(final String message) {
		log(message, Level.INFO);
	}
	
	public final void log(final String message, final Level level) {
		log(message, level, null);
	}
	
	public final void log(String message, final Level level, final World world) {
		final String prefix = world == null ? "" : "[" + world.getName() + "] ";
		message = ChatColor.stripColor(message);
		if(logger != null) {
			logger.log(level, prefix + message);
		}
		if(logsDirectory != null) {
			try {
				final File dayFile = new File(logsDirectory, date(DateType.DAY) + ".log");
				if(!dayFile.exists()) {
					dayFile.createNewFile();
				}
				Files.append("[" + date(DateType.HOUR) + "] [" + level + "] " + prefix + message + System.lineSeparator(), dayFile, Charsets.UTF_8);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
}