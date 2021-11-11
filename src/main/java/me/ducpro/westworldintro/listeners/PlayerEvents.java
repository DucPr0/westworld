package me.ducpro.westworldintro.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldintro.BeginIntro;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if (!plugin.getData().fileExist(uuid)) {
			plugin.getData().createPlayerFile(uuid);
			System.out.println("[WestCore]: Creating new file for " + uuid.toString() + ".yml");
			final Player finalp = e.getPlayer();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					BeginIntro beginintro = new BeginIntro(plugin);
					beginintro.beginIntro(finalp, false);
				}
			}, 1);
		}
		
		if (plugin.getData().getStage(uuid) <= Main.INTRO_STAGES) {
			for (Player p: Bukkit.getOnlinePlayers()) if (!p.getUniqueId().equals(uuid)) {
				p.hidePlayer(plugin, e.getPlayer());
				//Hide player from other players if they are still in intro stages.
			}
		}
		
		for (Player p: Bukkit.getOnlinePlayers()) if (!p.getUniqueId().equals(uuid)) {
			if (plugin.getData().getStage(p.getUniqueId()) <= Main.INTRO_STAGES) e.getPlayer().hidePlayer(plugin, p);
			//Hide other players from player if the other player is intro stages.
		}
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if (plugin.getData().getStage(uuid) <= Main.INTRO_STAGES) {
			e.setCancelled(true);
		}
	}
}
