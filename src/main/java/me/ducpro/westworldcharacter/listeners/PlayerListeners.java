package me.ducpro.westworldcharacter.listeners;

import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ducpro.westworldcharacter.utils.DisplayProfile;
import me.ducpro.westworldcore.main.Main;

public class PlayerListeners implements Listener {
	private final Main plugin;
	public PlayerListeners(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	@EventHandler
	public void onShiftRightClick(PlayerInteractEntityEvent e) {
		if (e.getHand().equals(EquipmentSlot.HAND) && e.getRightClicked() instanceof Player && e.getPlayer().isSneaking()) {
			Player rightClicked = (Player) e.getRightClicked();
			if (!plugin.containsPlayer(rightClicked.getUniqueId())) return; //Is NPC.
			DisplayProfile.displayProfile(plugin, e.getPlayer(), rightClicked);
		}
	}
}
