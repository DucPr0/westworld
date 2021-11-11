package me.ducpro.westworldcore;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import me.ducpro.nametagutils.SetPrefix;
import me.ducpro.westworldcore.main.Main;

public class WantedTeam {
	private Main plugin;
	private HashSet<UUID> wanted;
	public WantedTeam(Main plugin) {
		this.plugin = plugin;
		wanted = new HashSet<UUID>();
	}
	public void addPlayer(Player p) {
		String curname = plugin.getPlayerInfo(p).getName();
//		NickPlugin.getPlugin().getAPI().addNameToScoreboard(curname, "Criminal", ChatColor.RED + "Wanted ");
		SetPrefix.setPrefix(plugin, "Criminal", p, ChatColor.RED + "Wanted ");
		wanted.add(p.getUniqueId());
		plugin.getPlayerInfo(p).setWasWanted(true);
	}
	public void removePlayer(Player p) {
		String curname = plugin.getPlayerInfo(p).getName();
//		NickPlugin.getPlugin().getAPI().removeNameFromScoreboard(curname, "Criminal");
//		NickPlugin.getPlugin().getAPI().addNameToScoreboard(curname, plugin.getPlayerInfo(p).getRank(), plugin.getPlayerInfo(p).getPrefix());
		SetPrefix.setPrefix(plugin, plugin.getPlayerInfo(p).getRank(), p, plugin.getPlayerInfo(p).getPrefix());
		wanted.remove(p.getUniqueId());
	}
	public void removeUUIDFromList(UUID uuid) {
		wanted.remove(uuid);
	}
	public boolean isInTeam(Player p) {
		return wanted.contains(p.getUniqueId());
	}
}
