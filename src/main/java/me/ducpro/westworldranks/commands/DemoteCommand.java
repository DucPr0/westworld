package me.ducpro.westworldranks.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import me.ducpro.nametagutils.SetPrefix;
import me.ducpro.westworldcore.main.Main;

public class DemoteCommand implements CommandExecutor {
	private Main plugin;
	public DemoteCommand(Main plugin) {
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
			sender.sendMessage(ChatColor.RED + plugin.getPlayerInfo(p).getName() + " is already a member.");
			return true;
		} else if (rank.equals("donator")) {
			plugin.getPlayerInfo(p).setRank("member");
			plugin.getData().setRank(p.getUniqueId(), "member");
			
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(name, "donator");
			updatePrefix(p);
			
			List<String> donatorPerms = plugin.getPermHandler().getPerms("donator");
			for (String perm: donatorPerms) plugin.getPlayerInfo(p).removePerm(perm);
		} else if (rank.equals("mod")) {
			plugin.getPlayerInfo(p).setRank("donator");
			plugin.getData().setRank(p.getUniqueId(), "donator");
			
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(name, "mod");
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, "donator", plugin.getPlayerInfo(p).getPrefix());
			updatePrefix(p);
		} else if (rank.equals("admin")) {
			plugin.getPlayerInfo(p).setRank("mod");
			plugin.getData().setRank(p.getUniqueId(), "mod");
			
			String name = plugin.getPlayerInfo(p).getName();
//			NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(name, "admin");
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, "mod", plugin.getPlayerInfo(p).getPrefix());
			updatePrefix(p);
		}
		sender.sendMessage(ChatColor.GREEN + "Successfully demoted " + plugin.getPlayerInfo(p).getName() + " to " + 
												plugin.getPlayerInfo(p).getRank());
		return true;
	}
}
