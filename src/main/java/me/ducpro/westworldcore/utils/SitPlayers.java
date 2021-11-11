package me.ducpro.westworldcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class SitPlayers {
	public static void sit(Player p, double ydiff) {
		ArmorStand as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(new Vector(0, ydiff, 0)), EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setCustomName(ChatColor.RED + "Chair");
		as.addPassenger(p);
		as.setGravity(false);
	}
}
