package me.ducpro.westworldtrain;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldtrain.utils.Utils;

public class TrainManager {
	private Main plugin;
	private BossBar bar;
	private String curStationName;
	private boolean atStation;
	private int seconds;
	private BukkitTask countdown;
	private final int MOVE_SECONDS = 30, STAY_SECONDS = 16;
	public TrainManager(Main plugin) {
		this.plugin = plugin;
		bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		curStationName = null;
		atStation = true;
		seconds = 0;
		final Main finalplugin = plugin;
		countdown = new BukkitRunnable() {
			@Override
			public void run() {
				if (seconds == 0) {
					if (!atStation) {
						bar.setProgress(1);
						seconds = STAY_SECONDS;
						atStation = true;
					} else {
						curStationName = Utils.getNextStation(finalplugin, finalplugin.getConfigFile().getOrder(curStationName));
						bar.setProgress(1);
						seconds = MOVE_SECONDS;
						atStation = false;
					}
				} else seconds--;
				if (!atStation) {
					bar.setProgress((double) seconds / MOVE_SECONDS);
					String bartitle = Utils.colored("&eTravelling to %townname%: &7Arriving in %seconds%s.");
					bartitle = bartitle.replaceAll("%townname%", curStationName.replaceAll("_", " ")).replaceAll("%seconds%", seconds + "");
					bar.setTitle(bartitle);
				} else {
					bar.setProgress((double) seconds / STAY_SECONDS);
					String bartitle = Utils.colored("&aCurrently at %townname% station: &7Leaving in %seconds%s.");
					bartitle = bartitle.replaceAll("%townname%", curStationName.replaceAll("_", " ")).replaceAll("%seconds%", seconds + "");
					bar.setTitle(bartitle);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	public boolean isAtStation() {
		return atStation;
	}
	public String getStation() {
		return curStationName;
	}
	public void addPlayer(Player p) {
		Location trainspawn = plugin.getConfigFile().getLocation("train-spawn");
		p.teleport(trainspawn);
		bar.addPlayer(p);
	}
	public void removePlayer(Player p, boolean getOff) {
		if (!bar.getPlayers().contains(p)) return; 
		if (getOff) {
			Location station = plugin.getConfigFile().getLocation("trains." + curStationName);
			p.teleport(station);
			p.sendMessage(ChatColor.GREEN + "You have arrived at " + curStationName.replaceAll("_", " ") + " station");
		} else {
			String lasttown = plugin.getPlayerInfo(p).getLastTown();
			p.teleport(plugin.getConfigFile().getLocation("trains." + lasttown));
			//Train is either stopped or player quits the game. Teleport back to last town.
		}
		bar.removePlayer(p);
		plugin.getPlayerInfo(p).setLastTown(null); //Set last town back to null.
	}
	public void stopTrain() {
		List<Player> allPlayers = bar.getPlayers();
		for (Player p: allPlayers) {
			removePlayer(p, false);
			p.sendMessage(ChatColor.RED + "The train has been stopped.");
		}
		countdown.cancel();
	}
}
