package me.ducpro.westworldchat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class LOOCCommand {
	public static void process(Main plugin, Player p) {
		p.sendMessage(ChatColor.GREEN + "You are now in local OOC chat.");
		plugin.getPlayerInfo(p).setChatMode(3);
		return;
	}
	public static void sendLOOCMessage(Main plugin, Player p, String args[]) {
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message += args[i];
			if (i < args.length - 1) message += " ";
		}
		String pref = plugin.getPlayerInfo(p).getPrefix(), name = plugin.getPlayerInfo(p).getName();
		String fullmsg = "&b(OOC) " + pref + "&b" + name + ": " + message;
//		fullmsg = fullmsg.replace("%prefix%", plugin.getPlayerInfo(p).getPrefix()).replace("%name%", plugin.getPlayerInfo(p).getName()).replace("%message%", message);
//		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', fullmsg));
		fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
		Bukkit.getLogger().info(fullmsg);
		p.sendMessage(fullmsg);
		for (Entity entity: p.getNearbyEntities(30, 30, 30)) {
			if (entity instanceof Player) {
				Player target = (Player) entity;
				if (plugin.containsPlayer(target.getUniqueId()) && plugin.getPlayerInfo(target).getListenOOC()) target.sendMessage(fullmsg);
			}
		}
	}
}
