package me.ducpro.westworldintro.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class HatNPC {
	private Main plugin;
	public HatNPC(Main plugin) {
		this.plugin = plugin;
	}
	private final String[] npcmessages = {
		"You’re moments away from your Westworld experience. But feel free to choose a hat in this room to take with you, choose "
		+ "carefully though cause you may only take one. Talk to me again once you’re done or if you’d prefer to go on without a hat.",
		"&fInteresting choice. You’re ready to enter the park. Follow the rules, be careful and try to enjoy yourself. "
		+ "Westworld is what you make of it. Walk down the hall behind me and go through the door to enter the park. Good luck.",
		"&7Right-click the door at the end of the carriage to enter the park. You’ll be dropped off in Sweetwater, the main town in "
		+ "Westworld."
	};
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	private int curi;
	public void sendFirstMessage(Player p) {
		p.sendMessage(npcmessages[0]);
	}
	public void sendMessage(final Player p) {
		curi = 0;
		for (int i = 0; i < 2; i++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(colored(npcmessages[curi + 1]));
					if (curi == 1) {
						plugin.getPlayerInfo(p).setStage(5);
						plugin.getData().setStage(p.getUniqueId(), 5);
					}
					curi++;
				}
			}, 60 * i);
		}
	}
}
