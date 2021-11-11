package me.ducpro.westworldranks.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

import me.ducpro.westworldcore.main.Main;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.SPECTATOR) {
			p.teleport(plugin.getPlayerInfo(p).getLastNoclip());
			p.setGameMode(GameMode.ADVENTURE);
		}
	}
}
