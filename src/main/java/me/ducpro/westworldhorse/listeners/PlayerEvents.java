package me.ducpro.westworldhorse.listeners;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhorse.guis.ListGUI;
import me.ducpro.westworldhorse.info.HorseInfo;
import me.ducpro.westworldhorse.utils.Utils;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		this.plugin = plugin;
	}
	private final String speeds[] = {"slow", "normal", "fast"};
	@EventHandler
	public void onTameHorse(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Horse) {
			Player p = e.getPlayer();
			Horse horse = (Horse) e.getRightClicked();
			if (horse.isTamed()) return; //Prevent player - owned horses from being tamed.
			if (plugin.horseinfo.containsKey(horse.getUniqueId())) {
				//Prevent escaping horses from being tamed.
				if (plugin.horseinfo.get(horse.getUniqueId()).isRunning()) e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			if (p.getInventory().getItemInMainHand().getType() != Material.SADDLE) {
				p.sendMessage(ChatColor.RED + "You must be holding a saddle to wrangle the horse.");
				return;
			}
			if (!plugin.getData().canHaveHorse(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You have reached your horse limit. Sell your current horses to wrangle this one.");
				return;
			}
			int success = new Random().nextInt(2);
			if (success == 1) {
				//Add horse to player's config file.
				int randomIndex = new Random().nextInt(3);
				String configID = plugin.getData().addHorse(p.getUniqueId(), horse, speeds[randomIndex]);
				Utils.spawnHorse(plugin, configID, horse.getLocation(), p);
				horse.remove();
				p.sendMessage(ChatColor.GREEN + "Successfully wrangled the horse.");
			} else {
//				horse.remove();
				plugin.horseinfo.put(horse.getUniqueId(), new HorseInfo(plugin, horse, true));
				p.sendMessage(ChatColor.RED + "Failed to wrangle the horse. It is running away!");
			}
			ItemStack istack = p.getInventory().getItemInMainHand();
			int amount = istack.getAmount();
			amount--;
			if (amount > 0) {
				istack.setAmount(amount);
			} else {
				p.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		plugin.getPlayerInfo(p).clearAllHorses();
	}
	@EventHandler
	public void onStablemanInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player npc = (Player) e.getRightClicked();
			if (npc.getName().equals(Utils.colored("&a&lSTABLEMAN"))) {
				ListGUI.openListGUI(plugin, e.getPlayer(), 1);
			}
		}
		if (e.getRightClicked() instanceof Villager) {
			if (e.getRightClicked().getName().equals(Utils.colored("&a&lSTABLEMAN"))) {
				ListGUI.openListGUI(plugin, e.getPlayer(), 1);
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onTellHorseName(AsyncPlayerChatEvent e) {
		if (plugin.getPlayerInfo(e.getPlayer()).isRequestingHorseName()) {
			e.setCancelled(true);
			String horsename = e.getMessage();
			String configID = plugin.getPlayerInfo(e.getPlayer()).getHorseConfigID();
			plugin.getData().renameHorse(e.getPlayer().getUniqueId(), configID, horsename);
			plugin.getPlayerInfo(e.getPlayer()).setRequestingHorseName(false);
			e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully changed the horse's name to " + horsename);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRideHorse(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Horse) {
			Player p = e.getPlayer();
			Horse horse = (Horse) e.getRightClicked();
			if (!horse.isTamed()) return;
			UUID owner = plugin.horseinfo.get(horse.getUniqueId()).getOwner();
			if (!p.getUniqueId().equals(owner)) {
				e.setCancelled(true);
				String ownername = plugin.getPlayerInfo(owner).getName();
				p.sendMessage(ChatColor.RED + "You cannot ride " + ownername + "'s horse!");
			} else {
				e.setCancelled(false);
			}
		}
	}
}
