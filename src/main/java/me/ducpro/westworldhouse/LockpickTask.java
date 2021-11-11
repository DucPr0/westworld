package me.ducpro.westworldhouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.*;

import me.ducpro.westworldcore.main.Main;

public class LockpickTask {
	private Main plugin;
	private BukkitTask lockpickCountdown;
	public LockpickTask(Main plugin) {
		this.plugin = plugin;
		initLockpickCountdown();
	}
	private void initLockpickCountdown() {
		lockpickCountdown = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p: Bukkit.getOnlinePlayers()) {
					if (plugin.getPlayerInfo(p).getLockpickTime() == -1) continue; //Not wanted
					plugin.getPlayerInfo(p).decreaseLockpickTime();
					if (plugin.getPlayerInfo(p).getLockpickTime() == -1) {
						plugin.getLockpickTeam().removePlayer(p);
						if (!plugin.isWanted(p) && plugin.getPlayerInfo(p).wasWanted()) p.sendMessage(ChatColor.GREEN + "You are no longer wanted.");
						if (!plugin.isWanted(p)) plugin.getPlayerInfo(p).setWasWanted(false);
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
