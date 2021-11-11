package me.ducpro.westworldcore;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.*;

import me.ducpro.westworldcore.main.*;
import net.md_5.bungee.api.ChatColor;

public class RepeatingTasks {
	private Main plugin;
	private BukkitTask countPlaytime = null;
	private BukkitTask combatExpire = null;
	private BukkitTask recoveryEnd = null;
	public RepeatingTasks(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
		initCountPlaytime();
		initCombatExpire();
		initRecoveryEnd();
	}
	private void initCountPlaytime() {
		countPlaytime = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p: Bukkit.getOnlinePlayers()) {
					plugin.getPlayerInfo(p).increasePlaytime();
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	private void initCombatExpire() {
		combatExpire = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p: Bukkit.getOnlinePlayers()) {
					if (plugin.getPlayerInfo(p).getCombatTime() == 0) continue; //Not in combat.
					plugin.getPlayerInfo(p).decreaseCombatTime();
					if (plugin.getPlayerInfo(p).getCombatTime() == 0) {
						plugin.getPlayerInfo(p).endCombat(p);
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	private String convert(int secondsLeft) {
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;
		return minutes + "m " + seconds + "s";
	}
	private void initRecoveryEnd() {
		recoveryEnd = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p: Bukkit.getOnlinePlayers()) {
					PlayerInfo pinfo = plugin.getPlayerInfo(p);
					if (pinfo.getRecoveryTime() == 0) continue;
					pinfo.decreaseRecoveryTime();
					if (pinfo.getRecoveryTime() == 0) {
						pinfo.endRecoveryCountdown(p);
					}
					int ogTime = Main.RESPAWN_SECONDS;
					if (pinfo.getInJail()) ogTime = Main.JAIL_SECONDS;
					pinfo.setBarProgress((double) pinfo.getRecoveryTime() / ogTime);
					String prefix = "&c&lRECOVERY";
					if (pinfo.getInJail()) {
						prefix = "&c&lJAIL TIME";
						pinfo.setBarStyle(BarStyle.SEGMENTED_10);
					}
					pinfo.setBarName(ChatColor.translateAlternateColorCodes('&', prefix + ": " + convert(pinfo.getRecoveryTime())));
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
