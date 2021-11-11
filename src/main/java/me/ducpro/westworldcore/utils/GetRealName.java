package me.ducpro.westworldcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class GetRealName {
	public static String getRealName(Main plugin, String name) {
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (plugin.getPlayerInfo(p).getStage() <= Main.INTRO_STAGES) continue;
			if (plugin.getPlayerInfo(p).getName().equals(name)) return p.getName();
		}
		return null;
	}
}
