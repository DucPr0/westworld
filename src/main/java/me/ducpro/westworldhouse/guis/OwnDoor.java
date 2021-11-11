package me.ducpro.westworldhouse.guis;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mysql.fabric.xmlrpc.base.Array;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldhouse.utils.UtilStacks;
import me.ducpro.westworldhouse.utils.Utils;

public class OwnDoor {
	public static void ownDoorLeftClick(Main plugin, Player p, DoorInfo door) {
		plugin.getPlayerInfo(p).setDoor(door.getCoords());
		
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fConfiguring"));
		
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = paper.getItemMeta();
		meta.setDisplayName(Utils.colored("&eSell"));
		String lore = Utils.colored("&ePrice: &c%price%");
		lore = lore.replaceAll("%price%", Utils.getSellPrice(door.getType(), door.getSecondsLeft()) + "");
		meta.setLore(Arrays.asList(lore));
		paper.setItemMeta(meta);
		inv.setItem(1, paper);
		
		ItemStack emerald = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta2 = emerald.getItemMeta();
		meta2.setDisplayName(Utils.colored("&eAdd friends"));
		emerald.setItemMeta(meta2);
		inv.setItem(3, emerald);
		
		ItemStack redstone = new ItemStack(Material.REDSTONE, 1);
		ItemMeta meta3 = redstone.getItemMeta();
		meta3.setDisplayName(Utils.colored("&cRemove friends"));
		redstone.setItemMeta(meta3);
		inv.setItem(5, redstone);
		
		//Make item 7's name a countdown.
		ItemStack doorstack = new ItemStack(door.getType(), 1);
		ItemMeta meta4 = doorstack.getItemMeta();
		meta4.setDisplayName(Utils.colored("&fYour rent will last: " + door.convertTimeLeft(door.getSecondsLeft())));
		meta4.setLore(Arrays.asList(Utils.colored("&7Rent cost: &a" + Utils.getPrice(door.getType())),
									Utils.colored("&aClick to pay additional rent!")));
		doorstack.setItemMeta(meta4);
		inv.setItem(7, doorstack);
		
		p.openInventory(inv);
	}
}
