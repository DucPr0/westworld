package me.ducpro.westworldhouse.listeners;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import me.ducpro.westworldcore.main.*;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldcore.utils.GetItemDamage;
import me.ducpro.westworldhouse.guis.*;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldhouse.utils.Utils;
import net.milkbowl.vault.economy.Economy;

public class GUIEvents implements Listener {
	private Main plugin;
	public GUIEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onDoorRent(InventoryClickEvent e) {
		if (e.getInventory().getName().equals(ChatColor.WHITE + "Rent") && e.getInventory().getSize() == 9) {
			e.setCancelled(true);
			if (e.getSlot() == 4) {
				HumanEntity p = e.getWhoClicked();
				if (!plugin.getPlayerInfo(p).canRent()) {
					p.sendMessage(ChatColor.RED + "You have reached the rent limit.");
					return;
				}
				
				Coordinates curdoor = plugin.getPlayerInfo(p).getDoor();
				
				//Vault decrease money part. If price > cur money return
				int price = Utils.getPrice(curdoor.getBlock().getType());
				double curbalance = plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()));
				if (price > curbalance) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford this.");
					return;
				}
				plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()), (double) price);
				
				plugin.doorinfo.put(curdoor, new DoorInfo(curdoor.getBlock(), Utils.DAY_SECONDS, p.getUniqueId(), plugin));
				
				p.sendMessage(ChatColor.GREEN + "Successfully rented the house.");
				p.closeInventory();
			}
		}
	}
	@EventHandler
	public void onOwnDoorClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 9 && inv.getName().equals(ChatColor.WHITE + "Configuring")) {
			e.setCancelled(true);
			if (e.getRawSlot() == 1) {
				Coordinates coord = plugin.getPlayerInfo(e.getWhoClicked()).getDoor();
				DoorInfo door = plugin.doorinfo.get(coord);
				
				//Vault increase money part
				int sellprice = Utils.getSellPrice(door.getType(), door.getSecondsLeft());
				plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()), (double) sellprice);
				
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully sold the house.");
				plugin.getPlayerInfo(e.getWhoClicked()).decreaseDoors();
				
				door.rentEnd(false);
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getRawSlot() == 3) {
				FriendsList.openAddPlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), 1);
			} else if (e.getRawSlot() == 5) {
				FriendsList.openRemovePlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), 1);
			} else if (e.getRawSlot() == 7) {
				Coordinates coords = plugin.getPlayerInfo(e.getWhoClicked()).getDoor();
				DoorInfo door = plugin.doorinfo.get(coords);
				int price = Utils.getPrice(door.getType());
				int curbalance = (int) plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()));
				if (price > curbalance) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford this!");
					return;
				}
				plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()), price);
				door.addSecondsLeft(Utils.DAY_SECONDS);
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully rented the house for another day.");
			}
		}
	}
	@EventHandler
	public void onClickHead(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 36 && inv.getName().equals(ChatColor.WHITE + "Add friends")) {
			e.setCancelled(true);
			if (e.getRawSlot() == -999) return;
			if (e.getRawSlot() < 27) {
				if (inv.getItem(e.getRawSlot()) == null) return;
				Coordinates curcoord = plugin.getPlayerInfo(e.getWhoClicked()).getDoor();
				DoorInfo door = plugin.doorinfo.get(curcoord);
				//Clicking a head
				ItemStack skull = e.getInventory().getItem(e.getRawSlot());
				SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
				UUID uuid = skullmeta.getOwningPlayer().getUniqueId();
				
				door.addPlayer(uuid);
				
				String displayname = plugin.getData().getName(uuid);
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully added " + displayname + " to the list of friends.");
				
			} else if (e.getRawSlot() == 27 && inv.getItem(27) != null) {
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getFriendsPage();
				curpage--;
				FriendsList.openAddPlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage);
				
			} else if (e.getRawSlot() == 35 && inv.getItem(35) != null) {
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getFriendsPage();
				curpage++;
				FriendsList.openAddPlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage);
				
			}
		}
	}
	@EventHandler
	public void onClickHeadRemove(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 36 && inv.getName().equals(ChatColor.WHITE + "Remove friends")) {
			e.setCancelled(true);
			if (e.getRawSlot() == -999) return;
			if (e.getRawSlot() < 27) {
				if (inv.getItem(e.getRawSlot()) == null) return;
				Coordinates curcoord = plugin.getPlayerInfo(e.getWhoClicked()).getDoor();
				DoorInfo door = plugin.doorinfo.get(curcoord);
				
				ItemStack skull = e.getInventory().getItem(e.getRawSlot());
				SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
				UUID uuid = skullmeta.getOwningPlayer().getUniqueId();
				
				door.removePlayer(uuid);
				
				String displayname = plugin.getData().getName(uuid);
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully removed " + displayname + " from the list of friends.");
				
			} else if (e.getRawSlot() == 27 && inv.getItem(27) != null) {
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getFriendsPage();
				curpage--;
				FriendsList.openRemovePlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage);
				
			} else if (e.getRawSlot() == 35 && inv.getItem(35) != null) {
				int curpage = plugin.getPlayerInfo(e.getWhoClicked()).getFriendsPage();
				curpage++;
				FriendsList.openRemovePlayerList(plugin, Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), curpage);
				
			}
		}
	}
	private final long HOUR_MILI = 3600000;
	@EventHandler
	public void onLockpick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getSize() == 9 && inv.getName().equals(ChatColor.WHITE + "Lockpicking")) {
			e.setCancelled(true);
			if (e.getRawSlot() == 5) {
				long curTime = System.currentTimeMillis();
				long lastLockPick = plugin.getData().getLong(e.getWhoClicked().getUniqueId(), plugin.getData().LAST_LOCKPICK, 0);
				if (curTime - lastLockPick < HOUR_MILI) {
					if (!e.getWhoClicked().isOp()) {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot lockpick again so early!");
						return;
					}
				}
				plugin.getData().setLong(e.getWhoClicked().getUniqueId(), plugin.getData().LAST_LOCKPICK, curTime);
				Coordinates coord = plugin.getPlayerInfo(e.getWhoClicked()).getDoor();
				DoorInfo door = plugin.doorinfo.get(coord);
				if (!e.getWhoClicked().isOp() && Bukkit.getPlayer(door.getOwner()) == null) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "This person can not be lockpicked!");
					return;
				}
				ItemStack istack = e.getWhoClicked().getInventory().getItemInMainHand();
				int amount = istack.getAmount();
				if (amount > 1) e.getWhoClicked().getInventory().setItemInMainHand(new ItemStack(istack.getType(), amount - 1));
				else e.getWhoClicked().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
				boolean success = Utils.lockPicked(door.getType());
				if (success) {
					Block lowerDoor = coord.getBlock();
					BlockState lowerDoorState = lowerDoor.getState();
					Openable openable = (Openable) lowerDoorState.getData();
					boolean openState = openable.isOpen();
					if (!openState) openState = true;
					else openState = false;
					openable.setOpen(openState);
					lowerDoorState.setData((MaterialData) openable);
					lowerDoorState.update();
					
					for (Entity entity: e.getWhoClicked().getNearbyEntities(10, 10, 10)) {
						if (entity instanceof Player) {
							((Player) entity).playSound(entity.getLocation(), "ww.e.lockpick", 5, 1);
						}
					}
					Bukkit.getPlayer(e.getWhoClicked().getUniqueId()).playSound(e.getWhoClicked().getLocation(), "ww.e.lockpick", 5, 1);
					
					e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully lockpicked the house.");
					
					door.setLastLockpicked(System.currentTimeMillis());
				} else {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Failed to lockpick the house.");
				}
				//Increment lockpick count
				int curLockpicks = plugin.getData().getInt(e.getWhoClicked().getUniqueId(), plugin.getData().LOCKPICKS, 0);
				plugin.getData().setInt(e.getWhoClicked().getUniqueId(), plugin.getData().LOCKPICKS, curLockpicks + 1);
				//Set lockpick countdown time to 3 minutes
				plugin.getPlayerInfo(e.getWhoClicked().getUniqueId()).setLockpickTime(180);
				//Add player to lockpick team
				plugin.getLockpickTeam().addPlayer(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()));
				
				e.getWhoClicked().closeInventory();
			}
		}
	}
	@EventHandler
	public void onInfoClick(InventoryClickEvent e) {
		if (e.getInventory().getSize() == 9 && e.getInventory().getName().equals(ChatColor.WHITE + "Info")) {
			e.setCancelled(true);
		}
	}
}
