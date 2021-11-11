package me.ducpro.westworldheist.info;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldheist.utils.VaultDoors;

public class HeistInfo {
	private final int DURATION_SECONDS = 600;
	private Main plugin;
	private String bankname;
	private int secondsLeft;
	private Set<UUID> criminals = new HashSet<UUID>();
	private BukkitTask countdown;
	private ItemFrame doorframe;
	private List<Block> barriers;
	private List<Block> explosiveframes;
	private BossBar bossbar;
	public HeistInfo(Main plugin, String bankname, Player p, ItemFrame doorframe, List<Block> barriers, List<Block> explosiveframes) {
		this.doorframe = doorframe;
		this.barriers = barriers;
		this.explosiveframes = explosiveframes;
		secondsLeft = DURATION_SECONDS;
		this.plugin = plugin;
		this.bankname = bankname;
		criminals.add(p.getUniqueId());
		String bosstitle = ChatColor.RED + "" + ChatColor.BOLD + bankname.toUpperCase() + " BANK HEIST: 10M 00S";
		bossbar = Bukkit.createBossBar(bosstitle, BarColor.RED, BarStyle.SEGMENTED_10);
		bossbar.setProgress(1);
		countdown = new BukkitRunnable() {
			@Override
			public void run() {
				secondsLeft--;
				//Updating bossbar
				bossbar.setTitle(ChatColor.RED + "" + ChatColor.BOLD + HeistInfo.this.bankname.toUpperCase() + " BANK HEIST: " + convertToMinutes(secondsLeft));
				bossbar.setProgress((double) secondsLeft / DURATION_SECONDS);
				for (Player p: bossbar.getPlayers()) bossbar.removePlayer(p);
				List<Entity> nearbyplayers = HeistInfo.this.doorframe.getNearbyEntities(150, 100, 150);
				for (Entity entity: nearbyplayers) {
					if (entity instanceof Player) bossbar.addPlayer((Player) entity);
				}
				for (UUID uuid: criminals) {
					bossbar.addPlayer(Bukkit.getPlayer(uuid));
				}
				//Playing smoke effect
				int randombarrier = new Random().nextInt(HeistInfo.this.barriers.size());
				Block barrier = HeistInfo.this.barriers.get(randombarrier);
				barrier.getWorld().spawnParticle(Particle.LAVA, barrier.getLocation(), 2);
				barrier.getWorld().spawnParticle(Particle.FALLING_DUST, barrier.getLocation(), 2, Material.OAK_WOOD.createBlockData());
				//Ending a heist.
				if (criminals.size() == 0) {
					String realbankname = HeistInfo.this.bankname;
					realbankname = realbankname.replaceAll("_", " ");
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&lThe robbers of " + realbankname + " bank were all killed!"));
					heistEnd();
				}
				if (secondsLeft == 0) {
					String realbankname = HeistInfo.this.bankname;
					realbankname = realbankname.replaceAll("_", " ");
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eSeveral robbers in the " + realbankname + " heist "
							+ "have escaped."));
					heistEnd();
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	private String convertToMinutes(int seconds) {
		int min = seconds / 60, sec = seconds % 60;
		String stringmin = Integer.toString(min);
		String stringsec = Integer.toString(sec);
		if (stringmin.length() < 2) stringmin = "0" + stringmin;
		if (stringsec.length() < 2) stringsec = "0" + stringsec;
		return stringmin + "m " + stringsec + "s";
	}
	public void addCriminal(Player p) {
		criminals.add(p.getUniqueId());
	}
	public void removeCriminal(Player p) {
		criminals.remove(p.getUniqueId());
	}
	public void heistEnd() {
		countdown.cancel();
		for (UUID uuid: criminals) {
			Player p = Bukkit.getPlayer(uuid);
			plugin.getPlayerInfo(p).delRobbedBank(bankname);
			if (!plugin.getPlayerInfo(p).getRobStatus()) {
				plugin.getHeistTeam().removePlayer(p);
				p.sendTitle(ChatColor.GREEN + "You have escaped!", null, 10, 70, 20);
			}
		}
		plugin.heistinfo.remove(bankname);
		plugin.getConfigFile().setLastHeist(bankname, System.currentTimeMillis());
		doorframe.setItem(new VaultDoors().getNormalDoor());
		for (Block block : barriers) {
			block.setType(Material.BARRIER);
		}
		for (Player p: bossbar.getPlayers()) {
			bossbar.removePlayer(p);
		}
		for (Block block: explosiveframes) {
			ItemFrame iframe = (ItemFrame) block.getWorld().spawnEntity(block.getLocation(), EntityType.ITEM_FRAME);
			iframe.setItem(new VaultDoors().getPlaceExplosives());
		}
	}
}
