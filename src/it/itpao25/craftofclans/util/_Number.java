package it.itpao25.craftofclans.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class _Number {

	/**
	 * Esplodo un numero in solo numero senza decimali: esempio: 123,0111 -> 123
	 */
	public static String fmt(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}

	/**
	 * Lista dei giocatori all'interno del server Prevenendo i metodi obsoleti
	 */
	public static List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<Player>();
		for (World w : Bukkit.getWorlds()) {
			for (Player p : w.getPlayers()) {
				players.add(p);
			}
		}
		return players;
	}

	/**
	 * Controllo se la stringa è un numero
	 */
	public static boolean isNumero(String s) {
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException errore) {
			return false;
		}
		return true;
	}

	public static int random_int(int Min, int Max) {
		return (int) (Math.random() * (Max - Min)) + Min;
	}

	/**
	 * Versione del server
	 */
	public static String getVersion() {
		return Bukkit.getVersion();
	}

	/**
	 * Ritorno con il centro di un blocco
	 */
	public static Location getCenter(Location loc) {
		return new Location(loc.getWorld(), getRelativeCoord(loc.getBlockX()), getRelativeCoord(loc.getBlockY()), getRelativeCoord(loc.getBlockZ()));
	}

	private static double getRelativeCoord(int i) {
		double d = i;
		d = d < 0 ? d - .5 : d + .5;
		return d;
	}

	public static World getWorldPrincipale() {
        return Bukkit.getWorld("clansworld");
	}

	private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
	static {
		suffixes.put(1_000L, "k");
		suffixes.put(1_000_000L, "M");
		suffixes.put(1_000_000_000L, "G");
		suffixes.put(1_000_000_000_000L, "T");
		suffixes.put(1_000_000_000_000_000L, "P");
		suffixes.put(1_000_000_000_000_000_000L, "E");
	}

	public static String coolFormat(Double value) {
		return coolFormat(value.longValue());
	}

	public static String coolFormat(Long value) {
		// Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Long.MIN_VALUE)
			return coolFormat(Long.MIN_VALUE + 1);
		if (value < 0)
			return "-" + coolFormat(-value);
		if (value < 1000)
			return Long.toString(value); // deal with easy case

		Entry<Long, String> e = suffixes.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		long truncated = value / (divideBy / 10); // the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	/**
	 * Formatto le cifre
	 */
	public static String showNumero(Number amount) {
		NumberFormat format = DecimalFormat.getInstance();
		format.setRoundingMode(RoundingMode.FLOOR);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(2);
		return format.format(amount);
	}

	public static String getProgressHolo(double actuals, double max) {
		
		int percentuale = (int) ((int) actuals / max * 20);
		StringBuilder finale = new StringBuilder();
		for (int i = 0; i < percentuale + 1; i++) {
			String percn = "&a";
			if (percentuale <= 6) {
				percn = ("&e");
			}
			if (percentuale <= 5) {
				percn = ("&6");
			}
			if (percentuale <= 3) {
				percn = ("&4");
			}
			finale.append(percn + "|");
		}
		for (int i2 = 0; i2 < 20 - percentuale; i2++) {
			finale.append("&8|");
		}
		return Color.translate(finale.toString());

	}
	
	/**
     * Converto il tempo in "time ago"
     * Esempio "10d" come "10 giorni fa"
     */
	public static String getFrom(String data) {
		try {
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date past = format.parse(data);
	        Date now = new Date();
	        if(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) >= 1) {
	        	return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + "d";
	        }
	        if(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) >= 1) {
	        	return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime())+ "h";
	        }
	        if(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) >= 1) {
	        	return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m";
	        }
	        return "A moment ago";
	    }
	    catch (Exception j){
	        j.printStackTrace();
	    }
		return null;
	}
}
