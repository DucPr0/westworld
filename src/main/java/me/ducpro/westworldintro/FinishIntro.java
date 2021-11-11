package me.ducpro.westworldintro;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ducpro.nametagutils.ChangeName;
import me.ducpro.nametagutils.SetPrefix;
import me.ducpro.westworldcore.main.Main;

public class FinishIntro {
	private Main plugin;
	public FinishIntro(Main plugin) {
		this.plugin = plugin;
	}
	private void updatePrefix(Player p) {
		SetPrefix.setPrefix(plugin, plugin.getPlayerInfo(p).getRank(), p, plugin.getPlayerInfo(p).getPrefix());
	}
	public void finishIntro(Player p) {
		
		for (Player player: Bukkit.getOnlinePlayers()) player.showPlayer(plugin, p);
		
		String charactername = plugin.getPlayerInfo(p).getName();
//		NickPlugin.getPlugin().getAPI().nick(p, charactername, false, null, null);
//		NickPlugin.getPlugin().getAPI().addNameToScoreboard(charactername, plugin.getPlayerInfo(p).getRank(), 
//				plugin.getPlayerInfo(p).getPrefix());
		ChangeName.changeName(p, charactername, false, null, null);
		updatePrefix(p);
		p.setDisplayName(charactername);
		plugin.getData().addHistory(p.getUniqueId(), charactername);
		
		p.teleport(plugin.getConfigFile().getLocation("town-spawn"));
	}
}
