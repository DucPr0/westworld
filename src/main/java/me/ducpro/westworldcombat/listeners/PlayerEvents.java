package me.ducpro.westworldcombat.listeners;

import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ducpro.westworldcombat.utils.DuelHandler;
import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

import com.shampaggon.crackshot.events.*;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerMeelee(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			//Disable if none of them are criminals. Fighting players have 1 criminal, so this should work
			Player damager = (Player) e.getDamager();
			Player damagee = (Player) e.getEntity();
			if (!plugin.isWanted(damager) && !plugin.isWanted(damagee)) {
				e.setCancelled(true);
			} else {
				plugin.getPlayerInfo(damager).startCombat(damager);
				plugin.getPlayerInfo(damagee).startCombat(damagee);
			}
			//Check if damager and damagee are duelling. If yes, end the duel.
			if (plugin.getPlayerInfo(damager).isDuellingAgainst(damagee)) {
				e.setCancelled(false);
				DuelHandler.endDuel(plugin, damager, damagee);
			}
		}
	}
	@EventHandler
	public void onPlayerShoot(WeaponDamageEntityEvent e) {
		if (e.getVictim() instanceof Player && e.getPlayer() != null) {
			//Disable if none of them are criminals. Fighting players have 1 criminal, so this should work
			Player damager = e.getPlayer();
			Player damagee = (Player) e.getVictim();
			if (!plugin.isWanted(damager) && !plugin.isWanted(damagee)) {
				e.setCancelled(true);
			} else {
				plugin.getPlayerInfo(damager).startCombat(damager);
				plugin.getPlayerInfo(damagee).startCombat(damagee);
			}
			//Check if damager and damagee are duelling. If yes, end the duel.
			if (plugin.getPlayerInfo(damager).isDuellingAgainst(damagee)) {
				e.setCancelled(false);
				DuelHandler.endDuel(plugin, damager, damagee);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		final UUID uuid = p.getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (p == null) {
					plugin.getFightTeam().removeUUIDFromList(uuid);
					return;
				}
				if (plugin.getFightTeam().isInTeam(p)) {
					plugin.getFightTeam().removePlayer(p);
					if (!plugin.isWanted(p) && plugin.getPlayerInfo(p).wasWanted()) p.sendMessage(ChatColor.GREEN + "You are no longer wanted.");
					if (!plugin.isWanted(p)) plugin.getPlayerInfo(p).setWasWanted(false);
				}
			}
		}, 1);
		if (plugin.getCombatList().isInCombat(p)) {
			plugin.getCombatList().removePlayer(p);
			plugin.getPlayerInfo(p).endCombat(p);
		}
	}
}
