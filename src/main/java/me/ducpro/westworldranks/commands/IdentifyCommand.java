package me.ducpro.westworldranks.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class IdentifyCommand implements CommandExecutor {
	private Main plugin;
	public IdentifyCommand(Main plugin) {
		this.plugin = plugin;
	}
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (sender instanceof Player) {
			String rank = plugin.getPlayerInfo((Player) sender).getRank();
			if (!rank.equals("admin")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /identify {roleplayname}.");
			return true;
		}
		String roleplayname = "";
		for (int i = 0; i < args.length; i++) {
			roleplayname += args[i];
			if (i < args.length - 1) roleplayname += " ";
		}
		Player answerplayer = null;
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (plugin.getPlayerInfo(p).getStage() <= Main.INTRO_STAGES) continue;
			String name = plugin.getPlayerInfo(p).getName();
			if (name.equals(roleplayname)) answerplayer = p;
		}
		if (answerplayer == null) {
			sender.sendMessage(ChatColor.RED + "No online player exists with the name " + roleplayname);
			return true;
		}
		sender.sendMessage(colored("&7" + roleplayname + " &eis &7" + answerplayer.getName() + "&e."));
		List<String> bans = plugin.getData().getBans(answerplayer.getUniqueId());
		List<String> kicks = plugin.getData().getKicks(answerplayer.getUniqueId());
		List<String> mutes = plugin.getData().getMutes(answerplayer.getUniqueId());
		sender.sendMessage(colored("&eAll bans:"));
		int curid = 0;
		for (String banreason: bans) {
			curid++;
			sender.sendMessage(colored("&e" + curid + ". &7" + banreason));
		}
		sender.sendMessage(colored("&eAll kicks:"));
		curid = 0;
		for (String kickreason: kicks) {
			curid++;
			sender.sendMessage(colored("&e" + curid + ". &7" + kickreason));
		}
		sender.sendMessage(colored("&eAll mutes:"));
		curid = 0;
		for (String mutereason: mutes) {
			curid++;
			sender.sendMessage(colored("&e" + curid + ". &7" + mutereason));
		}
		return true;
	}
}
