package me.ducpro.westworldintro.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldintro.BeginIntro;

public class Reset {
	public static void reset(Main plugin, Player p) {
		if (plugin.getHeistTeam().isInTeam(p)) {
			p.sendMessage(ChatColor.RED + "You cannot reset while being wanted!");
			return;
		}
		if (plugin.getPlayerInfo(p).isInRespawn()) {
			p.sendMessage(ChatColor.RED + "You cannot reset while being dead.");
			return;
		}
		BeginIntro beginintro = new BeginIntro(plugin);
		beginintro.beginIntro(p, false);
		return;
	}
	public static void forceReset(Main plugin, CommandSender sender, String args[]) {
		Player target = null;
		if (args.length == 1) {
			if (!(sender instanceof Player)) target = null;
			else target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args[1]);
		}
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Please enter a valid player name.");
			return;
		}
		if (plugin.isWanted(target)) {
			target.sendMessage(ChatColor.RED + "You cannot reset while being wanted!");
			return;
		}
		if (plugin.getPlayerInfo(target).isInRespawn()) {
			target.sendMessage(ChatColor.RED + "You cannot reset while being dead.");
			return;
		}
		BeginIntro beginintro = new BeginIntro(plugin);
		beginintro.beginIntro(target, true);
	}
}
