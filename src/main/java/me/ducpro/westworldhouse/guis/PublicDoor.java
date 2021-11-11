package me.ducpro.westworldhouse.guis;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;

import me.ducpro.westworldcore.main.*;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldhouse.utils.UtilStacks;
import me.ducpro.westworldhouse.utils.Utils;

public class PublicDoor {
	public static void publicDoorLeftClick(Main plugin, Player p, Block b) {
		plugin.getPlayerInfo(p).setDoor(Utils.getLowerDoor(b));
		
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fRent"));
		
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = paper.getItemMeta();
		meta.setDisplayName(Utils.colored("&eRent"));
		String lore = Utils.colored("&ePrice: &c%price%");
		lore = lore.replaceAll("%price%", Utils.getPrice(b.getType()) + "");
		meta.setLore(Arrays.asList(lore));
		paper.setItemMeta(meta);
		inv.setItem(4, paper);
		
		p.openInventory(inv);
	}
}
