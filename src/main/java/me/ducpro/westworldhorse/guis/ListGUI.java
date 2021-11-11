package me.ducpro.westworldhorse.guis;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhorse.utils.UtilStacks;
import me.ducpro.westworldhorse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class ListGUI {
	private final static int HORSES_PER_PAGE = 27;
	public static void openListGUI(Main plugin, Player p, int page) {
		plugin.getPlayerInfo(p).setHorsePage(page);
		
		ConfigurationSection sec = plugin.getData().getHorseList(p.getUniqueId());
		Inventory inv = Bukkit.createInventory(null, 36, ChatColor.WHITE + "Your Horses:");
		Set<String> keys = sec.getKeys(false);
		int leftbound = HORSES_PER_PAGE * (page - 1), rightbound = HORSES_PER_PAGE * page - 1;
		int curid = 0;
		for (String key: keys) {
			if (key.equals("horse-count")) continue;
			if (curid < leftbound) {
				curid++;
				continue;
			}
			if (curid > rightbound) break;
			curid++;
			ItemStack istack = new ItemStack(Material.SADDLE, 1);
			ItemMeta meta = istack.getItemMeta();
			String horsename, horsespeed;
			horsename = sec.getConfigurationSection(key).getString("name");
			horsename = horsename.replaceAll("%roleplayname%", plugin.getPlayerInfo(p).getName());
			horsespeed = sec.getConfigurationSection(key).getString("speed");
			horsespeed = Character.toUpperCase(horsespeed.charAt(0)) + horsespeed.substring(1);
			meta.setDisplayName(ChatColor.YELLOW + horsename);
			String spawnstatus = ChatColor.RED + "Not Spawned";
			if (plugin.getPlayerInfo(p).isSpawned(key) != null) spawnstatus = ChatColor.GREEN + "Spawned";
			meta.setLore(Arrays.asList(Utils.colored("&eSpeed: &7" + horsespeed), spawnstatus));
			istack.setItemMeta(meta);
			inv.addItem(istack);
		}
		if (page > 1) {
			inv.setItem(27, UtilStacks.getUtilStacks().getLeftArrow());
		}
		if (HORSES_PER_PAGE * page < keys.size()) {
			inv.setItem(35, UtilStacks.getUtilStacks().getRightArrow());
		}
		p.openInventory(inv);
	}
}
