package me.ducpro.nametagutils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

import net.haoshoku.nick.NickPlugin;

public class SetPrefix {
	public static void setPrefix(Main plugin,String teamName, Player p, String prefix) {
		NickPlugin.getPlugin().getAPI().addNameToScoreboard(plugin.getPlayerInfo(p).getName(), teamName, prefix);
	}
	public static void setPrefix(Main plugin, String teamName, String nickName, String prefix) {
		NickPlugin.getPlugin().getAPI().addNameToScoreboard(nickName, teamName, prefix);
	}
}
