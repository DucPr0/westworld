package me.ducpro.westworldheist.utils;

import org.bukkit.block.Block;
import org.bukkit.Location;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;

public class GetBank {
	public static String getBank(Main plugin, Location loc) {
		int locx = loc.getBlockX(), locy = loc.getBlockY(), locz = loc.getBlockZ();
		for (int x = locx - 5; x <= locx + 5; x++) {
			for (int y = locy - 5; y <= locy + 5; y++) {
				for (int z = locz - 5; z <= locz + 5; z++) {
					Coordinates coord = new Coordinates(loc.getWorld(), x, y, z);
					if (plugin.chestinfo.containsKey(coord)) {
						return plugin.chestinfo.get(coord).getBank();
					}
				}
			}
		}
		return null;
	}
}
