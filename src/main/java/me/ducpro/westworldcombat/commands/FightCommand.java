package me.ducpro.westworldcombat.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import me.ducpro.westworldcore.main.Main;

public class FightCommand implements CommandExecutor {
	private Main plugin;
	public FightCommand(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public class CountdownInteger {
		int countdown;
		public CountdownInteger(int countdown) {
			this.countdown = countdown;
		}
		public int getCountdown() {
			return countdown;
		}
		public void decreaseCountdown() {
			countdown--;
		}
		public String getStringCountdown() {
			return Integer.toString(countdown);
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please enter a player to fight with.");
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[0]), p = (Player) sender;
		if (!p.isOp()) {
			long lastFight = plugin.getData().getLong(p.getUniqueId(), plugin.getData().LAST_FIGHT, 0);
			final long HALF_HOUR_MILI = 1800000;
			if (System.currentTimeMillis() - lastFight < HALF_HOUR_MILI) {
				p.sendMessage(ChatColor.RED + "Your last fight was too recent. You can't fight again right now.");
				return true;
			}
		}
		if (!p.isOp() && plugin.getCombatList().isInCombat(p)) {
			p.sendMessage(ChatColor.RED + "You can't challenge a player while already in combat.");
			return true;
		}
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "The other player is not online!");
			return true;
		}
		if (target.getUniqueId().equals(p.getUniqueId())) {
			p.sendMessage(ChatColor.RED + "You cannot fight yourself!");
			return true;
		}
		if (!p.isOp()) {
			long lastSpawn = plugin.getData().getLong(target.getUniqueId(), plugin.getData().LAST_SPAWN, 0);
			long FIVE_MINUTES = 300000;
			if (System.currentTimeMillis() - lastSpawn < FIVE_MINUTES) {
				p.sendMessage(ChatColor.RED + "You can't fight this person yet. Let them live.");
				return true;
			}
		}
		if (!target.getWorld().getUID().equals(p.getWorld().getUID()) || target.getLocation().distanceSquared(p.getLocation()) > 900) {
			sender.sendMessage(ChatColor.RED + plugin.getPlayerInfo(target).getName() + " is too far away.");
			return true;
		}
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().LAST_FIGHT, System.currentTimeMillis());
		final String title = colored("&c&lFIGHT INITIATED");
		String pSubtitle = colored("&cYour fight with %name% will begin in 10 seconds!");
		pSubtitle = pSubtitle.replaceAll("%name%", plugin.getPlayerInfo(target).getName());
		String targetSubtitle = colored("&cYou've been challenged to a fight by %name%, 10 seconds before the fight begins!");
		targetSubtitle = targetSubtitle.replaceAll("%name%", plugin.getPlayerInfo(p).getName());
		p.sendTitle(title, colored("&c10"), 10, 70, 20);
		p.sendMessage(pSubtitle);
		target.sendTitle(title, colored("&c10"), 10, 70, 20);
		target.sendMessage(targetSubtitle);
		
		plugin.getCombatList().addPlayer(p);
		plugin.getCombatList().addPlayer(target);
		
		final UUID pUUID = p.getUniqueId(), targetUUID = target.getUniqueId();
		final CountdownInteger curCountdown = new CountdownInteger(10);
		new BukkitRunnable() {
			@Override
			public void run() {
				curCountdown.decreaseCountdown();
				Player finalp = Bukkit.getPlayer(pUUID);
				Player finaltarget = Bukkit.getPlayer(targetUUID);
				if (curCountdown.getCountdown() == 0) {
					if (finalp != null) plugin.getFightTeam().addPlayer(finalp);
					this.cancel();
					return;
				}
				if (finalp != null) {
					finalp.sendTitle(title, colored("&c" + curCountdown.getStringCountdown()), 0, 70, 20);
					finalp.playSound(finalp.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
				}
				if (finaltarget != null) {
					finaltarget.sendTitle(title, colored("&c" + curCountdown.getStringCountdown()), 0, 70, 20);
					finaltarget.playSound(finaltarget.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
				}
			}
		}.runTaskTimer(plugin, 20, 20);
		return true;
	}
}
