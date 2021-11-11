package me.ducpro.westworldcharacter.commands;

import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class DescCommand {
	public static void setDesc(final Main plugin, Player p, String args[]) {
		String desc = "";
		for (int i = 0; i < args.length; i++) {
			desc += args[i];
			if (i < args.length - 1) desc += " ";
		}
		plugin.getData().setString(p.getUniqueId(), plugin.getData().DESC, desc);
		p.sendMessage(ChatColor.GREEN + "Successfully changed description.");
	}
}
