package me.ducpro.westworldcharacter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.ducpro.westworldcore.main.Main;

public class CrimeListeners implements Listener {
	private final Main plugin;
	public CrimeListeners(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerKillPlayer(PlayerDeathEvent e) {
		Player killed = e.getEntity();
		if (plugin.isWanted(killed)) return;
		Player killer = killed.getPlayer();
		if (killer == null) return;
		if (killer.getUniqueId().equals(killed.getUniqueId())) return; //Suicides
		int curKills = plugin.getData().getInt(killer.getUniqueId(), plugin.getData().KILLED, 0);
		plugin.getData().setInt(killer.getUniqueId(), plugin.getData().KILLED, curKills + 1);
	}
}
