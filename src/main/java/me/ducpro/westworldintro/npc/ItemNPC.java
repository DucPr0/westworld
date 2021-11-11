package me.ducpro.westworldintro.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class ItemNPC {
	private Main plugin;
	public ItemNPC(Main plugin) {
		this.plugin = plugin;
	}
	private final String[] npcmessages = {
		"&fOkay, let’s get you set up with some gear. Here are twelve gold nuggets, the currency in Westworld. I’d "
		+ "recommend buying some food and getting a job the moment you enter the park otherwise you might find yourself struggling.",
		"&fYou’re also going to need to defend yourself. There’s some nasty creatures out there and even nastier people. "
		+ "Here’s a revolver, keep it safe."
	};
	private String coloredMessage(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public void sendMessage(final Player p) {
		p.sendMessage(coloredMessage(npcmessages[0]));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				p.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, 12));
			}
		}, 60);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.sendMessage(coloredMessage(npcmessages[1]));
			}
		}, 120);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				String command = "csp give weapon Navy %player%";
				command = command.replaceAll("%player%", p.getName());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				command = "mi tool PISTOL_AMMO %player% 16";
				command = command.replaceAll("%player%", p.getName());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			}
		}, 180);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.getPlayerInfo(p).setStage(3);
				plugin.getData().setStage(p.getUniqueId(), 3);
			}
		}, 180);
	}
}
