package me.ducpro.westworldranks.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class SpectatorReset {
	public static void resetSpectators(Main plugin) {
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.teleport(plugin.getPlayerInfo(p).getLastNoclip());
			}
		}
	}
}
