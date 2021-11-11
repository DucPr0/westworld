package me.ducpro.westworldcore.files;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;

import me.ducpro.westworldcore.main.Main;

public class PermHandler {
	private Main plugin;
	File memberPerm, donatorPerm;
	FileConfiguration memberConfig, donatorConfig;
	public PermHandler(Main plugin) {
		this.plugin = plugin;
		plugin.saveResource("members.yml", false);
		plugin.saveResource("donators.yml", false);
		memberPerm = new File(plugin.getDataFolder(), "members.yml");
		donatorPerm = new File(plugin.getDataFolder(), "donators.yml");
		memberConfig = YamlConfiguration.loadConfiguration(memberPerm);
		donatorConfig = YamlConfiguration.loadConfiguration(donatorPerm);
	}
	void saveFile(FileConfiguration config, File file) {
		try {
			config.save(file);
		} catch (Exception e) {
			Bukkit.getLogger().info("There's an error with saving a permissions file.");
		}
	}
	public void savePerm(String rank) {
		if (rank.equals("member")) saveFile(memberConfig, memberPerm);
		if (rank.equals("donator")) saveFile(donatorConfig, donatorPerm);
	}
	public List<String> getPerms(String rank) {
		if (rank.equals("member")) return memberConfig.getStringList("perms");
		if (rank.equals("donator")) return donatorConfig.getStringList("perms");
		return null;
	}
	public void setPerms(String rank, List<String> newPerms) {
		if (rank.equals("member")) memberConfig.set("perms", newPerms);
		if (rank.equals("donator")) donatorConfig.set("perms", newPerms);
		savePerm(rank);
	}
	public void addPerm(String rank, String perm) {
		List<String> curPerms = getPerms(rank);
		curPerms.add(perm);
		setPerms(rank, curPerms);
	}
	public void removePerm(String rank, String perm) {
		List<String> curPerms = getPerms(rank);
		curPerms.remove(perm);
		setPerms(rank, curPerms);
	}
}
