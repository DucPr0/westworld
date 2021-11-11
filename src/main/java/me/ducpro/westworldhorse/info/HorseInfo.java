package me.ducpro.westworldhorse.info;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;

import me.ducpro.westworldcore.main.Main;

public class HorseInfo {
	private Horse horse;
	private UUID owner;
	private String speed, id;
	private boolean isRunningAway;
	public HorseInfo(Horse horse, UUID owner, String speed, String id) {
		this.horse = horse;
		this.owner = owner;
		this.speed = speed;
		this.id = id;
		isRunningAway = false;
	}
	public HorseInfo(final Main plugin, Horse horse, boolean isRunningAway) {
		this.horse = horse;
		this.isRunningAway = isRunningAway;
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.horseinfo.remove(HorseInfo.this.horse.getUniqueId());
				HorseInfo.this.horse.remove();
			}
		}, 80);
	}
	public boolean isRunning() {
		return isRunningAway;
	}
	public Horse getHorse() {
		return horse;
	}
	public UUID getOwner() {
		return owner;
	}
	public String getSpeed() {
		return speed;
	}
	public String getConfigID() {
		return id;
	}
}
