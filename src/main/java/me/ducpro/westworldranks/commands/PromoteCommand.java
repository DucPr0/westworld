package me.ducpro.westworldranks.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import me.ducpro.nametagutils.SetPrefix;
import me.ducpro.westworldcore.main.Main;

public class PromoteCommand implements CommandExecutor {
	private Main plugin;
	public PromoteCommand(Main plugin) {
		this.plugin = plugin;
	}
	private void updatePrefix(Player p) {
		SetPrefix.setPrefix(plugin, plugin.getPlayerInfo(p).getRank(), p, plugin.getPlayerInfo(p).getPrefix());
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (sender instanceof Player) {
			if (!sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /promote {name}");
			return true;
		}
		String target = args[0];
		Player p = Bukkit.getPlayerExact(target);
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "This player isn't online.");
			return true;
		}
		String rank = plugin.getPlayerInfo(p).getRank();
		if (rank.equals("member")) {
			plugin.getPlayerInfo(p).setRank("donator");
			plugin.getData().setRank(p.getUniqueId(), "donator");
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, "donator", plugin.getPlayerInfo(p).getPrefix());
			updatePrefix(p);
			
			List<String> donatorPerms = plugin.getPermHandler().getPerms("donator");
			for (String perm: donatorPerms) plugin.getPlayerInfo(p).addPerm(perm);
		} else if (rank.equals("donator")) {
			plugin.getPlayerInfo(p).setRank("mod");
			plugin.getData().setRank(p.getUniqueId(), "mod");
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(name, "donator");
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, "mod", plugin.getPlayerInfo(p).getPrefix());			
			updatePrefix(p);
		} else if (rank.equals("mod")) {
			plugin.getPlayerInfo(p).setRank("admin");
			plugin.getData().setRank(p.getUniqueId(), "admin");
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(name, "mod");
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, "admin", plugin.getPlayerInfo(p).getPrefix());
			updatePrefix(p);
			
		} else if (rank.equals("admin")) {
			sender.sendMessage(ChatColor.RED + plugin.getPlayerInfo(p).getName() + " is already an admin.");
			return true;
		}
		sender.sendMessage(ChatColor.GREEN + "Successfully promoted " + plugin.getPlayerInfo(p).getName() + " to " + 
											plugin.getPlayerInfo(p).getRank());
		return true;
	}
}
