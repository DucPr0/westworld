package me.ducpro.westworldhouse.info;

import java.util.HashSet;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldhouse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class DoorInfo {
	private Main plugin;
	World world;
	int x, y, z;
	Material type;
	UUID owner;
	int secondsleft;
	private BukkitTask countdown;
	private HashSet<UUID> allowed;
	private long lastlockpicked = -1;
	public DoorInfo(Block b, int secondsleft, UUID owner, Main plugin) {
		world = b.getWorld();
		x = b.getLocation().getBlockX();
		y = b.getLocation().getBlockY();
		z = b.getLocation().getBlockZ();
		type = b.getType();
		this.secondsleft = secondsleft;
		this.owner = owner;
		allowed = new HashSet<UUID>();
		this.plugin = plugin;
		if (plugin.containsPlayer(owner)) plugin.getPlayerInfo(owner).increaseDoors();
		initCountdown();
	}
	public DoorInfo(World world, int x, int y, int z, Material type, int secondsleft, UUID owner, Main plugin) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.secondsleft = secondsleft;
		this.owner = owner;
		allowed = new HashSet<UUID>();
		this.plugin = plugin;
		if (plugin.containsPlayer(owner)) plugin.getPlayerInfo(owner).increaseDoors();
		initCountdown();
	}
	private final int DAY_SECONDS = 86400;
	private final int HOUR_SECONDS = 3600;
	private final int MINUTE_SECONDS = 60;
	public String convertTimeLeft(int timeleft) {
		int days = timeleft / DAY_SECONDS;
		int hours = (timeleft - DAY_SECONDS * days) / HOUR_SECONDS;
		int minutes = (timeleft - DAY_SECONDS * days - HOUR_SECONDS * hours) / MINUTE_SECONDS;
		int seconds = timeleft - DAY_SECONDS * days - HOUR_SECONDS * hours - MINUTE_SECONDS * minutes;
		String display = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
		return display;
	}
	public World getWorld() {
		return world;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public Coordinates getCoords() {
		return new Coordinates(world, x, y, z);
	}
	public int getSecondsLeft() {
		return secondsleft;
	}
	public void addSecondsLeft(int add) {
		secondsleft += add;
	}
	public void initCountdown() {
		countdown = new BukkitRunnable() {
			@Override
			public void run() {
				if (DoorInfo.this.secondsleft == 0) {
					rentEnd(true);
				}
				DoorInfo.this.secondsleft--;
				if (plugin.containsPlayer(DoorInfo.this.owner)) {
					Coordinates coord = plugin.getPlayerInfo(DoorInfo.this.owner).getDoor();
					if (coord == null) return;
					if (coord.getBlock().getX() == DoorInfo.this.getX() && coord.getBlock().getY() == DoorInfo.this.getY() &&
							coord.getBlock().getZ() == DoorInfo.this.getZ() && coord.getWorld().getUID().equals(DoorInfo.this.getWorld().getUID())) {
						Player p = Bukkit.getPlayer(DoorInfo.this.getOwner());
						Inventory inv = p.getOpenInventory().getTopInventory();
						if (inv != null && inv.getSize() == 9 && inv.getName().equals(ChatColor.WHITE + "Configuring")) {
							ItemStack doorstack = inv.getItem(7);
							ItemMeta meta = doorstack.getItemMeta();
							meta.setDisplayName(Utils.colored("&fYour rent will last: " + convertTimeLeft(DoorInfo.this.secondsleft)));
							doorstack.setItemMeta(meta);
							inv.setItem(7, doorstack);
						}
						
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	public List<String> getAllowed() {
		List<String> list = new ArrayList<String>();
		for (UUID uuid: allowed) list.add(uuid.toString());
		return list;
	}
	public void rentEnd(boolean sendMessage) {
		countdown.cancel();
		if (plugin.containsPlayer(owner)) {
			plugin.getPlayerInfo(owner).decreaseDoors();
			if (sendMessage) Bukkit.getPlayer(owner).sendMessage(ChatColor.YELLOW + "One of your rents have expired.");
		}
		plugin.doorinfo.remove(new Coordinates(world, x, y, z));
	}
	public UUID getOwner() {
		return owner;
	}
	public Material getType() {
		return type;
	}
	public void setAllowed(HashSet<UUID> set) {
		allowed = set;
	}
	public void addPlayer(UUID uuid) {
		allowed.add(uuid);
	}
	public void removePlayer(UUID uuid) {
		allowed.remove(uuid);
	}
	public boolean isAllowed(UUID uuid) {
		return (uuid.equals(owner)) || (allowed.contains(uuid));
	}
	public void setLastLockpicked(long val) {
		lastlockpicked = val;
	}
	public boolean canInteract(long val) {
		long tmp = val - lastlockpicked;
		if (tmp < 30000) return false;
		return true;
	}
}
