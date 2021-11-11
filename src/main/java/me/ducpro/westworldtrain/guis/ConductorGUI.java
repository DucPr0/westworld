package me.ducpro.westworldtrain.guis;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ducpro.westworldtrain.utils.UtilStacks;
import me.ducpro.westworldtrain.utils.Utils;

public class ConductorGUI {
	public static void openConductorGUI(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fConductor"));
		ItemStack stonepick = UtilStacks.getUtilStacks().getStonePick();
		inv.setItem(0, stonepick);
		
		ItemStack broaditem = new ItemStack(Material.GOLD_NUGGET, 1);
		ItemMeta meta = broaditem.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Click to board the train!");
		meta.setLore(Arrays.asList(Utils.colored("&7Cost: 5.")));
		broaditem.setItemMeta(meta);
		inv.setItem(4, broaditem);
		
		p.openInventory(inv);
	}
}
