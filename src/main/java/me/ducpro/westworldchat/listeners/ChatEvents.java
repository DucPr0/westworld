package me.ducpro.westworldchat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import me.ducpro.westworldchat.Utils;
import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.PlayerInfo;

public class ChatEvents implements Listener {
	private Main plugin;
	public ChatEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) return;
		Player p = e.getPlayer();
		if (p.hasMetadata("BlackJackTable")) {
			Plugin owningplugin = p.getMetadata("BlackJackTable").get(0).getOwningPlugin();
			p.removeMetadata("BlackJackTable", owningplugin);
			e.setCancelled(true);
			final Player finalp = e.getPlayer();
			final boolean originallyop = finalp.isOp();
			final String message = e.getMessage();
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					try {
						finalp.setOp(true);
						finalp.performCommand("bj bet " + message);
					} finally {
						if (!originallyop) finalp.setOp(false);
					}
					return;
				}
			});
			return;
		}
		PlayerInfo pinfo = plugin.getPlayerInfo(p);
		if (pinfo.getStage() > Main.INTRO_STAGES) {
			
			e.setCancelled(true);
			//Detect mutes
			if (System.currentTimeMillis() < plugin.getData().getLong(p.getUniqueId(), plugin.getData().MUTE, 0) ||
					plugin.getData().getLong(p.getUniqueId(), plugin.getData().MUTE, 0) == -1) {
				p.sendMessage(ChatColor.RED + "You are currently muted.");
				return;
			}
			
			String msg = e.getMessage().trim();
			if (msg.charAt(0) != '*') {
				if (pinfo.getChatMode() == 0) {
					String quote = "\"";
					String pref = plugin.getPlayerInfo(p).getPrefix(), name = plugin.getPlayerInfo(p).getName();
					String fullmsg = pref + "&f" + name + ": " + quote + msg + "&r&f" + quote;
					fullmsg = Utils.getMessage(plugin, p, fullmsg);
					fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
					
					Bukkit.getLogger().info(fullmsg);
					p.sendMessage(fullmsg);
					for (Entity entity: p.getNearbyEntities(20, 20, 20)) {
						if (entity instanceof Player) {
							entity.sendMessage(fullmsg);
						}
					}
				} else if (pinfo.getChatMode() == 1) {
					String pref = plugin.getPlayerInfo(p).getPrefix(), name = plugin.getPlayerInfo(p).getName();
					String fullmsg = "&b(OOC) " + pref + "&b" + name + ": " + msg;
					fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
					
					Bukkit.getLogger().info(fullmsg);
					for (Player online: Bukkit.getOnlinePlayers()) {
						if (plugin.getPlayerInfo(online).getListenOOC()) online.sendMessage(fullmsg);
					}
				} else if (pinfo.getChatMode() == 2) {
					String fullmsg = "&c(STAFF) %prefix%&c%name%: %message%";
					fullmsg = fullmsg.replace("%name%", pinfo.getName()).replace("%message%", e.getMessage()).replace("%prefix%", pinfo.getPrefix());
					fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
					
					for (Player online: Bukkit.getOnlinePlayers()) {
						String rank = plugin.getPlayerInfo(online).getRank();
						if (rank.equals("mod") || rank.equals("admin")) online.sendMessage(fullmsg);
					}
				} else if (pinfo.getChatMode() == 3) {
					String pref = plugin.getPlayerInfo(p).getPrefix(), name = plugin.getPlayerInfo(p).getName();
					String fullmsg = "&b(OOC) " + pref + "&b" + name + ": " + msg;
					fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
					
					Bukkit.getLogger().info(fullmsg);
					p.sendMessage(fullmsg);
					for (Entity entity: p.getNearbyEntities(30, 30, 30)) {
						if (entity instanceof Player) {
							Player target = (Player) entity;
							if (plugin.containsPlayer(target.getUniqueId()) && plugin.getPlayerInfo(target).getListenOOC()) target.sendMessage(fullmsg);
						}
					}
				}
			} else {
				msg = msg.substring(1);
				String fullmsg = ChatColor.YELLOW + "" + ChatColor.ITALIC + "%name% " + msg;
				fullmsg = fullmsg.replace("%name%", pinfo.getName());
				
				Bukkit.getLogger().info(fullmsg);
				p.sendMessage(fullmsg);
				for (Entity entity: p.getNearbyEntities(20, 20, 20)) {
					if (entity instanceof Player) {
						entity.sendMessage(fullmsg);
					}
				}
			}
		}
	}
}
