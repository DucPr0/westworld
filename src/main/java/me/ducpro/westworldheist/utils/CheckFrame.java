package me.ducpro.westworldheist.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

public class CheckFrame {
	public static ItemFrame getFrame(Location loc, double radius) {
		for (Entity entity: loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if (entity instanceof ItemFrame) return ((ItemFrame) entity);
		}
		return null;
	}
}
