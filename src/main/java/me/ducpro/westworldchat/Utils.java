package me.ducpro.westworldchat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class Utils {
	public static String getMessage(Main plugin, Player p, String message) {
		String finalmessage = "";
		int cntstars = 0;
		for (int i = 0; i < message.length(); i++) {
			if (message.charAt(i) == '*') {
				cntstars++;
				if (cntstars == 1) {
					finalmessage += "&7&o" + plugin.getPlayerInfo(p).getName() + " ";
				} else if (cntstars == 2) {
					finalmessage += "&r&f";
				} else finalmessage += "*";
			} else finalmessage += message.charAt(i);
		}
//		Bukkit.broadcastMessage("cntstars = " + cntstars + " finalmsg = " + finalmessage + " ogmsg = " + message);
		return finalmessage;
	}
}
