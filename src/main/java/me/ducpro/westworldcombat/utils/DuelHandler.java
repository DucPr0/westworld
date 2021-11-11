package me.ducpro.westworldcombat.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class DuelHandler {
	public static void beginDuel(Main plugin, Player p, Player target) {
		plugin.getPlayerInfo(p).setDuelling(target.getUniqueId());
		plugin.getPlayerInfo(target).setDuelling(p.getUniqueId());
	}
	public static void endDuel(Main plugin, Player winner, Player loser) {
		plugin.getPlayerInfo(winner).setDuelling(null);
		plugin.getPlayerInfo(loser).setDuelling(null);
		
		String msg = ChatColor.GREEN + plugin.getPlayerInfo(winner).getName() + " won a duel against " + 
							plugin.getPlayerInfo(loser).getName() + "!";
		winner.sendMessage(msg);
		for (Entity entity: winner.getNearbyEntities(30, 30, 30)) {
			if (entity instanceof Player) {
				((Player) entity).sendMessage(msg);
			}
		}
	}
}
