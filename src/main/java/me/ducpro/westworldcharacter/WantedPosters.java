package me.ducpro.westworldcharacter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.*;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldheist.utils.CheckFrame;

public class WantedPosters {
	private BukkitTask task;
	public WantedPosters(final Main plugin) {
		// TODO Auto-generated constructor stub
		task = new BukkitRunnable() {
			@Override
			public void run() {
				int maxBadThings = -1;
				Player wantedPlayer = null;
				
				for (Player p: Bukkit.getOnlinePlayers()) {
					int badThings = plugin.getData().getInt(p.getUniqueId(), plugin.getData().HEISTS, 0) + 
							plugin.getData().getInt(p.getUniqueId(), plugin.getData().KILLED, 0) + 
							plugin.getData().getInt(p.getUniqueId(), plugin.getData().LOCKPICKS, 0);
					if (badThings > maxBadThings) {
						maxBadThings = badThings;
						wantedPlayer = p;
					}
				}
				
				final String path = "wanted-posters";
				List<Location> locs = plugin.getConfigFile().getLocations(path);
				for (Location loc: locs) {
					ItemFrame e = CheckFrame.getFrame(loc, 0.5);
					if (e == null) continue;
					if (wantedPlayer == null) {
						e.setItem(new ItemStack(Material.AIR, 1), false);
						continue;
					}
					ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
					skullMeta.setDisplayName(plugin.getPlayerInfo(wantedPlayer).getName());
					skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(wantedPlayer.getUniqueId()));
					skull.setItemMeta(skullMeta);
					e.setItem(skull, false);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
