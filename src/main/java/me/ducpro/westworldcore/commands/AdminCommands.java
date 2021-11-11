package me.ducpro.westworldcore.commands;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldheist.BeginHeist;
import me.ducpro.westworldheist.commands.Bank;
import me.ducpro.westworldhorse.commands.SetSpeed;
import me.ducpro.westworldhorse.commands.Spawnpoints;
import me.ducpro.westworldhorse.utils.KillHorses;
import me.ducpro.westworldintro.BeginIntro;
import me.ducpro.westworldintro.commands.Reset;
import me.ducpro.westworldintro.commands.SetSpawn;
import me.ducpro.westworldtrain.TrainManager;
import me.ducpro.westworldtrain.commands.SetTrainSpawn;
import me.ducpro.westworldtrain.commands.TownCommands;
import net.md_5.bungee.api.ChatColor;

public class AdminCommands implements CommandExecutor {
	private Main plugin;
	public AdminCommands(Main plugin, TrainManager train) {
		this.plugin = plugin;
	}
	private String[] helpmsg = {
		"&e/westcore setintrospawn: &7Set intro's spawn.",
		"&e/westcore settownspawn: &7Set town's spawn.",
		"&e/westcore hatitem add: &7Make item in your hand equipable as a hat.",
		"&e/westcore hatitem remove: &7Remove the item in your hand from being a hat.",
		"&e/westcore identify {name}: &7Get the real name of a player.",
		"&e/westcore history {name}: &7Get history of player's names.",
		"&e/westcore forcereset {name}: &7Force reset a player's character creation process.",
		"&e/westcore addarmorstand: &7Add a sitable armor stand at current position.",
		"&e/westcore rotate: &7Disable all item frame rotations.",
		"&e/westcore list: &7List all online players along with their nicknames.",
		"&e/westcore removearmorstand: &7Toggle visiblity of all armorstands for removal.",
		"&e/westcore addspawnpoint: &7Add a spawnpoint for horses.",
		"&e/westcore listspawnpoints: &7List all spawnpoints for horses and their ID.",
		"&e/westcore removespawnpoint {ID}: &7Remove a horse spawnpoint by ID.",
		"&e/westcore addbank {bankname}: &7Add a bank.",
		"&e/westcore removebank {bankname}: &7Remove a bank.",
		"&e/westcore listbanks : &7List all banks.",
		"&e/westcore listchests {bankname}: &7List all chests.",
		"&e/westcore addchest {bankname} {amount}: &7Set chest you are looking at to a bank chest, with a total amount of gold.",
		"&e/westcore removechest {bankname} {chestID}: &7Remove chest from bank's list.",
		"&e/westcore settrainspawn: &7Set train's spawn.",
		"&e/westcore addtown {townname}: &7Add a town to the train's cycle.",
		"&e/westcore listtowns: &7List all towns in the order of the cycle.",
		"&e/westcore removetown {townname}: &7Remove a town from the train's cycle.",
		"&e/westcore setorder {townname} {order}: &7Set order for town in train's cycle.",
		"&e/westcore starttrain: &7Start the train.",
		"&e/westcore stoptrain: &7Stop the train.",
		"&e/westcore toggletrainstart: &7Toggle if the train automically starts when the server is turned on.",
		"&e/westcore addperm <member/donator> {perm}: &7Add a permission for the selected rank.",
		"&e/westcore removeperm <member/donator> {perm}: &7Remove a permission from the selected rank.",
		"&e/westcore setmedward: &7Make current location a possible respawn location after a player dies.",
		"&e/westcore setjailcell: &7Make current location a possible jailcell after a criminal dies.",
		"&e/westcore addposter {x} {y} {z}: &7Add a wanted poster.",
		"&e/westcore listposters: &7List all wanted posters.",
		"&e/westcore delposter {ID}: &7Remove poster.",
		"&e/westcore setydiff {section} {ydiff}: &7Developer command.",
		"&e/westcore setspeed {speedtype} {value}: &7Developer command.",
		"&e/westcore removehorses: &7Developer command.",
		"&e/promote {name}: &7Promote player.",
		"&e/demote {name}: &7Demote player."
	};
	private int MESSAGES_PER_PAGE = 6;
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
			return true;
		}
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			int curpage = 1;
			if (args.length == 2) {
				try {
					curpage = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Please enter a valid page.");
					return true;
				}
			}
			int leftbound = MESSAGES_PER_PAGE * (curpage - 1), rightbound = MESSAGES_PER_PAGE * curpage - 1;
			if (leftbound >= helpmsg.length) {
				sender.sendMessage(ChatColor.RED + "That page does not exist!");
				return true;
			}
			if (rightbound >= helpmsg.length) rightbound = helpmsg.length - 1;
			sender.sendMessage(ChatColor.YELLOW + "Westcore help - Page " + ChatColor.GRAY + curpage + ChatColor.YELLOW + ":");
			for (int i = leftbound; i <= rightbound; i++) {
				sender.sendMessage(colored(helpmsg[i]));
			}
			sender.sendMessage(ChatColor.YELLOW + "Next page: /westcore help " + ChatColor.GRAY + (curpage + 1) + ChatColor.YELLOW + ".");
		} else if (args[0].equalsIgnoreCase("setintrospawn")) {
			SetSpawn.setIntroSpawn(plugin, sender);
			
		} else if (args[0].equalsIgnoreCase("settownspawn")) {
			SetSpawn.setTownSpawn(plugin, sender);
			
		} else if (args[0].equalsIgnoreCase("identify")) {
			String name = "";
			for (int i = 1; i < args.length; i++) {
				name += args[i] + " ";
			}
			name = name.trim();
			
			for (Player p: Bukkit.getOnlinePlayers()) {
				if (plugin.getPlayerInfo(p).getStage() <= Main.INTRO_STAGES) continue;
				String playername = plugin.getPlayerInfo(p).getName();
				if (playername.equals(name)) {
					sender.sendMessage(ChatColor.GREEN + name + " is " + p.getName());
					return true;
				}
			}
			
			sender.sendMessage(ChatColor.RED + "There's no player with name " + name);
			
		} else if (args[0].equalsIgnoreCase("hatitem")) {
			if (args.length == 1 || (!(args[1].equalsIgnoreCase("add")) && !(args[1].equalsIgnoreCase("remove")))) {
				sender.sendMessage(ChatColor.RED + "Correct usage: /westcore hatitem {add/remove}.");
				return true;
			}
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			
			Player p = (Player) sender;
			ItemStack currentitem = p.getInventory().getItemInMainHand();
			String itemname = null;
			if (currentitem.hasItemMeta() && currentitem.getItemMeta().hasDisplayName()) {
				itemname = currentitem.getItemMeta().getDisplayName();
			}
			if (itemname == null) {
				sender.sendMessage(ChatColor.RED + "This item have no name.");
				return true;
			}
			
			if (args[1].equalsIgnoreCase("add")) {
				plugin.getConfigFile().addItem("allowed-hats", itemname);
				sender.sendMessage(ChatColor.GREEN + "Successfully added " + itemname + " to the list of hats.");
			} else {
				boolean removed = plugin.getConfigFile().removeItem("allowed-hats", itemname);
				if (removed) sender.sendMessage(ChatColor.GREEN + "Successfully removed " + itemname + " from the list of hats.");
				else sender.sendMessage(ChatColor.RED + itemname + " is not a hat.");
			}
			
		} else if (args[0].equalsIgnoreCase("forcereset")) {
			Reset.forceReset(plugin, sender, args);
			
		} else if (args[0].equalsIgnoreCase("history")) {
			Player target = null;
			if (args.length == 1) {
				if (!(sender instanceof Player)) target = null;
				else target = (Player) sender;
			} else {
				target = Bukkit.getPlayer(args[1]);
			}
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Please enter a valid player name.");
				return true;
			}
			
			sender.sendMessage(colored("&ePast names of " + args[1] + ":"));
			
			List<String> list = plugin.getData().getHistory(target.getUniqueId());
			int id = 0;
			for (String name: list) {
				id++;
				sender.sendMessage(colored("&e" + id + ". &7" + name));
			}
			
		} else if (args[0].equalsIgnoreCase("setydiff")) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Correct usage: /westcore setydiff {section} {ydiff}.");
				return true;
			}
			int section;
			try {
				section = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Please enter a valid section.");
				return true;
			}
			
			double ydiff;
			try {
				ydiff = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Please enter a valid number.");
				return true;
			}
			
			String pathname = "ydiff" + section;
			plugin.getConfigFile().setDouble(pathname, ydiff, true);
			sender.sendMessage(ChatColor.GREEN + "Successfully set ydiff for section " + section + " to " + ydiff + ".");
			
		} else if (args[0].equalsIgnoreCase("addarmorstand")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			Player p = (Player) sender;
			
			double ydiff = plugin.getConfigFile().getDouble("ydiff3");
			
			ArmorStand as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(new Vector(0, ydiff, 0)), EntityType.ARMOR_STAND);
			as.setVisible(false);
			as.setCustomName(ChatColor.RED + "TRAIN CHAIR");
			as.setGravity(false);
			
			sender.sendMessage(ChatColor.GREEN + "Successfully added train chair.");
			
		} else if (args[0].equalsIgnoreCase("rotate")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			Player p = (Player) sender;
			
			if (plugin.getPlayerInfo(p).getRotate()) {
				plugin.getPlayerInfo(p).setRotate(false);
				p.sendMessage(ChatColor.RED + "Toggled rotate mode OFF");
			} else {
				plugin.getPlayerInfo(p).setRotate(true);
				p.sendMessage(ChatColor.GREEN + "Toggled rotate mode ON");
			}
			
		} else if (args[0].equalsIgnoreCase("list")) {
			int id = 0;
			
			sender.sendMessage(ChatColor.YELLOW + "Online players:");
			for (Player p: Bukkit.getOnlinePlayers()) {
				id++;
				sender.sendMessage(colored("&e" + id + ". &f" + p.getName() + " &e(" + "&f" + plugin.getPlayerInfo(p).getName() + "&e)"));
			}
			
		} else if (args[0].equalsIgnoreCase("removearmorstand")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			Player p = (Player) sender;
			
			boolean finalremove = plugin.getPlayerInfo(p).toggleRemoveStand();
			if (finalremove) p.sendMessage(ChatColor.GREEN + "Toggled remove armorstand mode ON.");
			else p.sendMessage(ChatColor.RED + "Toggled remove armorstand mode OFF.");
			
			for (World world: Bukkit.getWorlds()) {
				for (Entity e: world.getEntities()) {
					if (e instanceof ArmorStand) {
						if (e.getCustomName() != null && e.getCustomName().equals(ChatColor.RED + "TRAIN CHAIR")) {
							if (finalremove) ((ArmorStand) e).setVisible(true);
							else ((ArmorStand) e).setVisible(false);
						}
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("setspeed")) {
			SetSpeed.setSpeed(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("addspawnpoint")) {
			Spawnpoints.addSpawnpoint(plugin, sender);
		} else if (args[0].equalsIgnoreCase("listspawnpoints")) {
			Spawnpoints.listSpawnpoint(plugin, sender);
		} else if (args[0].equalsIgnoreCase("removespawnpoint")) {
			Spawnpoints.removeSpawnpoint(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("addbank")) {
			Bank.addBank(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("removebank")) {
			Bank.removeBank(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("listbanks")) {
			Bank.listBanks(plugin, sender);
		} else if (args[0].equalsIgnoreCase("listchests")) {
			Bank.listChests(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("addchest")) {
			Bank.addChest(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("removechest")) {
			Bank.removeChest(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("settrainspawn")) {
			SetTrainSpawn.setTrainSpawn(plugin, sender);
		} else if (args[0].equalsIgnoreCase("addtown")) {
			TownCommands.addTown(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("listtowns")) {
			TownCommands.listTowns(plugin, sender);
		} else if (args[0].equalsIgnoreCase("removetown")) {
			TownCommands.removeTown(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("setorder")) {
			TownCommands.setOrder(plugin, sender, args);
		} else if (args[0].equalsIgnoreCase("starttrain")) {
			if (plugin.train != null) {
				sender.sendMessage(ChatColor.RED + "The train is already started!");
				return true;
			}
			plugin.train = new TrainManager(plugin);
			sender.sendMessage(ChatColor.GREEN + "Successfully started train.");
		} else if (args[0].equalsIgnoreCase("stoptrain")) {
			if (plugin.train == null) {
				sender.sendMessage(ChatColor.RED + "The train is not in operation.");
				return true;
			}
			plugin.train.stopTrain();
			plugin.train = null;
			sender.sendMessage(ChatColor.GREEN + "Successfully stopped train.");
		} else if (args[0].equalsIgnoreCase("toggletrainstart")) {
			boolean cur = plugin.getConfigFile().getTrainStart();
			if (!cur) {
				cur = true;
				plugin.getConfigFile().setTrainStart(true);
				sender.sendMessage(ChatColor.GREEN + "Successfully toggled auto train start ON");
			} else {
				cur = false;
				plugin.getConfigFile().setTrainStart(false);
				sender.sendMessage(ChatColor.GREEN + "Successfully toggled auto train start " + ChatColor.RED + "OFF");
			}
		} else if (args[0].equalsIgnoreCase("addperm")) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Correct usage: /wc addperm {rank} {perm}.");
				return true;
			}
			String rank = args[1], perm = args[2];
			if (!rank.equals("member") && !rank.equals("donator")) {
				sender.sendMessage(ChatColor.RED + "Invalid rank. Available ranks are member/donator.");
				return true;
			}
			plugin.getPermHandler().addPerm(rank, perm);
			for (Player p: Bukkit.getOnlinePlayers()) {
				if (rank.equals("member")) plugin.getPlayerInfo(p).addPerm(perm);
				if (rank.equals("donator") && !plugin.getPlayerInfo(p).getRank().equals("member")) plugin.getPlayerInfo(p).addPerm(perm);
			}
			sender.sendMessage(ChatColor.GREEN + "Successfully added permission " + perm + " to rank " + rank + ".");
		} else if (args[0].equalsIgnoreCase("removeperm")) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Correct usage: /wc removeperm {rank} {perm}.");
				return true;
			}
			String rank = args[1], perm = args[2];
			if (!rank.equals("member") && !rank.equals("donator")) {
				sender.sendMessage(ChatColor.RED + "Invalid rank. Available ranks are member/donator.");
				return true;
			}
			plugin.getPermHandler().removePerm(rank, perm);
			for (Player p: Bukkit.getOnlinePlayers()) {
				plugin.getPlayerInfo(p).removePerm(perm);
			}
			sender.sendMessage(ChatColor.GREEN + "Successfully removed permission " + perm + " from rank " + rank + ".");
		} else if (args[0].equalsIgnoreCase("setmedward") || args[0].equalsIgnoreCase("setjailcell")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			Player p = (Player) sender;
			String path = "normal-respawn";
			if (args[0].equalsIgnoreCase("setjailcell")) path = "jail-respawn";
			plugin.getConfigFile().addLocation(path, p.getLocation());
			if (args[0].equalsIgnoreCase("setmedward")) {
				p.sendMessage(ChatColor.GREEN + "Successfully added possible respawn location.");
			} else {
				p.sendMessage(ChatColor.GREEN + "Successfully added possible jail location.");
			}
		} else if (args[0].equalsIgnoreCase("removehorses")) {
			KillHorses.killHorses(plugin);
		} else if (args[0].equalsIgnoreCase("addposter")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			Player p = (Player) sender;
			final String path = "wanted-posters";
			if (args.length < 4) {
				p.sendMessage(ChatColor.RED + "Correct usage: /wc addposter {x} {y} {z}.");
				return true;
			}
			World world = p.getWorld();
			int x, y, z;
			try {
				x = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.RED + "Please enter a valid number.");
				return true;
			}
			
			try {
				y = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.RED + "Please enter a valid number.");
				return true;
			}
			
			try {
				z = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.RED + "Please enter a valid number.");
				return true;
			}
			
			plugin.getConfigFile().addLocation(path, new Location(world, x, y, z));
			p.sendMessage(ChatColor.GREEN + "Successfully added poster at position " + x + " " + y + " " + z);
		} else if (args[0].equalsIgnoreCase("listposters")) {
			final String path = "wanted-posters";
			ConfigurationSection sec = plugin.getConfigFile().getSection(path);
			Set<String> set = sec.getKeys(false);
			for (String key: set) {
				if (key.equals("count")) continue;
				Location loc = plugin.getConfigFile().getLocation(path + "." + key);
				sender.sendMessage(colored("&e" + key + ": &7" + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "&e."));
			}
		} else if (args[0].equalsIgnoreCase("delposter")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Please enter an ID.");
				return true;
			}
			String ID = args[1];
			final String path = "wanted-posters";
			boolean removed = plugin.getConfigFile().removeLocation(path + "." + ID);
			if (!removed) sender.sendMessage(ChatColor.RED + "There's no poster with such ID.");
			else sender.sendMessage(ChatColor.GREEN + "Successfully removed the poster.");
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid subcommand.");
		}
		return true;
	}
}
