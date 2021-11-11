package me.ducpro.westworldhorse.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;

import me.ducpro.westworldcore.main.Main;

public class KillHorses {
	public static void killHorses(Main plugin) {
		for (World world: Bukkit.getWorlds()) {
			for (Entity e: world.getEntities()) {
				if (e instanceof Horse) {
					Horse h = (Horse) e;
					if (h.isTamed()) h.remove();
//					if (plugin.horseinfo.containsKey(h.getUniqueId())) h.remove();
				}
			}
		}
	}
}
