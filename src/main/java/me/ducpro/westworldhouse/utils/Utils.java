package me.ducpro.westworldhouse.utils;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

import me.ducpro.westworldcore.main.Main.Coordinates;

public class Utils {
	public static final int DAY_SECONDS = 86400; //86400
	public static final int DAY_HOURS = 24;
	public static final int DEFAULT_SELL_PERCENT = 50;
	public static boolean isDoor(Material mat) {
		return (mat == Material.BIRCH_DOOR) || (mat == Material.SPRUCE_DOOR) || (mat == Material.JUNGLE_DOOR) ||
				(mat == Material.OAK_DOOR);
	}
	public static int getPrice(Material mat) {
		if (mat == Material.BIRCH_DOOR) return 500;
		if (mat == Material.SPRUCE_DOOR) return 190;
		if (mat == Material.JUNGLE_DOOR) return 100;
		if (mat == Material.OAK_DOOR) return 250;
		return -1;
	}
	private static int getChance(Material mat) {
		if (mat == Material.BIRCH_DOOR) return 5;
		if (mat == Material.SPRUCE_DOOR) return 40;
		if (mat == Material.JUNGLE_DOOR) return 60;
		if (mat == Material.OAK_DOOR) return 20;
		return -1;
	}
	public static boolean lockPicked(Material mat) {
		Random r = new Random();
		int num = r.nextInt(100);
		if (num < getChance(mat)) return true;
		return false;
	}
	public static int getSellPrice(Material mat, int secondsleft) {
		int hoursleft = secondsleft / 3600;
		int hourspassed = DAY_HOURS - hoursleft;
		int percentleft = DEFAULT_SELL_PERCENT - hourspassed;
		int sellPrice = (getPrice(mat) * percentleft) / 100;
		return sellPrice;
	}
	public static String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static Coordinates getLowerDoor(Block b) {
		Door door = (Door) b.getState().getData();
		
		Coordinates lower = new Coordinates(b);
		Coordinates lower2 = new Coordinates(b);
		lower2.add(0, -1, 0);
		
		if (isDoor(lower2.getWorld().getBlockAt(lower2.getLocation()).getType())) {
			return lower2;
		}
		return lower;
	}
}
