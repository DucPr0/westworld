package me.ducpro.westworldintro.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class NameAgeNPC {
	private Main plugin;
	public NameAgeNPC(Main plugin) {
		this.plugin = plugin;
	}
	private final String[] npcmessages = {
		"&fWelcome %username%, to Westworld. You’re about to enter an immersive 1800’s old west experience, but first we require "
		+ "some basic information about you. What name will you go by in the park?",
		"&7Please enter a first and last name in chat. Inappropriate names will have you permanently removed from the park.",
		"&fPerfect. Now, how old will you be?",
		"&7Please enter an age between 21-40.",
		"&fBrilliant, please continue onto the next room"
	};
	private String coloredMessage(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public void sendMessage(final Player p, final int id) {
		if (id > 0) p.sendMessage(coloredMessage(npcmessages[id]));
		else p.sendMessage(coloredMessage(npcmessages[id]).replaceAll("%username%", p.getName()));
		if (id < 4) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(coloredMessage(npcmessages[id + 1]));
				}
			}, 60);
		}
		if (id == 0) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.getPlayerInfo(p).askingname = true;
				}
			}, 60);
		} else if (id == 2) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.getPlayerInfo(p).askingage = true;
				}
			}, 60);
		} else {
			plugin.getPlayerInfo(p).askingage = false;
			String name = plugin.getPlayerInfo(p).getName();
			int age = plugin.getPlayerInfo(p).getAge();
			plugin.getData().setPlayerData(p.getUniqueId(), name, age);
			plugin.getPlayerInfo(p).setStage(2);
			plugin.getData().setStage(p.getUniqueId(), 2);
			//Set the stage to 2 for equipment manager NPC
		}
	}
}
