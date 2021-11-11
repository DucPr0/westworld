package me.ducpro.westworldintro;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class BeginIntro {
	private Main plugin;
	private final long DAY_MILLISECONDS = 86400000;
	public BeginIntro(Main plugin) {
		this.plugin = plugin;
	}
	public void beginIntro(Player p, boolean bypass) {
		if (!bypass) {
			long last = plugin.getData().getLong(p.getUniqueId(), plugin.getData().CREATION, 0);
			if (System.currentTimeMillis() - last < DAY_MILLISECONDS) {
				p.sendMessage(ChatColor.RED + "You must wait 24 hours before creating a new character.");
				return;
			}
		}
		
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			ItemStack istack = p.getInventory().getItem(i);
			if (istack == null) continue;
			boolean soulbound = false;
			if (istack.hasItemMeta() && istack.getItemMeta().hasLore() && istack.getItemMeta().getLore().contains(ChatColor.AQUA + "Soulbound")) soulbound = true;
			if (!soulbound) p.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
		}
		
		p.setGameMode(GameMode.ADVENTURE);
		p.teleport(plugin.getConfigFile().getLocation("intro-spawn"));
		for (Player player: Bukkit.getOnlinePlayers()) if (!player.getUniqueId().equals(p.getUniqueId())) {
			player.hidePlayer(plugin, p);
		}
		
		plugin.getData().setStage(p.getUniqueId(), 1);
		plugin.getPlayerInfo(p).setStage(1);
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().CREATION, System.currentTimeMillis());
		
		plugin.getPlayerInfo(p).clearAllHorses();
		plugin.train.removePlayer(p, false);
		
		plugin.getData().setString(p.getUniqueId(), plugin.getData().DESC, null);
	}
}
