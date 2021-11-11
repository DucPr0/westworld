package me.ducpro.westworldcore.listeners;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.utils.GetRealName;
import net.md_5.bungee.api.ChatColor;

public class CommandEvents implements Listener {
	private Main plugin;
	public CommandEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		//Replace in - game name by playername.
		String command = e.getMessage();
		String[] parts = command.split(" ");
		if (parts[0].equalsIgnoreCase("/westcore") || parts[0].equalsIgnoreCase("/wc") || parts[0].equalsIgnoreCase("/identify")) return;
		if (parts[0].startsWith("/minecraft:")) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return;
			}
		}
		if (parts[0].equalsIgnoreCase("/reset")) {
			e.setCancelled(true);
			e.getPlayer().performCommand("westcorereset");
			return;
		}
		if (parts[0].equalsIgnoreCase("/me")) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return;
			}
		}
		String finalcommand = "";
		
		int i = 0;
		while (i < parts.length - 1) {
			String name = parts[i] + " " + parts[i + 1];
			String realname = GetRealName.getRealName(plugin, name);
			
			if (realname == null) {
				finalcommand += parts[i] + " ";
				i++;
			} else {
				finalcommand += realname + " ";
				i += 2;
			}
		}
		
		if (i == parts.length - 1) finalcommand += parts[parts.length - 1];
		e.setMessage(finalcommand);
	}
	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		List<String> completions = e.getCompletions();
		List<String> newcompletions = new ArrayList<String>();
		for (String s: completions) {
			if (Bukkit.getPlayerExact(s) == null) newcompletions.add(s);
			else {
				Player p = Bukkit.getPlayerExact(s);
				if (plugin.getPlayerInfo(p).getStage() <= Main.INTRO_STAGES) continue;
				String roleplayname = plugin.getPlayerInfo(p).getName();
				newcompletions.add(roleplayname);
			}
		}
		e.setCompletions(newcompletions);
	}
}
