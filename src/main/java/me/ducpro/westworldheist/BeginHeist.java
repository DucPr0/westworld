package me.ducpro.westworldheist;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldheist.info.HeistInfo;

public class BeginHeist {
	public static void beginHeist(Main plugin, Player p, String bankname, ItemFrame doorframe, List<Block> barriers, List<Block> placeexplosives) {
		plugin.heistinfo.put(bankname, new HeistInfo(plugin, bankname, p, doorframe, barriers, placeexplosives));
		plugin.getPlayerInfo(p).addRobbedBank(bankname);
		
		//Get all chests belonging to bank and fill them with gold.
		ConfigurationSection chestsection = plugin.getConfigFile().getChests(bankname);
		for (String chestname: chestsection.getKeys(false)) {
			if (chestname.equals("chestcount")) continue;
			if (chestname.equals("lastrobbed")) continue;
			Coordinates coord = plugin.getConfigFile().getChest(bankname, chestname);
			Block block = coord.getBlock();
			if (block.getType() != Material.CHEST) continue;
			Chest chest = (Chest) block.getState();
			Inventory inv = chest.getBlockInventory();
			inv.clear();
			int amount = chestsection.getConfigurationSection(chestname).getInt("amount");
			Random r = new Random();
			List<Integer> list = new ArrayList<Integer>();
			while (amount > 0 && list.size() < 27) {
				int tmp = r.nextInt(amount) + 1;
				list.add(tmp);
				amount -= tmp;
			}
			int curidx = 0;
			while (amount > 0) {
				int curval = list.get(curidx);
				if (amount >= 64 - curval) {
					amount -= (64 - curval);
					list.set(curidx, 64);
				} else {
					amount = 0;
					list.set(curidx, curval + amount);
				}
				curidx++;
			}
			int nuggets = r.nextInt(27 - list.size() + 1);
			while (nuggets > 0) {
				nuggets--;
				int nuggetamount = r.nextInt(10);
				list.add(-nuggetamount);
			}
			while (list.size() < 27) {
				list.add(0);
			}
			for (int i = 0; i < list.size(); i++) {
				int randomind = r.nextInt(list.size());
				int val = list.get(i);
				list.set(i, list.get(randomind));
				list.set(randomind, val);
			}
			for (int i = 0; i < inv.getSize(); i++) {
				if (list.get(i) >= 0) inv.setItem(i, new ItemStack(Material.GOLD_INGOT, list.get(i)));
				else inv.setItem(i, new ItemStack(Material.GOLD_NUGGET, -list.get(i)));
			}
		}
		//Announce to everyone in a 300 block radius
		for (Entity entity: p.getNearbyEntities(300, 100, 300)) {
			if (entity instanceof Player) {
				if (((Player) entity).getUniqueId() != p.getUniqueId()) {
					((Player) entity).sendTitle(ChatColor.RED + "" + ChatColor.BOLD + p.getDisplayName(),
							ChatColor.RED + "is attempting to rob the bank!", 10, 70, 20);
				}
			}
		}
		//Make player's name red
		plugin.getHeistTeam().addPlayer(p);
		
		//Add heist to list of crimes
		int curHeists = plugin.getData().getInt(p.getUniqueId(), plugin.getData().HEISTS, 0);
		plugin.getData().setInt(p.getUniqueId(), plugin.getData().HEISTS, curHeists + 1);
		
		//Play sound to everyone in a 40 - block radius
		for (Entity e: doorframe.getWorld().getNearbyEntities(doorframe.getLocation(), 40, 40, 40)) {
			if (e instanceof Player) {
				((Player) e).playSound(doorframe.getLocation(), "ww.e.heist", 5, 1);
			}
		}
	}
}
