package me.ducpro.westworldcore.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import net.md_5.bungee.api.ChatColor;

public class DestroyArmorstands {
	public static void destroy() {
		List<World> list = Bukkit.getWorlds();
		for (World world: list) {
			List<Entity> elist = world.getEntities();
			for (Entity e: elist) {
				if (e.getType() == EntityType.ARMOR_STAND && e.getCustomName() != null && 
						e.getCustomName().equals(ChatColor.RED + "Chair")) {
					e.eject();
					e.remove();
				}
			}
		}
	}
}
