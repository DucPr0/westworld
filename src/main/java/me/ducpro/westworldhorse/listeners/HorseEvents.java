package me.ducpro.westworldhorse.listeners;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class HorseEvents implements Listener {
	private Main plugin;
	public HorseEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onHorseDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Horse) {
			Horse horse = (Horse) e.getEntity();
			e.setCancelled(true);
			List<Entity> sitting = horse.getPassengers();
			for (Entity entity: sitting) {
				entity.sendMessage(ChatColor.RED + "Ouch! You have been knocked off your horse!");
				horse.removePassenger(entity);
			}
		}
	}
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		Entity[] elist = e.getChunk().getEntities();
		for (int i = 0; i < elist.length; i++) {
			Entity entity = elist[i];
			if (entity instanceof Horse) {
				if (plugin.horseinfo.containsKey(entity.getUniqueId())) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}
}
