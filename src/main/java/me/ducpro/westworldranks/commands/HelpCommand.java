package me.ducpro.westworldranks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class HelpCommand implements CommandExecutor {
	private Main plugin;
	public HelpCommand(Main plugin) {
		this.plugin = plugin;
	}
	private String modhelp[] = {
		"&e/ban {name} {timeInHours} {reason}: &7Ban player {name} for reason {reason} (Maximally 1 day).",
		"&e/kick {name} {reason}: &7Kick player {name} for reason {reason}.",
		"&e/mute {name} {timeInMinutes} {reason}: &7Mute player {name} for reason {reason} (Maximally 1 hour).",
		"&e/staff: &7Switch to staff chat channel.",
		"&eNote: &7Roleplay names can be used in above commands.",
	};
	private String adminhelp[] = {
		"&e/ban {name} {timeInHours} {reason}: &7Ban player {name} for reason {reason}.",
		"&e/unban {name}: &7Unban player {name.",
		"&e/kick {name} {reason}: &7Kick player {name} for reason {reason}.",
		"&e/mute {name} {timeInMinutes} {reason}: &7Mute player {name} for reason {reason}.",
		"&e/unmute {name}: &7Unmute player {name}.",
		"&e/staff: &7Switch to staff chat channel.",
		"&e/tp {name}: &7Teleport to player.",
		"&e/promote {name}: &7Promote player (member - donator - mod - admin).",
		"&e/demote {name}: &7Demote player (admin - mod - donator - member).",
		"&e/identify {name}: &7Get info about player.",
		"&e/broadcast {message} :&7Broadcast a message to the whole server.",
		"&eNote: &7Roleplay names can be used in above commands except for /unban.",
	};
	private String playerhelp[] = {
		"&e/reset: &7Reset your character with a 24 - hour cooldown.",
		"&e/ooc: &7Switch to global out - of - character chat channel.",
		"&e/looc: &7Switch to local out - of - character chat channel.",
		"&e/local: &7Switch to local chat channel.",
		"&e/suicide: &7Kill yourself.",
		"&e/sit: &7Sit down right where you are at.",
		"&e/toggleooc: &7Toggle receiving chat from OOC channel.",
		"&e/msg: &7Message a player privately.",
		"&e/r: &7Reply to the last messenger.",
		"&e/coinflip: &7Flip a coin.",
		"&e/roll: &7Roll a dice.",
		"&e/desc: &7Set your character's description.",
		"&e/me {name}: &7Find out information about a certain online character.",
		"&e/spit: &7Spits.",
		
	};
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			for (String s: adminhelp) sender.sendMessage(colored(s));
			return true;
		}
		Player p = (Player) sender;
		String rank = plugin.getPlayerInfo(p).getRank();
		if (rank.equals("member") || rank.equals("donator")) {
			for (String s: playerhelp) p.sendMessage(colored(s));
		} else if (rank.equals("mod")) {
			for (String s: modhelp) p.sendMessage(colored(s));
			for (String s: playerhelp) p.sendMessage(colored(s));
		} else {
			for (String s: adminhelp) p.sendMessage(colored(s));
			for (String s: playerhelp) p.sendMessage(colored(s));
		}
		return true;
	}
}
