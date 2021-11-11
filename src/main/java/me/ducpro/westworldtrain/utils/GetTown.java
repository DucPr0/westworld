package me.ducpro.westworldtrain.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import me.ducpro.westworldcore.main.Main;

public class GetTown {
	public static String getTown(Main plugin, Player p) {
		ConfigurationSection sec = plugin.getConfigFile().listTowns();
		double minDistance = -1;
		String minTown = null;
		for (String townname: sec.getKeys(false)) {
			Location townloc = plugin.getConfigFile().getLocation("trains." + townname);
			double curDistance = townloc.distanceSquared(p.getLocation());
			if (minDistance == -1 || curDistance < minDistance) {
				minDistance = curDistance;
				minTown = townname;
			}
		}
		return minTown;
	}
}
