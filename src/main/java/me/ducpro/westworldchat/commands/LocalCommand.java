package me.ducpro.westworldchat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ducpro.westworldchat.Utils;
import me.ducpro.westworldcore.main.Main;

public class LocalCommand {
	public static void process(Main plugin, Player p) {
		p.sendMessage(ChatColor.GREEN + "You are now in Local chat.");
		plugin.getPlayerInfo(p).setChatMode(0);
		return;
	}
	public static void sendLocalMessage(Main plugin, Player p, String args[]) {
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message += args[i];
			if (i < args.length - 1) message += " ";
		}
		String quote = "\"";
		String pref = plugin.getPlayerInfo(p).getPrefix(), name = plugin.getPlayerInfo(p).getName();
		String fullmsg = pref + "&f" + name + ": " + quote + message + "&r&f" + quote;
//		fullmsg = fullmsg.replace("%prefix%", plugin.getPlayerInfo(p).getPrefix()).replace("%name%", plugin.getPlayerInfo(p).getName()).replace("%message%", message);
		fullmsg = Utils.getMessage(plugin, p, fullmsg);
		fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
		Bukkit.getLogger().info(fullmsg);
		p.sendMessage(fullmsg);
		for (Entity entity: p.getNearbyEntities(20, 20, 20)) {
			if (entity instanceof Player) {
				entity.sendMessage(fullmsg);
			}
		}
	}
}
