package it.itpao25.craftofclans.handler;

import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class LogHandler {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String PREFIX = ANSI_RED + "[" + ANSI_RESET + ANSI_YELLOW + "CraftOfClans" + ANSI_RESET + ANSI_RED + "] " + ANSI_RESET;
	public static Logger log = Bukkit.getLogger();

	public static void log(String msg) {
		// log.info(PREFIX + ANSI_GREEN + msg + ANSI_RESET);
		log.info(PREFIX + msg);
	}

	public static void error(String msg) {
		// log.severe(PREFIX + ANSI_RED + msg + ANSI_RESET);
		log.severe(PREFIX + msg);
	}

	public static Logger getLogger() {
		if (log != null) {
			return log;
		}
		return null;
	}
}
