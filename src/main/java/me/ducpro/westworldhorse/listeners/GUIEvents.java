package me.ducpro.westworldhorse.listeners;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.PlayerInfo;
import me.ducpro.westworldhorse.guis.EditGUI;
import me.ducpro.westworldhorse.guis.ListGUI;
import me.ducpro.westworldhorse.info.HorseInfo;
import me.ducpro.westworldhorse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class GUIEvents implements Listener {
	private Main plugin;
	public GUIEvents(Main plugin) {
		this.plugin = plugin;
	}
	private final int HORSES_PER_PAGE = 27;
	@EventHandler
	public void onListClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 36 && inv.getName().equals(Utils.colored("&fYour Horses:"))) {
			e.setCancelled(true);
			
			if (e.getRawSlot() >= 0 && e.getRawSlot() < 27) {
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getHorsePage();
				int configIndex = HORSES_PER_PAGE * (curpage - 1) + e.getRawSlot();
				String configID = null;
				ConfigurationSection horseSection = plugin.getData().getHorseList(e.getWhoClicked().getUniqueId());
				Set<String> configKeys = horseSection.getKeys(false);
				int curind = 0;
				for (String key: configKeys) {
					if (key.equals("horse-count")) continue;
					if (curind == configIndex) {
						configID = key;
						break;
					}
					curind++;
				}
				if (configID == null) return;
				PlayerInfo pinfo = plugin.getPlayerInfo(e.getWhoClicked());
				pinfo.setHorseConfigID(configID);
				UUID horseUUID = pinfo.isSpawned(configID);
				if (horseUUID != null) {
					pinfo.setHorseUUID(horseUUID);
					EditGUI.despawnGUI(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()));
				} else {
					EditGUI.spawnGUI(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()));
				}
				
			} else if (e.getRawSlot() == 27) {
				if (inv.getItem(27) == null) return;
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getHorsePage();
				ListGUI.openListGUI(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage - 1);
				
			} else if (e.getRawSlot() == 35) {
				if (inv.getItem(35) == null) return;
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getHorsePage();
				ListGUI.openListGUI(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage + 1);
				
			}
		}
	}
	@EventHandler
	public void onSpawnGUIClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 9 && inv.getName().equals(Utils.colored("&fEdit Horse"))) {
			e.setCancelled(true);
			if (e.getRawSlot() == 2) {
				//Spawn
				Player p = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
				PlayerInfo pinfo = plugin.getPlayerInfo(p);
				String configID = pinfo.getHorseConfigID();
				Location spawnloc = Utils.getSpawnLocation(plugin, p.getLocation());
				String name = plugin.getData().getHorseName(p.getUniqueId(), configID);
				name = name.replaceAll("%roleplayname%", pinfo.getName());
				
				Utils.spawnHorse(plugin, configID, spawnloc, p);
				
				p.closeInventory();
				p.sendMessage(ChatColor.GREEN + "Successfully spawned " + name + ".");
			} else if (e.getRawSlot() == 4) {
				//Sell
				Player p = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
				PlayerInfo pinfo = plugin.getPlayerInfo(p);
				String configID = pinfo.getHorseConfigID();
				String name = plugin.getData().getHorseName(p.getUniqueId(), configID);
				name = name.replaceAll("%roleplayname%", pinfo.getName());
				
				int price = Utils.getPrice(plugin.getData().getHorseSpeed(p.getUniqueId(), configID));
				plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), (double) price);
				plugin.getData().delHorse(p.getUniqueId(), configID);
				
				p.closeInventory();
				p.sendMessage(ChatColor.GREEN + "Successfully sold " + name + " for " + price + ".");
			} else if (e.getRawSlot() == 6) {
				//Rename
				e.getWhoClicked().closeInventory();
				if (plugin.getPlayerInfo(e.getWhoClicked()).getRank().equals("member")) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You must be a donator to rename your horse!");
					return;
				}
				final int RENAME_COST = 10;
				int curbalance = (int) plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()));
				if (curbalance < RENAME_COST) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford to rename the horse.");
					return;
				}
				e.getWhoClicked().sendMessage(ChatColor.GRAY + "Please enter a new name for the horse in chat.");
				plugin.getPlayerInfo(e.getWhoClicked()).setRequestingHorseName(true);
				plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()), (double) RENAME_COST);
			}
		}
	}
	@EventHandler
	public void onDespawnGUIClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 9 && inv.getName().equals(Utils.colored("&fDespawn Horse"))) {
			e.setCancelled(true);
			if (e.getRawSlot() == 4) {
				String configID = plugin.getPlayerInfo(e.getWhoClicked()).getHorseConfigID();
				String name = plugin.getData().getHorseName(e.getWhoClicked().getUniqueId(), configID);
				name = name.replaceAll("%roleplayname%", plugin.getPlayerInfo(e.getWhoClicked()).getName());
				plugin.getPlayerInfo(e.getWhoClicked()).removeHorse(configID);
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully despawned " + name + ".");
			}
		}
	}
}
