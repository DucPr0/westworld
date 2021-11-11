package me.ducpro.westworldhorse.guis;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhorse.info.HorseInfo;
import me.ducpro.westworldhorse.utils.UtilStacks;
import me.ducpro.westworldhorse.utils.Utils;

public class EditGUI {
	public static void spawnGUI(Main plugin, Player p) {
		String configID = plugin.getPlayerInfo(p).getHorseConfigID();
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.WHITE + "Edit Horse");
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack spawnItem = new ItemStack(Material.SADDLE, 1);
		ItemMeta spawnMeta = spawnItem.getItemMeta();
		spawnMeta.setDisplayName(ChatColor.YELLOW + "Spawn");
		spawnItem.setItemMeta(spawnMeta);
		inv.setItem(2, spawnItem);
		
		ItemStack sellItem = new ItemStack(Material.PAPER, 1);
		ItemMeta sellMeta = sellItem.getItemMeta();
		sellMeta.setDisplayName(Utils.colored("&eSell"));
		String lore = Utils.colored("&ePrice: &7%price%");
		lore = lore.replaceAll("%price%", Utils.getPrice(plugin.getData().getHorseSpeed(p.getUniqueId(), configID)) + "");
		sellMeta.setLore(Arrays.asList(lore));
		sellItem.setItemMeta(sellMeta);
		inv.setItem(4, sellItem);
		
		ItemStack renameItem = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta renameMeta = renameItem.getItemMeta();
		renameMeta.setDisplayName(Utils.colored("&eRename"));
		renameMeta.setLore(Arrays.asList(Utils.colored("&ePrice: &710")));
		renameItem.setItemMeta(renameMeta);
		inv.setItem(6, renameItem);
		
		p.openInventory(inv);
	}
	public static void despawnGUI(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.colored("&fDespawn Horse"));
		inv.setItem(0, UtilStacks.getUtilStacks().getStonePick());
		
		ItemStack despawnItem = new ItemStack(Material.SADDLE, 1);
		ItemMeta despawnMeta = despawnItem.getItemMeta();
		despawnMeta.setDisplayName(Utils.colored("&eDespawn"));
		despawnItem.setItemMeta(despawnMeta);
		inv.setItem(4, despawnItem);
		
		p.openInventory(inv);
	}
}
