package me.ducpro.westworldintro.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class InfoNPC {
	private Main plugin;
	public InfoNPC(Main plugin) {
		this.plugin = plugin;
	}
	private final String[] npcmessages = {
		"&fOut of respect for the other guests we require you to stay in-character during the duration of your stay in the park, breaking "
		+ "the immersion will result in removal from the park. There’s a radio for you to speak freely, use that for "
		+ "any out of character conversations.",
		"&7Do /ooc and /local to toggle between local and global out-of-character chat channels.",
		"&fAnd absolutely no killing other guests for no reason and always allow them the chance to live when robbing them.",
		"&7For a full list of rules, visit the website or #info section of our Discord.",
		"&fBe sure to let the other guests know what you’re doing, give them a reason to start a conversation with you.",
		"&7Put a “*” before your message to make it an action.",
		"&fNow, are you ready to continue?",
		"&7Type *nods their head",
		"&fOkay, move along."
	};
	int curi;
	private String coloredMessage(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public void sendMessage(final Player p) {
		curi = 0;
		for (int i = 0; i < 8; i++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(coloredMessage(npcmessages[curi]));
					if (curi == 7) plugin.getPlayerInfo(p).requestingnod = true;
					curi++;
				}
			}, i * 60);
		}
	}
	public void sendLastMessage(final Player p) {
		p.sendMessage(coloredMessage(npcmessages[8]));
		plugin.getPlayerInfo(p).setStage(4);
		plugin.getData().setStage(p.getUniqueId(), 4);
	}
}
