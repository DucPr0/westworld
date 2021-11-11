package me.ducpro.westworldhorse.utils;

import java.util.Random;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhorse.info.HorseInfo;

public class Utils {
	public static String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static int getPrice(String speed) {
		if (speed.equalsIgnoreCase("slow")) return 70;
		if (speed.equalsIgnoreCase("normal")) return 80;
		if (speed.equalsIgnoreCase("fast")) return 100;
		return -1;
	}
	public static Location getSpawnLocation(Main plugin, Location playerloc) {
		String spawnpointpath = "horse-spawnpoints";
		Set<String> spawnpoints = plugin.getConfigFile().getHorseSpawnpoints().getKeys(false);
		ArrayList<Location> list = new ArrayList<Location>();
		for (String s: spawnpoints) {
			if (s.equals("count")) continue;
			Location loc = plugin.getConfigFile().getLocation(spawnpointpath + "." + s);
			if (loc.distanceSquared(playerloc) <= 64) list.add(loc);
		}
		if (list.size() == 0) return playerloc;
		else {
			return list.get(new Random().nextInt(list.size()));
		}
	}
	public static void spawnHorse(Main plugin, String configID, Location spawnloc, Player p) {
		Horse horse = (Horse) spawnloc.getWorld().spawnEntity(spawnloc, EntityType.HORSE);
		String speed = plugin.getData().getHorseSpeed(p.getUniqueId(), configID);
		
		plugin.getPlayerInfo(p).spawnHorse(horse);
		plugin.horseinfo.put(horse.getUniqueId(), new HorseInfo(horse, p.getUniqueId(), speed, configID));
		
		String name = plugin.getData().getHorseName(p.getUniqueId(), configID);
		name = name.replaceAll("%roleplayname%", plugin.getPlayerInfo(p).getName());
		Horse.Color horsecolor = Horse.Color.valueOf(plugin.getData().getHorseColor(p.getUniqueId(), configID));
		Horse.Style horsestyle = Horse.Style.valueOf(plugin.getData().getHorseStyle(p.getUniqueId(), configID));
		
		horse.setCustomNameVisible(true);
		horse.setCustomName(name);
		horse.setColor(horsecolor);
		horse.setStyle(horsestyle);
		horse.setTamed(true);
		horse.setAI(false);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
		horse.setAdult();
		horse.setRemoveWhenFarAway(false);
		
		double horsespeed = plugin.getConfigFile().getDouble(speed);
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(horsespeed);
	}
}