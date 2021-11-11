package me.ducpro.westworldheist.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldheist.BeginHeist;
import me.ducpro.westworldcore.main.Main;

public class VaultExplosion {
	private static int diffx[] = {-1, 0, 1, 0}, diffz[] = {0, -1, 0, 1};
	public static void createExplosion(Main plugin, Location explosive, Player p, String bankname) {
		ArrayList<Block> placeexplosives = new ArrayList<Block>();
		int vaultdiffx = 0, vaultdiffz = 0;
		placeexplosives.add(explosive.getBlock());
		for (int i = 0; i < 4; i++) {
			Location loc = explosive.clone();
			loc = loc.add(diffx[i], 0, diffz[i]);
			if (loc.getBlock().getType() == Material.BARRIER) {
				vaultdiffx = diffx[i];
				vaultdiffz = diffz[i];
			}
			ItemFrame frame = CheckFrame.getFrame(loc, 0.5);
			if (frame != null) {
				placeexplosives.add(loc.getBlock());
			}
		}
		ArrayList<Block> barriers = new ArrayList<Block>();
		Location curbarrier = explosive.clone().add(vaultdiffx, 0, vaultdiffz);
		ItemFrame doorframe = null;
		for (int diffx = -1; diffx <= 1; diffx++) {
			for (int diffy = -1; diffy <= 1; diffy++) {
				for (int diffz = -1; diffz <= 1; diffz++) {
					if (diffx != 0 && diffz != 0) continue;
					Location newloc = curbarrier.clone().add(diffx, diffy, diffz);
					Block block = newloc.getBlock();
					if (block.getType() == Material.AIR) {
						ItemFrame tmp = CheckFrame.getFrame(newloc, 0.5);
						if (tmp != null) {
							if (tmp.getItem().getType() == Material.DIAMOND_HOE) doorframe = tmp;
						}
					} else if (block.getType() == Material.BARRIER) barriers.add(block);
				}
			}
		}
		if (doorframe == null) {
			Bukkit.broadcastMessage(ChatColor.RED + "Item frame does not exist!");
			return;
		}
		for (Block block: barriers) {
			block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(), 2, false, false);
		}
		doorframe.setItem(new VaultDoors().getBrokenDoor());
		for (Block block: barriers) {
			block.setType(Material.AIR);
		}
		BeginHeist.beginHeist(plugin, p, bankname, doorframe, barriers, placeexplosives);
	}
}
