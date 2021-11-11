package me.ducpro.nametagutils;

import org.bukkit.entity.Player;

import net.haoshoku.nick.NickPlugin;

public class ChangeName {
	public static void changeName(Player p, String newName, boolean skinChange, String texture, String sig) {
		NickPlugin.getPlugin().getAPI().nick(p, newName, skinChange, texture, sig);
	}
}
