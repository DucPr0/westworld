package me.ducpro.westworldcore.files;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Horse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.*;

import me.ducpro.westworldcore.main.Main;

public class DataHandler {
	private Main plugin;
	private File folder;
	public DataHandler(Main plugin) {
		this.plugin = plugin;
	}
	public void load() {
		folder = new File(plugin.getDataFolder(), "playerdata");
		if (!folder.exists()) folder.mkdir();
	}
	private File getFile(UUID uuid) {
		return new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
	}
	private FileConfiguration getConfiguration(UUID uuid) {
		return YamlConfiguration.loadConfiguration(getFile(uuid));
	}
	private void saveFile(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createPlayerFile(UUID uuid) {
		File file = getFile(uuid);
		FileConfiguration config = getConfiguration(uuid);
		saveFile(file, config);
	}
	public boolean fileExist(UUID uuid) {
		File playerfile = getFile(uuid);
		return playerfile.exists();
	}
	public int getStage(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getInt("creatingstage");
	}
	public String getName(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (config.contains("name")) return config.getString("name");
		else return null;
	}
	public Integer getAge(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (config.contains("age")) return config.getInt("age");
		else return null;
	}
	public void setStage(UUID uuid, int stage) {
		FileConfiguration config = getConfiguration(uuid);
		config.set("creatingstage", stage);
		saveFile(getFile(uuid), config);
	}
	public void setPlayerData(UUID uuid, String name, int age) {
		File file = getFile(uuid);
		FileConfiguration config = getConfiguration(uuid);
		config.set("name", name);
		config.set("age", age);
		addHistory(uuid, name);
		saveFile(file, config);
	}
	private final List<String> emptylist = new ArrayList<String>();
	public List<String> getHistory(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains("history")) {
			config.set("history", emptylist);
		}
		return config.getStringList("history");
	}
	public void addHistory(UUID uuid, String name) {
		FileConfiguration config = getConfiguration(uuid);
		List<String> list = getHistory(uuid);
		list.add(name);
		config.set("history", list);
		saveFile(getFile(uuid), config);
	}
	//horses.yml exclusive
	public boolean canHaveHorse(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains("horses")) {
			config.createSection("horses");
			config.set("horses.horse-count", 0);
		}
		ConfigurationSection horsesection = config.getConfigurationSection("horses");
		int HORSE_LIMIT = Main.PLAYER_HORSE_LIMIT;
		String rank = getRank(uuid);
		if (rank.equals("donator") || rank.equals("mod")) HORSE_LIMIT = Main.DONATOR_HORSE_LIMIT;
		if (rank.equals("admin")) return true;
		if (horsesection.getKeys(false).size() >= HORSE_LIMIT + 1) return false;
		return true;
	}
	public String addHorse(UUID uuid, Horse e, String speed) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains("horses")) config.createSection("horses");
		ConfigurationSection horsesection = config.getConfigurationSection("horses");
		if (!horsesection.contains("horse-count")) horsesection.set("horse-count", 0);
		int id = horsesection.getInt("horse-count");
		id++;
		horsesection.set("horse-count", id);
		ConfigurationSection newhorse = horsesection.createSection("horse" + id);
		newhorse.set("name", "%roleplayname%'s horse");
		newhorse.set("color", e.getColor().toString());
		newhorse.set("style", e.getStyle().toString());
		newhorse.set("speed", speed);
		saveFile(getFile(uuid), config);
		return "horse" + id;
	}
	public void delHorse(UUID uuid, String configID) {
		FileConfiguration config = getConfiguration(uuid);
		ConfigurationSection horsesection = config.getConfigurationSection("horses");
		horsesection.set(configID, null);
		saveFile(getFile(uuid), config);
	}
	public void renameHorse(UUID uuid, String configID, String name) {
		FileConfiguration config = getConfiguration(uuid);
		ConfigurationSection horsesection = config.getConfigurationSection("horses");
		horsesection.set(configID + ".name", name);
		saveFile(getFile(uuid), config);
	}
	public ConfigurationSection getHorseList(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains("horses")) {
			config.createSection("horses");
			config.set("horses.horse-count", 0);
		}
		return config.getConfigurationSection("horses");
	}
	public String getHorseSpeed(UUID uuid, String configID) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getString("horses." + configID + ".speed");
	}
	public String getHorseName(UUID uuid, String configID) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getString("horses." + configID + ".name");
	}
	public String getHorseColor(UUID uuid, String configID) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getString("horses." + configID + ".color");
	}
	public String getHorseStyle(UUID uuid, String configID) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getString("horses." + configID + ".style");
	}
	//Playerdata system.
	public String getRank(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains("rank")) {
			config.set("rank", "member");
			saveFile(getFile(uuid), config);
		}
		return config.getString("rank");
	}
	public void setRank(UUID uuid, String rank) {
		FileConfiguration config = getConfiguration(uuid);
		config.set("rank", rank);
		saveFile(getFile(uuid), config);
	}
	public void addMute(UUID uuid, String reason) {
		FileConfiguration config = getConfiguration(uuid);
		List<String> list = config.getStringList("mutes");
		list.add(reason);
		config.set("mutes", list);
		saveFile(getFile(uuid), config);
	}
	public void addBan(UUID uuid, String reason) {
		FileConfiguration config = getConfiguration(uuid);
		List<String> list = config.getStringList("bans");
		list.add(reason);
		config.set("bans", list);
		saveFile(getFile(uuid), config);
	}
	public void addKick(UUID uuid, String reason) {
		FileConfiguration config = getConfiguration(uuid);
		List<String> list = config.getStringList("kicks");
		list.add(reason);
		config.set("kicks", list);
		saveFile(getFile(uuid), config);
	}
	public List<String> getMutes(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getStringList("mutes");
	}
	public List<String> getBans(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getStringList("bans");
	}
	public List<String> getKicks(UUID uuid) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getStringList("kicks");
	}
	public final static String LAST_LOCKPICK = "lastlockpick", LAST_FIGHT = "lastfight", LAST_SPAWN = "lastspawn", MUTE = "currentmute",
			RECOVERY = "recoveryEnd", JAIL = "in-jail", CREATION = "lastcreation", DESC = "description", KILLED = "killed", 
			HEISTS = "heists", PLAYTIME = "playtime", LOCKPICKS = "lockpicks";
	public void setInt(UUID uuid, String path, int val) {
		FileConfiguration config = getConfiguration(uuid);
		config.set(path, val);
		saveFile(getFile(uuid), config);
	}
	public int getInt(UUID uuid, String path, int defaultValue) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains(path)) {
			config.set(path, defaultValue);
			saveFile(getFile(uuid), config);
		}
		return config.getInt(path);
	}
	public void setLong(UUID uuid, String path, long val) {
		FileConfiguration config = getConfiguration(uuid);
		config.set(path, val);
		saveFile(getFile(uuid), config);
	}
	public long getLong(UUID uuid, String path, long defaultValue) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains(path)) {
			config.set(path, defaultValue);
			saveFile(getFile(uuid), config);
		}
		return config.getLong(path);
	}
	public void setBoolean(UUID uuid, String path, boolean val) {
		FileConfiguration config = getConfiguration(uuid);
		config.set(path, val);
		saveFile(getFile(uuid), config);
	}
	public boolean getBoolean(UUID uuid, String path, boolean defaultValue) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains(path)) {
			config.set(path, defaultValue);
			saveFile(getFile(uuid), config);
		}
		return config.getBoolean(path);
	}
	public void setString(UUID uuid, String path, String val) {
		FileConfiguration config = getConfiguration(uuid);
		config.set(path, val);
		saveFile(getFile(uuid), config);
	}
	public String getString(UUID uuid, String path, String defaultValue) {
		FileConfiguration config = getConfiguration(uuid);
		if (!config.contains(path)) {
			config.set(path, defaultValue);
			saveFile(getFile(uuid), config);
		}
		return config.getString(path);
	}
}
