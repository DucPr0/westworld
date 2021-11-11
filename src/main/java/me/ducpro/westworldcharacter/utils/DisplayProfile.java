package me.ducpro.westworldcharacter.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class DisplayProfile {
	private static String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	private static final String[] msgs = {
			"&e%name%&7:",
			"&eAge: &7%age%",
			"&eDescription: &7%desc%",
			"&eLevel: &7%level%",
			"&ePlaytime: &7%playtime%"
		};
	private static final int DAY_SECONDS = 86400;
	private static final int HOUR_SECONDS = 3600;
	private static final int MINUTE_SECONDS = 60;
	private static String convertTime(int timeleft) {
		int days = timeleft / DAY_SECONDS;
		int hours = (timeleft - DAY_SECONDS * days) / HOUR_SECONDS;
		int minutes = (timeleft - DAY_SECONDS * days - HOUR_SECONDS * hours) / MINUTE_SECONDS;
		int seconds = timeleft - DAY_SECONDS * days - HOUR_SECONDS * hours - MINUTE_SECONDS * minutes;
		String display = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
		return display;
	}
	public static void displayProfile(Main plugin, Player sender, Player target) {
		for (String s: msgs) {
			String msg = s;
			msg = msg.replaceAll("%name%", plugin.getPlayerInfo(target).getName());
			msg = msg.replaceAll("%age%", plugin.getPlayerInfo(target).getAge() + "");
			
			String playerDesc = plugin.getData().getString(target.getUniqueId(), plugin.getData().DESC, "");
			msg = msg.replaceAll("%desc%", playerDesc);
			
			msg = msg.replaceAll("%level%", Integer.toString(target.getLevel()));
			
			int curPlaytime = plugin.getData().getInt(target.getUniqueId(), plugin.getData().PLAYTIME, 0) + 
					plugin.getPlayerInfo(target).getPlaytime();
			msg = msg.replaceAll("%playtime%", convertTime(curPlaytime));
			
			sender.sendMessage(colored(msg));
		}
	}
	public static void displayProfile(Main plugin, CommandSender sender, Player target) {
		for (String s: msgs) {
			String msg = s;
			msg = msg.replaceAll("%name%", plugin.getPlayerInfo(target).getName());
			msg = msg.replaceAll("%age%", plugin.getPlayerInfo(target).getAge() + "");
			
			String playerDesc = plugin.getData().getString(target.getUniqueId(), plugin.getData().DESC, "");
			msg = msg.replaceAll("%desc%", playerDesc);
			
			msg = msg.replaceAll("%level%", Integer.toString(target.getLevel()));
			
			int curPlaytime = plugin.getData().getInt(target.getUniqueId(), plugin.getData().PLAYTIME, 0) + 
					plugin.getPlayerInfo(target).getPlaytime();
			msg = msg.replaceAll("%playtime%", convertTime(curPlaytime));
			
			sender.sendMessage(colored(msg));
		}
	}
}
