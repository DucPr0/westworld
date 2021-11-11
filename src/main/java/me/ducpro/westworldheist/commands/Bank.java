package me.ducpro.westworldheist.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldheist.info.ChestInfo;

public class Bank {
	public static void addBank(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore addbank {bankname}.");
			return;
		}
		String bankname = args[1];
		plugin.getConfigFile().addBank(bankname);
		sender.sendMessage(ChatColor.GREEN + "Successfully added bank " + bankname);
	}
	public static void listBanks(Main plugin, CommandSender sender) {
		String msg = ChatColor.YELLOW + "All available banks: ";
		ConfigurationSection banksection = plugin.getConfigFile().getBanks();
		Set<String> banks = banksection.getKeys(false);
		int curid = 0;
		for (String s: banks) {
			if (curid == 0) msg += (ChatColor.GRAY + s);
			else msg += ", " + s;
			curid = 1;
		}
		msg += ".";
		sender.sendMessage(msg);
	}
	private static String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static void listChests(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore listchests {bankname}.");
			return;
		}
		String bankname = args[1];
		if (!plugin.getConfigFile().bankExist(bankname)) {
			sender.sendMessage(ChatColor.RED + "Bank does not exist.");
			return;
		}
		ConfigurationSection banksection = plugin.getConfigFile().getChests(bankname);
		Set<String> chests = banksection.getKeys(false);
		sender.sendMessage(ChatColor.YELLOW + "All available chests for bank " + ChatColor.GRAY + bankname + ChatColor.YELLOW + ":");
		for (String chest: chests) {
			if (chest.equals("chestcount")) continue;
			if (chest.equals("lastrobbed")) continue;
			int x = banksection.getInt(chest + ".x");
			int y = banksection.getInt(chest + ".y");
			int z = banksection.getInt(chest + ".z");
			int amount = banksection.getInt(chest + ".amount");
			sender.sendMessage(colored("&e" + chest + ": &7" + x + " " + y + " " + z + "&e. Amount: &7" + amount + "&e."));
		}
	}
	public static void removeChest(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore addchest {bankname} {amount}.");
			return;
		}
		String bankname = args[1], chestID = args[2];
		if (!plugin.getConfigFile().bankExist(bankname)) {
			sender.sendMessage(ChatColor.RED + "Bank does not exist.");
			return;
		}
		
		Coordinates chestcoord = plugin.getConfigFile().getChest(bankname, chestID);
		plugin.chestinfo.remove(chestcoord);
		
		boolean removed = plugin.getConfigFile().removeChest(bankname, chestID);
		if (!removed) sender.sendMessage(ChatColor.RED + "Chest does not exist.");
		else sender.sendMessage(ChatColor.GREEN + "Successfully removed " + chestID + " from " + bankname + "'s chests.");
	}
	public static void addChest(Main plugin, CommandSender sender, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore removechest {bankname} {chestid}.");
			return;
		}
		String bankname = args[1];
		if (!plugin.getConfigFile().bankExist(bankname)) {
			sender.sendMessage(ChatColor.RED + "Bank does not exist.");
			return;
		}
		int amount;
		try {
			amount = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Please enter a valid amount.");
			return;
		}
		if (amount > 64 * 27) {
			sender.sendMessage(ChatColor.RED + "Amount is too big. Please enter an amount smaller than 1728.");
			return;
		}
		Player p = (Player) sender;
		HashSet<Material> notlook = new HashSet<Material>();
		notlook.add(Material.AIR);
		notlook.add(Material.ACACIA_TRAPDOOR);
		notlook.add(Material.BIRCH_TRAPDOOR);
		notlook.add(Material.OAK_TRAPDOOR);
		notlook.add(Material.DARK_OAK_TRAPDOOR);
		notlook.add(Material.JUNGLE_TRAPDOOR);
		notlook.add(Material.SPRUCE_TRAPDOOR);
		Block b = p.getTargetBlock(notlook, 4);
		if (b.getType() != Material.CHEST) {
			sender.sendMessage(ChatColor.RED + "Bank vaults can only be chests.");
			sender.sendMessage(b.getType().toString());
			return;
		}
		plugin.getConfigFile().addChest(bankname, b.getX(), b.getY(), b.getZ(), b.getWorld().getName(), amount);
		plugin.chestinfo.put(new Coordinates(b), new ChestInfo(bankname, amount, new Coordinates(b)));
		sender.sendMessage(ChatColor.GREEN + "Successully added a vault for bank " + bankname + ".");
	}
	public static void removeBank(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore removebank {bankname}.");
			return;
		}
		if (!plugin.getConfigFile().bankExist(args[1])) {
			sender.sendMessage(ChatColor.RED + "Bank does not exist.");
			return;
		}
		String bankname = args[1];
		Set<String> chests = plugin.getConfigFile().getChests(bankname).getKeys(false);
		for (String s: chests) {
			if (s.equals("chestcount")) continue;
			if (s.equals("lastrobbed")) continue;
			Coordinates coord = plugin.getConfigFile().getChest(bankname, s);
			plugin.chestinfo.remove(coord);
		}
		plugin.getConfigFile().removeBank(args[1]);
		sender.sendMessage(ChatColor.GREEN + "Successfully removed bank " + bankname + ".");
	}
}
