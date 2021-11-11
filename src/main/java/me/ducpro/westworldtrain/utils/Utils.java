package me.ducpro.westworldtrain.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import me.ducpro.westworldcore.main.Main;

public class Utils {
	public static String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static String getNextStation(Main plugin, int order) {
		if (order == -1) order = 999999;
		ConfigurationSection towns = plugin.getConfigFile().listTowns();
		if (towns.getKeys(false).size() == 0) return null;
		String mintown = null;
		int mintownorder = 999999;
		for (String townname: towns.getKeys(false)) {
			int townorder = towns.getInt(townname + ".order");
			if (townorder > order && townorder < mintownorder) {
				mintownorder = townorder;
				mintown = townname;
			}
		}
		if (mintown == null) {
			//Max station. Cycle back to first one.
			for (String townname: towns.getKeys(false)) {
				int townorder = towns.getInt(townname + ".order");
				if (townorder < mintownorder) {
					mintownorder = townorder;
					mintown = townname;
				}
			}
			return mintown;
		}
		return mintown;
	}
}
