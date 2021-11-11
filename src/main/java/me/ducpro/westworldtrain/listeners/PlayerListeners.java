package me.ducpro.westworldtrain.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.utils.GetItemDamage;
import me.ducpro.westworldtrain.TrainManager;
import me.ducpro.westworldtrain.guis.ConductorGUI;
import me.ducpro.westworldtrain.utils.GetTown;
import me.ducpro.westworldtrain.utils.Utils;

public class PlayerListeners implements Listener {
	private Main plugin;
	public PlayerListeners(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onRightClickConductor(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player || e.getRightClicked() instanceof Villager) {
			if (e.getRightClicked().getName().equals(ChatColor.translateAlternateColorCodes('&', "&a&lCONDUCTOR"))) {
				if (plugin.isWanted(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.RED + "You are a wanted criminal! You can not broad the train!");
					return;
				}
				ConductorGUI.openConductorGUI(e.getPlayer());
			}
		}
	}
	@EventHandler
	public void onConductorGUIClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 9 && inv.getName().equals(Utils.colored("&fConductor"))) {
			e.setCancelled(true);
			if (e.getRawSlot() == 4) {
				Player p = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
				if (plugin.train == null) {
					p.sendMessage(ChatColor.RED + "The train is not in operation.");
					return;
				}
				
				int curbalance = (int) plugin.getEconomy().getBalance(p);
				if (curbalance < 5) {
					p.sendMessage(ChatColor.RED + "You cannot afford to board the train.");
					return;
				}
				plugin.getEconomy().withdrawPlayer(p, 5);
				
				String curTown = GetTown.getTown(plugin, p);
				plugin.getPlayerInfo(p).setLastTown(curTown);
				
				plugin.train.addPlayer(p);
				p.sendMessage(ChatColor.GREEN + "You have boarded the train.");
				p.closeInventory();
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerGetOffTrain(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame iframe = (ItemFrame) e.getRightClicked();
			ItemStack istack = iframe.getItem();
			if (istack.getType() == Material.DIAMOND_HOE && GetItemDamage.getDamage(istack) == 1554) {
				if (plugin.getPlayerInfo(e.getPlayer()).getStage() <= Main.INTRO_STAGES) return; //Door in intro.
				e.setCancelled(true);
				if (!plugin.train.isAtStation()) {
					e.getPlayer().sendMessage(ChatColor.RED + "You cannot exit now. The train is still moving.");
					return;
				}
				plugin.train.removePlayer(e.getPlayer(), true);
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String lasttown = plugin.getPlayerInfo(p).getLastTown();
		if (lasttown != null) {
			//Player is currently on the train.
			plugin.train.removePlayer(p, false);
		}
	}
}
