package me.ducpro.westworldheist.load;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldheist.info.ChestInfo;

public class LoadChests {
	public static void loadChests(Main plugin) {
		Set<String> banks = plugin.getConfigFile().getBanks().getKeys(false);
		for (String bankname: banks) {
			ConfigurationSection chests = plugin.getConfigFile().getChests(bankname);
			for (String chestname: chests.getKeys(false)) {
				if (chestname.equals("chestcount")) continue;
				if (chestname.equals("lastrobbed")) continue;
				int x = chests.getInt(chestname + ".x"), y = chests.getInt(chestname + ".y"), z = chests.getInt(chestname + ".z");
				World world = Bukkit.getWorld(chests.getString(chestname + ".world"));
				int amount = chests.getInt(chestname + ".amount");
				Coordinates coord = new Coordinates(world, x, y, z);
//				Bukkit.broadcastMessage("Loaded " + coord.getBlock().getX() + " " + coord.getBlock().getY() + " " + coord.getBlock().getZ());
				plugin.chestinfo.put(coord, new ChestInfo(bankname, amount, coord));
			}
		}
	}
}
