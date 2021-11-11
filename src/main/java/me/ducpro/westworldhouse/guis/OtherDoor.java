package me.ducpro.westworldhouse.guis;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldhouse.utils.UtilStacks;
import me.ducpro.westworldhouse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class OtherDoor {
	public static void otherDoorLeftClick(Main plugin, Player p, DoorInfo door) {
		plugin.getPlayerInfo(p).setDoor(door.getCoords());
		
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fLockpicking"));
		
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack owner = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skullmeta = (SkullMeta) owner.getItemMeta();
//		skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(door.getOwner()));
		String msg = Utils.colored("&eOwner: &f%name%");
		msg = msg.replaceAll("%name%", plugin.getData().getName(door.getOwner()));
		skullmeta.setDisplayName(msg);
		owner.setItemMeta(skullmeta);
		inv.setItem(3, owner);
		
		ItemStack picklock = new ItemStack(Material.TRIPWIRE_HOOK, 1);
		ItemMeta meta = picklock.getItemMeta();
		meta.setDisplayName(Utils.colored("&eLockpicking"));
		picklock.setItemMeta(meta);
		inv.setItem(5, picklock);
		
		final Main finalplugin = plugin;
		final Inventory finalinv = inv;
		final UUID finalowner = door.getOwner();
		final Player finalp = p;
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				ItemStack skull = finalinv.getItem(3);
				SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
				skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(finalowner));
				skull.setItemMeta(skullmeta);
				final ItemStack finalskull = skull;
				Bukkit.getScheduler().runTask(finalplugin, new Runnable() {
					@Override
					public void run() {
						Inventory inv = finalinv;
						inv.setItem(3, finalskull);
						Inventory topinv = finalp.getOpenInventory().getTopInventory();
						if (topinv != null && topinv.getSize() == 9 && topinv.getName().equals(ChatColor.WHITE + "Lockpicking")) {
							finalp.openInventory(inv);
						}
					}
				});
			}
		}, 1L);
		
		p.openInventory(inv);
	}
	public static void otherDoorNoLockpick(Main plugin, Player p, DoorInfo door) {
		plugin.getPlayerInfo(p).setDoor(door.getCoords());
		
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fInfo"));
		
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack owner = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skullmeta = (SkullMeta) owner.getItemMeta();
//		skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(door.getOwner()));
		String msg = Utils.colored("&eOwner: &f%name%");
		msg = msg.replaceAll("%name%", plugin.getData().getName(door.getOwner()));
		skullmeta.setDisplayName(msg);
		owner.setItemMeta(skullmeta);
		inv.setItem(4, owner);
		
		final Main finalplugin = plugin;
		final Inventory finalinv = inv;
		final UUID finalowner = door.getOwner();
		final Player finalp = p;
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				ItemStack skull = finalinv.getItem(4);
				SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
				skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(finalowner));
				skull.setItemMeta(skullmeta);
				final ItemStack finalskull = skull;
				Bukkit.getScheduler().runTask(finalplugin, new Runnable() {
					@Override
					public void run() {
						Inventory inv = finalinv;
						inv.setItem(4, finalskull);
						Inventory topinv = finalp.getOpenInventory().getTopInventory();
						if (topinv != null && topinv.getSize() == 9 && topinv.getName().equals(ChatColor.WHITE + "Info")) {
							finalp.openInventory(inv);
						}
					}
				});
			}
		}, 1L);
		
		p.openInventory(inv);
	}
}
